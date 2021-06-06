import kotlin.math.pow

class DH {
    private var g: Double  = 0.0
    private var p: Double  = 0.0
    private var privateNum: Double = (2..10).random().toDouble()
    private var A: Double = 0.0
    private var B: Double = 0.0
    private var key: String = ""

    fun checkControlSum() {
        key = (B.pow(privateNum) % p).toString()
    }

    fun getRemains(): Double {
        A = g.pow(privateNum) % p
        return A
    }

    fun getKey(): String  {
        return key
    }

    fun setP(p: Double)  {
        this.p = p
    }

    fun setG(g: Double)  {
        this.g = g
    }

    fun getB() : Double {
        return B
    }

    fun setB(b: Double) {
        B = b
    }
}