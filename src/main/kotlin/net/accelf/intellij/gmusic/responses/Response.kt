package net.accelf.intellij.gmusic.responses

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import java.lang.reflect.Type

@JsonAdapter(Response.Adapter::class)
open class Response(
    val channel: String,
    val payload: JsonElement,
) {

    class Adapter : JsonDeserializer<Response> {

        override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): Response {
            val response = p2!!.deserialize<BaseResponse>(p0, BaseResponse::class.java)

            return when (response.channel) {
                "connect" -> ConnectResponse(response)
                "API_VERSION" -> ApiVersionResponse(response)
                "track" -> TrackResponse(response, p2)
                else -> response
            }
        }
    }
}
