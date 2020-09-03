package net.accelf.intellij.gmusic.responses

class ConnectResponse(response: BaseResponse) : Response(response.channel, response.payload) {

    val code: String = payload.asString
    val codeRequired = code == "CODE_REQUIRED"
}
