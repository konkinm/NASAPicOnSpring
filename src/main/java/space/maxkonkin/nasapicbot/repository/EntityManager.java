package space.maxkonkin.nasapicbot.repository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.ydb.auth.AuthProvider;
import tech.ydb.auth.iam.CloudAuthHelper;
import tech.ydb.core.grpc.GrpcTransport;
import tech.ydb.table.SessionRetryContext;
import tech.ydb.table.TableClient;
import tech.ydb.table.query.DataQueryResult;
import tech.ydb.table.query.Params;
import tech.ydb.table.transaction.TxControl;

import java.util.function.Consumer;

public class EntityManager {

    private static final Logger logger = LoggerFactory.getLogger(EntityManager.class);
    private final String database;
    private final String endpoint;

    public EntityManager(String database, String endpoint) {
        this.database = database;
        this.endpoint = endpoint;
    }

    public void execute(String query, Params params, Consumer<DataQueryResult> callback) {
        logger.debug("Authentication via environ...");
        AuthProvider authProvider = CloudAuthHelper.getAuthProviderFromEnviron();

        logger.debug("Creating GrpcTransport...");
        try (GrpcTransport transport = GrpcTransport.forEndpoint(endpoint, database)
                .withAuthProvider(authProvider)
                .build()) {
            logger.debug("Creating TableClient...");
            try (TableClient tableClient = TableClient.newClient(transport).build()) {

                logger.debug("Creating SessionRetryContext...");
                SessionRetryContext retryCtx = SessionRetryContext.create(tableClient).build();

                logger.debug("Configuring TxControl...");
                TxControl<?> txControl = TxControl.serializableRw().setCommitTx(true);

                logger.debug("Executing DataQuery...");
                DataQueryResult result = retryCtx.supplyResult(session -> session
                                .executeDataQuery(query, txControl, params))
                        .join().getValue();

                if (!result.isEmpty()){
                    logger.info("Result set obtained!");
                }

                if (callback != null) {
                    callback.accept(result);
                }
            }
        }
    }

    public void execute(String query, Params params) {
        execute(query, params, null);
    }
}
