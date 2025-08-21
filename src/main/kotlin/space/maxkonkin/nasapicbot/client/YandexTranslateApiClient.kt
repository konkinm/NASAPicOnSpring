package space.maxkonkin.nasapicbot.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import space.maxkonkin.nasapicbot.config.YandexTranslateConfig
import space.maxkonkin.nasapicbot.model.LangCode
import java.io.IOException
import java.io.UnsupportedEncodingException

class YandexTranslateApiClient(
    private val config: YandexTranslateConfig,
    private val mapper: ObjectMapper,
    private val httpClient: CloseableHttpClient
) {
    @Throws(IOException::class)
    fun translate(inputTexts: List<String>, langCode: LangCode): List<String> {
        val httpPost = HttpPost(config.apiBaseUri)
        httpPost.setHeader("Content-type", "application/json")
        httpPost.setHeader("Authorization", config.apiToken)
        val entity = getStringEntity(inputTexts, langCode)
        httpPost.entity = entity
        val textNodes: List<JsonNode>
        httpClient.execute(httpPost).use { response ->
            val jsonNode = mapper.readTree(response.entity.content)
            textNodes = jsonNode.findValues("text")
        }
        return textNodes.map { node -> node.textValue() }.toList()
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getStringEntity(inputTexts: List<String>, langCode: LangCode): StringEntity {
        val commaSeparatedTexts = StringBuilder()
        for (inputText in inputTexts) {
            val filteredInputText = inputText.replace("\"", "\\\"")
            commaSeparatedTexts.append("\"")
            commaSeparatedTexts.append(filteredInputText)
            commaSeparatedTexts.append("\",")
        }
        val json =
            """{ "folderId": "${config.folderId}", "texts": [$commaSeparatedTexts], "targetLanguageCode": "${langCode.code}"}"""
        return StringEntity(json)
    }
}