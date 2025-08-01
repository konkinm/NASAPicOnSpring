package space.maxkonkin.nasapicbot.repository

import org.slf4j.LoggerFactory
import tech.ydb.auth.iam.CloudAuthHelper
import tech.ydb.core.grpc.GrpcTransport
import tech.ydb.table.SessionRetryContext
import tech.ydb.table.TableClient
import tech.ydb.table.query.DataQueryResult
import tech.ydb.table.query.Params
import tech.ydb.table.transaction.TxControl
import java.util.function.Consumer

class EntityManager(private val database: String, private val endpoint: String) {
    fun execute(query: String, params: Params, callback: Consumer<DataQueryResult>?) {
        logger.debug("Authentication via environ...")
        val authProvider = CloudAuthHelper.getAuthProviderFromEnviron()
        logger.debug("Creating GrpcTransport...")
        try {
            GrpcTransport.forEndpoint(endpoint, database)
                .withAuthProvider(authProvider)
                .build().use { transport ->
                    logger.debug("Creating TableClient...")
                    TableClient.newClient(transport).build().use { tableClient ->
                        logger.debug("Creating SessionRetryContext...")
                        val retryCtx = SessionRetryContext.create(tableClient).build()
                        logger.debug("Configuring TxControl...")
                        val txControl = TxControl.serializableRw().setCommitTx(true)
                        logger.debug("Executing DataQuery...")
                        val result = retryCtx.supplyResult { session ->
                            session
                                .executeDataQuery(query, txControl, params)
                        }
                            .join().value
                        if (!result.isEmpty) {
                            logger.info("Result set obtained!")
                        }
                        callback?.accept(result)
                    }
                }
        } catch (e: Exception) {
            logger.error(e.message)
        }
    }

    fun execute(query: String, params: Params) {
        execute(query, params, null)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(EntityManager::class.java)
    }
}
