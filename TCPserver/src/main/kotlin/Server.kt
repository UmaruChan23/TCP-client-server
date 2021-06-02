import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread

fun main() {
    val server = ServerSocket(9999)
    println("Server running on port ${server.localPort}")

    while (true) {
        val client = server.accept()
        println("Client connected : ${client.inetAddress.hostAddress}")

        thread { ClientHandler(client).run() }
    }
}

class ClientHandler(private val client: Socket) {
    private val reader: BufferedReader = BufferedReader(InputStreamReader(client.getInputStream()))
    private val writer: BufferedWriter = BufferedWriter(OutputStreamWriter(client.getOutputStream()))
    private var running: Boolean = false

    fun run() {
        running = true

        while (running) {
            try {
                val text = reader.readLine()
                if (text == "EXIT") {
                    shutdown()
                    continue
                }
                write(text + "\n")
            } catch (ex: Exception) {
                shutdown()
            }

        }
    }

    private fun write(message: String) {
        writer.write(message)
        writer.flush()
    }

    private fun shutdown() {
        running = false
        client.close()
        reader.close()
        writer.close()
        println("${client.inetAddress.hostAddress} closed the connection")
    }

}

