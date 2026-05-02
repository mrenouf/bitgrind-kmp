package com.bitgrind.kmp.ulid

import org.kotlincrypto.random.CryptoRand
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * An implementation of [Universally-Unique Lexicographically-Sortable Identifiers](https://github.com/ulid/spec)
 *
 * This class is not thread-safe. For concurrent use, create a new instance for each thread.
 */
@OptIn(ExperimentalTime::class)
sealed class ULID(
    val now: () -> Long = { Clock.System.now().toEpochMilliseconds() },
    val random: (ByteArray) -> Unit = { CryptoRand.nextBytes(it) }
) {
    private var lastMs = 0L
    private val randBuffer = ByteArray(10)

    fun create(): String {
        val ts = now()
        if (lastMs == ts) {
            randBuffer.inc()
        } else {
            random(randBuffer)
            lastMs = ts
        }
        return "${ts.encodeUlidTimestamp()}${randBuffer.encodeUlid()}"
    }

    data class Values(val timestamp: Long, val randomness: ByteArray) {
        constructor(
            value: String
        ) : this(
            value.substring(0, 10).decodeUlidTimestamp(strict = true),
            value.substring(10).decodeUlid(strict = true)
        ) {
            require(value.length == 26) { "Invalid ULID: $value. Must be 26 chars long" }
        }

        fun encode(): String = "${timestamp.encodeUlidTimestamp()}${randomness.encodeUlid()}"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Values) return false
            if (timestamp != other.timestamp) return false
            if (!randomness.contentEquals(other.randomness)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = timestamp.hashCode()
            result = 31 * result + randomness.contentHashCode()
            return result
        }
    }

    companion object Default : ULID() {
        fun decode(value: String): Values = Values(value)
    }

    private fun ByteArray.inc() {
        var i = lastIndex
        while (i >= 0 && this[i] == (-1).toByte()) {
            this[i] = 0
            i--
        }
        if (i >= 0) this[i]++
        if (i < 0 || i == 0 && this[0] == (-1).toByte()) error("Overflow!")
    }

    private val values = intArrayOf(
        /*  0 */ -1, -1, -1, -1, -1, -1, -1, -1,
        /*  8 */ -1, -1, -1, -1, -1, -1, -1, -1,
        /* 16 */ -1, -1, -1, -1, -1, -1, -1, -1,
        /* 24 */ -1, -1, -1, -1, -1, -1, -1, -1,
        /* 32 */ -1, -1, -1, -1, -1, -1, -1, -1,
        /* 40 */ -1, -1, -1, -1, -1, -1, -1, -1,
        /* 48 */  0, 1, 2, 3, 4, 5, 6, 7,
        /* 56 */  8, 9, -1, -1, -1, -1, -1, -1,
        /* 64 */ -1, 10, 11, 12, 13, 14, 15, 16,
        /* 72 */ 17, 0, 18, 19, 0, 20, 21, 0,
        /* 80 */ 22, 23, 24, 25, 26, 0, 27, 28,
        /* 88 */ 29, 30, 31
    )

    private val chars = charArrayOf(
        /*  0 */  '0', '1', '2', '3', '4', '5', '6', '7',
        /*  8 */  '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
        /* 16 */  'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q',
        /* 24 */  'r', 's', 't', 'v', 'w', 'x', 'y', 'z'
    )

    internal fun Long.encodeUlidTimestamp(): String {
        require(this < 0xFFFF_FFFF_FFFFL) { "timestamp must fit in 48 bits" }

        var v = this
        val out = CharArray(10)
        for (i in 9 downTo 0) {
            out[i] = chars[(v and 0x1F).toInt()]
            v = v ushr 5
        }
        require(chars.indexOf(out[0]) <= 7) { "Internal error: non-canonical timestamp" }
        return out.concatToString()
    }

    internal fun String.decodeUlidTimestamp(strict: Boolean = true): Long {
        require(length >= 10) { "ULID must be at least 10 chars" }

        var value = 0L
        var bits = 0

        for (idx in 0 until 10) {
            val ch = this[idx]
            val code = ch.uppercaseChar().code
            val v = if (code <= 90) values[code] else -1

            require(v >= 0) { "Invalid Base32 character in timestamp: '$ch'" }

            value = (value shl 5) or v.toLong()
            bits += 5
        }

        if (strict) {
            require(value ushr 48 == 0L) {
                "Non-canonical ULID timestamp (top 2 bits must be 0)"
            }
        }
        return value
    }

    internal fun ByteArray.encodeUlid(): String {
        if (isEmpty()) return ""

        val outLen = (size * 8 + 4) / 5
        val out = CharArray(outLen)

        var outPos = 0
        var buffer = 0
        var bits = 0

        for (b in this) {
            buffer = (buffer shl 8) or (b.toInt() and 0xFF)
            bits += 8

            while (bits >= 5) {
                bits -= 5
                val digit = (buffer ushr bits) and 0x1F
                out[outPos++] = chars[digit]
            }
        }

        if (bits != 0) {
            val digit = (buffer shl (5 - bits)) and 0x1F
            out[outPos++] = chars[digit]
        }

        return out.concatToString(0, 0 + outPos)
    }

    internal fun String.decodeUlid(strict: Boolean = false): ByteArray {
        if (isEmpty()) return ByteArray(0)

        val maxBytes = (length * 5) / 8
        val out = ByteArray(maxBytes)

        var outPos = 0
        var buffer = 0
        var bits = 0

        for (ch in this) {
            val code = ch.uppercaseChar().code
            val v = if (code <= 90) values[code] else -1
            if (v == -2 && !strict) continue
            require(v >= 0) { "Invalid Base32 char: '$ch'" }

            buffer = (buffer shl 5) or v
            bits += 5

            while (bits >= 8) {
                bits -= 8
                out[outPos++] = ((buffer ushr bits) and 0xFF).toByte()
            }
        }
        if (strict && bits != 0) {
            val mask = (1 shl bits) - 1
            require((buffer and mask) == 0) { "Non-zero trailing bits" }
        }
        return if (outPos == out.size) out else out.copyOf(outPos)
    }
}
