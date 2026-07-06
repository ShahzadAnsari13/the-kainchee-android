package com.thekainchee.user.utils.socket
import io.socket.client.IO
import io.socket.client.Socket
object SocketManager {
    private val SOCKET_URL = "http://10.70.177.101:3000"
    private var socket: Socket? = null

    fun connect(){
        if (socket==null){
            socket = IO.socket(SOCKET_URL)
        }
        socket?.connect()
    }
    fun disconnect(){
        socket?.disconnect()
    }
    fun getSocket(): Socket? {
        return socket
    }

}