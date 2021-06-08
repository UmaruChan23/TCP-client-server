import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.util.*

import kotlin.concurrent.thread
import kotlin.experimental.xor

    fun main() {
        val server = ServerSocket(9999)
        println("Server running on port ${server.localPort}")

        while (true) {
            val client = server.accept()

            val tempClient = ClientHandler(client)
            ServerList.serverList.add(tempClient)
            println("Client connected : ${client.inetAddress.hostAddress}")

            thread { tempClient.run() }
        }
    }

object ServerList {
    val serverList: LinkedList<ClientHandler> = LinkedList()
}

class ClientHandler(private val client: Socket) {
    private val sin = client.getInputStream()
    private val out = client.getOutputStream()
    private val reader = DataInputStream(sin)
    private val writer = DataOutputStream(out)
    private val keyHandler = DH()
    private var running: Boolean = false

    fun run() {
        running = true

        genKeys()

        while (running) {
            try {

                val text = read()
                println(text)

                if (text == "EXIT" || text.equals(null)) {
                    shutdown()
                    continue
                }

                for(cl in ServerList.serverList)
                {
                    cl.write(text)
                }

            } catch (ex: Exception) {
                shutdown()
            }

        }
    }

    private fun genKeys(){
        writer.writeUTF("${keyHandler.getG()};${keyHandler.getP()};${keyHandler.getRemains()}")
        writer.flush()

        keyHandler.setA(reader.readUTF().toDouble())
        keyHandler.checkControlSum()
    }


    private fun write(message: String) {
        writer.writeInt(message.length)
        writer.write(encode(message, keyHandler.getKey()))
        writer.flush()
    }

    private fun read(): String {
        var buffer = ByteArray(reader.readInt())
        reader.read(buffer)
        return decode(buffer, keyHandler.getKey())
    }

    private fun shutdown() {
        running = false
        client.close()
        println("${client.inetAddress.hostAddress} closed the connection")
    }

    fun encode(pText: String, pKey: String): ByteArray {
        val txt = pText.toByteArray()
        val key = pKey.toByteArray()
        val res = ByteArray(pText.length)
        for (i in txt.indices) {
            res[i] = (txt[i] xor key[i % key.size])
        }
        return res
    }


    fun decode(pText: ByteArray, pKey: String): String {
        val res = ByteArray(pText.size)
        val key = pKey.toByteArray()
        for (i in pText.indices) {
            res[i] = (pText[i] xor key[i % key.size])
        }
        return String(res)
    }

}

