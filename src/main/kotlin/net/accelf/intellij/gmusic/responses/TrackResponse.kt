package net.accelf.intellij.gmusic.responses

import com.google.gson.JsonDeserializationContext

class TrackResponse(response: BaseResponse, gson: JsonDeserializationContext) :
    Response(response.channel, response.payload) {

    val track: Track = gson.deserialize(response.payload, Track::class.java)
}
