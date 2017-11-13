package ga.chrom_web.player.multiplayer


import ga.chrom_web.player.multiplayer.data.PlayerData
import io.socket.client.Socket
import org.json.JSONObject

class ConnectionSocketManager : SocketManager() {

    var connectionListener: ConnectionListener? = null

    val isConnected: Boolean
        get() = socket.connected()

    fun connect() {
        Utils.debugLog("Trying to connect...")
        if (!socket.connected()) {
            socket.connect()
        } else {
            Utils.debugLog("But already connected...")
        }
    }

    fun disconnect() {
        Utils.debugLog("Disconnecting...")
        socket.disconnect()
    }

    override fun subscribeOnEvents() {
        socket.on(SocketManager.EVENT_CONNECTED) { args ->
            Utils.debugLog("Connected. Data: " + args[0])
            connectionListener?.connected(JsonUtil.jsonToObject(args[0], PlayerData::class.java))
        }
        socket.on(SocketManager.EVENT_JOIN) { args ->
            Utils.debugLog("Someone joined maybe it's me: " + args[0])
            connectionListener?.joined(JsonUtil.parseNick(args[0]))
        }
        socket.on(SocketManager.EVENT_DISCONNECT) { args ->
            Utils.debugLog("Someone disconnect: " + args[0])
            connectionListener?.someoneDisconnected(JsonUtil.parseNick(args[0]))
        }
        // we receive pings each 25 seconds
        // send pong back
        socket.on(Socket.EVENT_PING, {
            socket.emit(Socket.EVENT_PONG)
        })

        socket.on(Socket.EVENT_DISCONNECT) { _ -> Utils.debugLog("Disconnected!!!") }
        socket.on(Socket.EVENT_RECONNECT) { _ -> Utils.debugLog("Reconnect successful") }
    }

    fun join(nick: String) {
        socket.emit(SocketManager.EVENT_JOIN, JSONObject(mapOf("nick" to nick)))
    }

    interface ConnectionListener {
        fun connected(playerData: PlayerData)

        fun joined(nick: String?)

        fun someoneDisconnected(nick: String?)
    }
}
