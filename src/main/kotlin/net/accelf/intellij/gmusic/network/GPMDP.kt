package net.accelf.intellij.gmusic.network

import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.reactivex.Flowable
import net.accelf.intellij.gmusic.commands.Command
import net.accelf.intellij.gmusic.responses.Response

interface GPMDP {
    @Receive
    fun observeWebSocketEvent(): Flowable<WebSocket.Event>

    @Send
    fun send(command: Command): Boolean

    @Receive
    fun observe(): Flowable<Response>
}
