import kotlin.math.pow

class DH {
    private var g: Double  = (10..30).random().toDouble()
    private var p: Double  = (31..41).random().toDouble()
    private var privateNum: Double = (2..10).random().toDouble()
    private var A: Double = 0.0
    private var B: Double = 0.0
    private var key: String = ""

    fun checkControlSum() {
        key = (A.pow(privateNum) % p).toString()
    }

    fun getRemains(): Double {
        B = g.pow(privateNum) % p
        return B
    }

    fun getKey(): String  {
        return key
    }

    fun getP() : Double {
        return p
    }

    fun getG() : Double {
        return g
    }

    fun getA() : Double {
        return A
    }

    fun setA(a: Double) {
        A = a
    }
}