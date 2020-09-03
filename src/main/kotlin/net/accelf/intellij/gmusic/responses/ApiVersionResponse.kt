package net.accelf.intellij.gmusic.responses

class ApiVersionResponse(response: BaseResponse) : Response(response.channel, response.payload) {

    private val major: Int
    private val minor: Int
    private val patch: Int

    init {
        val regex = Regex("""([0-9]+)\.([0-9]+)\.([0-9]+).*""")
        @Suppress("MagicNumber")
        regex.matchEntire(response.payload.asString)!!.let {
            major = it.groupValues[1].toInt()
            minor = it.groupValues[2].toInt()
            patch = it.groupValues[3].toInt()
        }
    }

    val supports = major == 1
}
