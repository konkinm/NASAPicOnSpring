package space.maxkonkin.nasapicbot.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import space.maxkonkin.nasapicbot.config.YandexTranslateConfig
import space.maxkonkin.nasapicbot.model.LangCode

class YandexTranslateApiClient(
    private val config: YandexTranslateConfig,
    private val mapper: ObjectMapper,
    private val httpClient: OkHttpClient
) {
    fun translate(inputTexts: List<String>, langCode: LangCode): List<String> {
        val requestBody = getStringEntity(inputTexts, langCode).toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(config.apiBaseUri)
            .addHeader("Content-type", "application/json")
            .addHeader("Authorization", config.apiToken)
            .post(requestBody)
            .build()
        val textNodes: List<JsonNode>
        httpClient.newCall(request).execute().use { response ->
            val jsonNode = mapper.readTree(response.body?.string())
            textNodes = jsonNode.findValues("text")
        }

        return textNodes.map { node -> node.textValue() }.toList()
    }

    private fun getStringEntity(inputTexts: List<String>, langCode: LangCode): String {
        val commaSeparatedTexts = StringBuilder()
        for (inputText in inputTexts) {
            val filteredInputText = inputText.replace("\"", "\\\"")
            commaSeparatedTexts.append("\"")
            commaSeparatedTexts.append(filteredInputText)
            commaSeparatedTexts.append("\",")
        }
        val json =
            """{ "folderId": "${config.folderId}", "texts": [$commaSeparatedTexts], "targetLanguageCode": "${langCode.code}"}"""
        return json
    }
}