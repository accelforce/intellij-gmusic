package net.accelf.intellij.gmusic.responses

import com.google.gson.JsonElement

class BaseResponse(channel: String, payload: JsonElement) : Response(channel, payload)
