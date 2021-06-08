import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import kotlin.concurrent.thread
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
        thread { ReadAllMsg(reader, keyHandler).run() }
        thread { WriteAllMsg(writer, keyHandler, client).run() }
    } catch (ex: Exception) {
        println("ex")
        client.close()
    }
}


class ReadAllMsg(private val reader: DataInputStream, private val keyHandler: DH) {

    fun run() {
        try {
            while (true) {
                val buffer = ByteArray(reader.readInt())
                reader.read(buffer)
                val msg = decode(buffer, keyHandler.getKey())
                println(msg)
            }
        } catch (ex: Exception) {
        }
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

class WriteAllMsg(private val writer: DataOutputStream, private val keyHandler: DH, private val client: Socket) {

    fun run() {
        try {
            while (true) {
                val text = readLine().toString()

                if(text.equals("exit")){
                    client.close()
                }

                val massage = encode(text, keyHandler.getKey())

                writer.writeInt(massage.size)
                writer.write(massage)
                writer.flush()
            }
        } catch (ex: Exception) {
        }
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
}