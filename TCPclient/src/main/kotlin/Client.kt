import java.net.Socket

fun main() {
    val client = Socket("127.0.0.1", 9999)
    client.outputStream.write("Hello".toByteArray())
    client.close()
}