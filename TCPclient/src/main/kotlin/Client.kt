import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

fun main() {
    val client = Socket("127.0.0.1", 9999)
    val consoleReader = BufferedReader(InputStreamReader(System.`in`))
    val reader = BufferedReader(InputStreamReader(client.getInputStream()))
    val writer = BufferedWriter(OutputStreamWriter(client.getOutputStream()))
        try {
            val text = consoleReader.readLine()

            writer.write(text + "\n")
            writer.flush()

            val ans = reader.readLine()
            println(ans)
        } catch (ex: Exception) {
            client.close()
        } finally {
            println("Client close...");
            client.close();
            reader.close();
            writer.close();
        }

    client.close()
}
