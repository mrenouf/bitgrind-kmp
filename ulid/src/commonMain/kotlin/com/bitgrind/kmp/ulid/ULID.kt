package com.bitgrind.kmp.ulid

import com.bitgrind.kmp.ulid.ULIDFactory.Companion.inc
import org.kotlincrypto.random.CryptoRand
import kotlin.time.Clock

private val defaultUlid: ULID = ULID.monotonicFactory()

/**
 * Create a ULID using the default instance.
 *
 * Not thread-safe. Create a new instance per thread with [ULID.monotonicFactory] or serialize access.
 */
fun ulid(time: Long = Clock.System.now().toEpochMilliseconds()) = defaultUlid(time)

interface ULID {
    operator fun invoke(time: Long = Clock.System.now().toEpochMilliseconds()): String

    companion object {
        fun monotonicFactory(
            random: (ByteArray) -> Unit = { CryptoRand.nextBytes(it) }
        ): ULID = ULIDFactory(random)

        fun isValid(value: String): Boolean {
            if (value.length != 26) return false
            if (value[0] !in '0'..'7') return false
            for (i in 1 until 26) {
                val code = value[i].code
                if (code !in 0..122) return false
                if (values[code] < 0) return false
            }
            return true
        }

        fun decodeTime(ulid: String): Long {
            require(ulid.length >= 10) { "ulid must be at least 10 chars" }

            val time = ulid.substring(0, 10)
            require(time[0] in '0'..'7') { "timestamp value must be <= 48 bits" }

            var result = 0L
            var bits = 0

            for (idx in 0 until 10) {
                val ch = time[idx]
                val code = ch.code
                val v = if (code < values.size) values[code] else -1

                require(v >= 0) { "Invalid Base32 character in timestamp: '$ch'" }

                result = (result shl 5) or v.toLong()
                bits += 5
            }
            return result
        }

        internal fun encodeTime(time: Long): String {
            require(time in 0 .. 0xFFFF_FFFF_FFFFL) { "time must fit in 48 bits" }

            var ts = time
            val out = CharArray(10)
            for (i in 9 downTo 0) {
                out[i] = encoding[(ts and 0x1F).toInt()]
                ts = ts ushr 5
            }
            return out.concatToString()
        }

        internal fun encodeRandom(bytes: ByteArray): String {
            require(bytes.size == 10) { "random must be 10 bytes" }

            val outLen = (bytes.size * 8 + 4) / 5
            val out = CharArray(outLen)

            var outPos = 0
            var buffer = 0
            var bits = 0

            for (b in bytes) {
                buffer = (buffer shl 8) or (b.toInt() and 0xFF)
                bits += 8

                while (bits >= 5) {
                    bits -= 5
                    val digit = (buffer ushr bits) and 0x1F
                    out[outPos++] = encoding[digit]
                }
            }

            if (bits != 0) {
                val digit = (buffer shl (5 - bits)) and 0x1F
                out[outPos++] = encoding[digit]
            }

            return out.concatToString(0, 0 + outPos)
        }

        private val values = intArrayOf(
            /*  0 */ -1, -1, -1, -1, -1, -1, -1, -1,
            /*  8 */ -1, -1, -1, -1, -1, -1, -1, -1,
            /* 16 */ -1, -1, -1, -1, -1, -1, -1, -1,
            /* 24 */ -1, -1, -1, -1, -1, -1, -1, -1,
            /* 32 */ -1, -1, -1, -1, -1, -1, -1, -1,
            /* 40 */ -1, -1, -1, -1, -1, -1, -1, -1,
            /* 48 */  0,  1,  2,  3,  4,  5,  6,  7,  // 0-7
            /* 56 */  8,  9, -1, -1, -1, -1, -1, -1,  // 8,9
            /* 64 */ -1, 10, 11, 12, 13, 14, 15, 16,  // A–G
            /* 72 */ 17, -1, 18, 19, -1, 20, 21, -1,  // H,J,K,M,N
            /* 80 */ 22, 23, 24, 25, 26,  0, 27, 28,  // P,Q,R,S,T,V,W
            /* 88 */ 29, 30, 31, -1, -1, -1, -1, -1,  // X,Y,Z
            /* 96 */ -1, 10, 11, 12, 13, 14, 15, 16,  // a–g
            /*104 */ 17, -1, 18, 19, -1, 20, 21, -1,  // h,j,k,m,n
            /*112 */ 22, 23, 24, 25, 26, -1, 27, 28,  // p,q,r,s,t,v,w
            /*120 */ 29, 30, 31                       // x,y,z
        )

        private val encoding = charArrayOf(
            /*  0 */  '0', '1', '2', '3', '4', '5', '6', '7',
            /*  8 */  '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
            /* 16 */  'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q',
            /* 24 */  'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'
        )
    }
}

/**
 * An implementation of [Universally-Unique Lexicographically-Sortable Identifiers](https://github.com/ulid/spec)
 *
 * This class is not thread-safe. For concurrent use, create a new instance for each thread.
 */
class ULIDFactory(val random: (ByteArray) -> Unit) : ULID {
    private var lastMs: Long? = null
    private val randBuffer = ByteArray(10).apply(random)

    override operator fun invoke(time: Long): String {
        require(time in 0 .. 0xFFFF_FFFF_FFFFL) { "time must fit in 48 bits" }

        val last = lastMs
        var ts = time
        if (last == null || ts > last) {
            random(randBuffer)
            lastMs = time
            ts = time
        } else {
            inc(randBuffer)
            ts = last
        }
        return "${ULID.encodeTime(ts)}${ULID.encodeRandom(randBuffer)}"
    }

    companion object {
        private fun inc(array: ByteArray) {
            var i = array.lastIndex
            while (i >= 0 && array[i] == (-1).toByte()) {
                array[i] = 0
                i--
            }
            if (i >= 0) array[i]++
            if (i < 0 || i == 0 && array[0] == (-1).toByte()) error("Overflow!")
        }
    }
}
