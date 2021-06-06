import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import kotlin.experimental.xor

fun main() {
    val client = Socket("127.0.0.1", 9999)
    val sin = client.getInputStream()
    val out = client.getOutputStream()
    val reader = DataInputStream(sin)
    val writer = DataOutputStream(out)

    val keyHandler = DH()

    val p = reader.readUTF()
    val gpB = p.split(";")
    keyHandler.setG(gpB[0].toDouble())
    keyHandler.setP(gpB[1].toDouble())
    keyHandler.setB(gpB[2].toDouble())

    writer.writeUTF(keyHandler.getRemains().toString())
    writer.flush()
    keyHandler.checkControlSum()

    try {

        val text = readLine().toString()

        val massage = encode(text, keyHandler.getKey())

        writer.writeInt(massage.size)
        writer.write(massage)
        writer.flush()

        var buffer = ByteArray(reader.readInt())
        reader.read(buffer)

        val ans = decode(buffer, keyHandler.getKey())
        println(ans)
    } catch (ex: Exception) {
        client.close()
    } finally {
        println("Client close...");
        client.close();
    }

    client.close()
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