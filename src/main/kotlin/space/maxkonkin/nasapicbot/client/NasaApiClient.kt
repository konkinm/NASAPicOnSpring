package space.maxkonkin.nasapicbot.client

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import space.maxkonkin.nasapicbot.config.NasaAPIConfig
import space.maxkonkin.nasapicbot.to.NasaTo
import space.maxkonkin.nasapicbot.util.cloneWithReplacedUrl

class NasaApiClient(
    private val nasaAPIConfig: NasaAPIConfig,
    private val mapper: ObjectMapper,
    private val httpClient: OkHttpClient
) {
    fun makeNasaApiRequest(param: String): String {
        return nasaAPIConfig.apiBaseUri + param
    }

    fun getNASAObject(uri: String): NasaTo {
        val request = Request.Builder().url(uri).build()
        httpClient.newCall(request).execute().use { response ->
            val input = mapper.readValue(response.body?.string(), NasaTo::class.java)
            return if (input.mediaType == "video" && input.url?.contains("embed/") == true) {
                val filtered = input.url.replace("embed/", "watch?v=")
                    .replace("?rel=0", "")
                cloneWithReplacedUrl(input, filtered)
            } else {
                input
            }
        }
    }

    fun getNASAObjects(uri: String): List<NasaTo> {
        val request = Request.Builder().url(uri).build()
        httpClient.newCall(request).execute().use { response ->
            val tos: List<NasaTo> = mapper.readValue(
                response.body?.string(),
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
