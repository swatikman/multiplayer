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
            Utils.debugLog("But already joined...")
        }
    }

    fun disconnect() {
        Utils.debugLog("Disconnecting...")
        socket.disconnect()
    }

    override fun subscribeOnEvents() {
        socket.on(Socket.EVENT_CONNECT, {
            Utils.debugLog("Connected")
            connectionListener?.connected()
        })
        socket.on(SocketManager.EVENT_JOINED) { args ->
            Utils.debugLog("Joined to room with :" + args[0])
            connectionListener?.joined(JsonUtil.jsonToObject(args[0], PlayerData::class.java))
        }
        socket.on(SocketManager.EVENT_JOIN) { args ->
            Utils.debugLog("Someone joined maybe it's me: " + args[0])
            connectionListener?.someoneJoined(JsonUtil.parseNick(args[0]))
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

    fun join(nick: String, room: String) {
        val data = JSONObject(mapOf("nick" to nick, "room" to room))
        socket.emit(SocketManager.EVENT_JOIN, data)
    }

    interface ConnectionListener {
        fun joined(playerData: PlayerData)

        fun someoneJoined(nick: String?)

        fun someoneDisconnected(nick: String?)

        fun connected()
    }
}
