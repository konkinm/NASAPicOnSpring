package space.maxkonkin.nasapicbot.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.springframework.stereotype.Component
import space.maxkonkin.nasapicbot.config.NasaAPIConfig
import space.maxkonkin.nasapicbot.to.NasaTo
import space.maxkonkin.nasapicbot.util.cloneWithReplacedUrl
import java.io.IOException

@Component
class NasaApiClient(
    private val nasaAPIConfig: NasaAPIConfig,
    private val mapper: ObjectMapper,
    private val httpClient: CloseableHttpClient
) {
    fun makeNasaApiRequest(param: String): String {
        return nasaAPIConfig.apiBaseUri + param
    }

    @Throws(IOException::class)
    fun getNASAObject(uri: String): NasaTo {
        httpClient.execute(HttpGet(uri)).use { response ->
            val input = mapper.readValue(response.entity.content, NasaTo::class.java)
            return if (input.mediaType == "video" && input.url?.contains("embed/") == true) {
                val filtered = input.url.replace("embed/", "watch?v=")
                    .replace("?rel=0", "")
                cloneWithReplacedUrl(input, filtered)
            } else {
                input
            }
        }
    }

    @Throws(IOException::class, InterruptedException::class)
    fun getNASAObjects(uri: String): List<NasaTo> {
        httpClient.execute(HttpGet(uri)).use { response ->
            val tos: List<NasaTo> = mapper.readValue(
                response.entity.content,
                mapper.typeFactory.constructCollectionType(List::class.java, NasaTo::class.java)
            )
            return getFiltered(tos)
        }
    }

    private fun getFiltered(tos: List<NasaTo>): List<NasaTo> {
        val filtered: MutableList<NasaTo> = ArrayList()
        for (to in tos) {
            if (to.mediaType == "video" && to.url?.contains("embed/") == true) {
                val replaced = to.url.replace("embed/", "watch?v=")
                    .replace("?rel=0", "")
                filtered.add(cloneWithReplacedUrl(to, replaced))
            } else {
                filtered.add(to)
            }
        }
        return filtered
    }
}
