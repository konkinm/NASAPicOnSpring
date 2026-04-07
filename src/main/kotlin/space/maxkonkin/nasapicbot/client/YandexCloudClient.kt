package space.maxkonkin.nasapicbot.client

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import space.maxkonkin.nasapicbot.config.YandexCloudConfig
import space.maxkonkin.nasapicbot.to.InvokeFunction
import space.maxkonkin.nasapicbot.to.Rule
import space.maxkonkin.nasapicbot.to.Timer
import space.maxkonkin.nasapicbot.to.Trigger

class YandexCloudClient(
    private val config: YandexCloudConfig,
    private val mapper: ObjectMapper,
    private val httpClient: OkHttpClient
) {
    fun isTriggerCreated(triggerId: String?, token: String): Boolean {
        if (triggerId?.isNotBlank() == true) {
            val request = Request.Builder()
                .url("${config.triggerApiUri}/$triggerId")
                .addHeader("Authorization", "Bearer $token")
                .get()
                .build()
            return httpClient.newCall(request).execute().isSuccessful

        } else return false
    }

    fun createTrigger(chatId: String, token: String?): String? {
        if (token != null) {
            val body = mapper.writeValueAsString(
                Trigger(
                    config.folderId,
                    "nasapic-schedule-${System.getenv("PROFILE")}-$chatId",
                    Rule(
                        Timer(
                            payload = chatId,
                            invokeFunction = InvokeFunction(
                                functionId = config.triggerFunctionId,
                                serviceAccountId = config.serviceAccountId
                            ),
                        )
                    )
                )
            ).toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(config.triggerApiUri)
                .addHeader("Authorization", "Bearer $token")
                .post(body)
                .build()
            val response = httpClient.newCall(request).execute()

            return mapper.readTree(response.body?.string()).get("response").get("id").asText()
        } else return null
    }

    fun pauseTrigger(triggerId: String?, token: String): Boolean {
        if (triggerId?.isNotBlank() == true) {
            val request = Request.Builder()
                .url("${config.triggerApiUri}/$triggerId:pause")
                .addHeader("Authorization", "Bearer $token")
                .post("".toRequestBody())
                .build()
            return httpClient.newCall(request).execute().isSuccessful
        }
        return false
    }

    fun resumeTrigger(triggerId: String?, token: String): Boolean {
        if (triggerId?.isNotBlank() == true) {
            val request = Request.Builder()
                .url("${config.triggerApiUri}/$triggerId:resume")
                .addHeader("Authorization", "Bearer $token")
                .post("".toRequestBody())
                .build()
            return httpClient.newCall(request).execute().isSuccessful
        }
        return false
    }

    fun deleteTrigger(triggerId: String?, token: String): Boolean {
        if (triggerId?.isNotBlank() == true) {
            val request = Request.Builder()
                .url("${config.triggerApiUri}/$triggerId")
                .addHeader("Authorization", "Bearer $token")
                .delete()
                .build()
            return httpClient.newCall(request).execute().isSuccessful
        }
        return false
    }
}