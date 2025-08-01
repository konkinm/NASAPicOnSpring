package space.maxkonkin.nasapicbot.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.springframework.stereotype.Component
import space.maxkonkin.nasapicbot.config.YandexTranslateApiConfig
import space.maxkonkin.nasapicbot.model.LangCode
import java.io.IOException
import java.io.UnsupportedEncodingException

@Component
class YandexTranslateApiClient(private val config: YandexTranslateApiConfig) {
    @Throws(IOException::class)
    fun translate(inputTexts: List<String>, langCode: LangCode): List<String> {
        val httpPost = HttpPost(config.apiBaseUri)
        httpPost.setHeader("Content-type", "application/json")
        httpPost.setHeader("Authorization", config.apiToken)
        val entity = getStringEntity(inputTexts, langCode)
        httpPost.entity = entity
        val textNodes: List<JsonNode>
        HttpClients.createDefault().use { client ->
            client.execute(httpPost).use { response ->
                val jsonNode = mapper.readTree(response.entity.content)
                textNodes = jsonNode.findValues("text")
            }
        }
        return textNodes.map { obj: JsonNode -> obj.textValue() }.toList()
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
        val json = """{ "folderId": "${config.folderId}", "texts": [$commaSeparatedTexts], "targetLanguageCode": "${langCode.code}"}"""
        return StringEntity(json)
    }

    companion object {
        private val mapper = ObjectMapper()
    }
}