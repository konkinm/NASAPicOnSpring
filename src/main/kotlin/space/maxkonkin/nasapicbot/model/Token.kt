package space.maxkonkin.nasapicbot.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Token(
    @JsonProperty("access_token")
    val token: String,
    @JsonProperty("expires_in")
    val expiresIn: Long,
    @JsonProperty("token_type")
    val tokenType: String
)
