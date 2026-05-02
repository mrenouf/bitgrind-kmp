package com.bitgrind.kmp.ulid

import com.bitgrind.kmp.ulid.ULID.Companion.decodeTime
import com.bitgrind.kmp.ulid.ULID.Companion.encodeTime
import com.bitgrind.kmp.ulid.ULID.Companion.monotonicFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ULIDTest {

    val pattern = Regex("^[0-7][0-9a-hjkmnp-tv-zA-HJKMNP-TV-Z]{25}\$")

    @OptIn(ExperimentalUnsignedTypes::class)
    val stubPrng: (ByteArray) -> Unit = {
        // "YYYYYYYYYYYYYYYY"
        ubyteArrayOf(0xF7u, 0xBDu, 0xEFu, 0x7Bu, 0xDEu).toByteArray().copyInto(it)
        ubyteArrayOf(0xF7u, 0xBDu, 0xEFu, 0x7Bu, 0xDEu).toByteArray().copyInto(it, 5)
    }

    // Test cases from:
    // https://github.com/ulid/javascript/blob/master/test/node/ulid.spec.ts

    @Test
    fun `decodeTime decodes a timestamp`() {
        val id = "01ARYZ6S41TSV4RRFFQ69G5FAV"
        val ts = decodeTime(id)
        assertEquals(1469918176385, ts, "Timestamp must match")
    }

    @Test
    fun `encodeTime encodes a timestamp`() {
        val ts = 1469918176385L
        val id = encodeTime(ts)
        assertEquals("01ARYZ6S41", id, "Encoded timestamp must match")
    }
    @Test
    fun `ulid generates a ULID`() {
        val id = ulid()
        assertTrue(id.matches(pattern))
    }

    @Test
    fun `returned factory generates a ULID`() {
        val factory = monotonicFactory()
        val id = factory()
        assertTrue(id.matches(pattern))
    }

    @Test
    fun `returned factory generates unique ULIDs`() {
        val seedTime = 1469918176385
        val factory = monotonicFactory()
        val id = factory(seedTime)
        val id2 = factory(seedTime)
        assertNotEquals(id, id2, "ULIDs must be unique")
    }


    @Test
    fun `returned factory generates a ULID during single point in time`() {
        val stubbedUlid = monotonicFactory(stubPrng)
        val seedTime = 1469918176385

        assertEquals("01ARYZ6S41YYYYYYYYYYYYYYYY", stubbedUlid(seedTime), "first call")
        assertEquals("01ARYZ6S41YYYYYYYYYYYYYYYZ", stubbedUlid(seedTime), "second call")
        assertEquals("01ARYZ6S41YYYYYYYYYYYYYYZ0", stubbedUlid(seedTime), "third call")
        assertEquals("01ARYZ6S41YYYYYYYYYYYYYYZ1", stubbedUlid(seedTime), "fourth call")
    }

    @Test
    fun `returned factory generates a ULID with specific seedTime`() {
        val stubbedUlid = monotonicFactory(stubPrng)

        assertEquals("01ARYZ6S41YYYYYYYYYYYYYYYY", stubbedUlid(1469918176385), "first call")
        assertEquals("01ARYZ6S41YYYYYYYYYYYYYYYZ", stubbedUlid(1469918176385), "second call with the same")
        assertEquals("01ARYZ6S41YYYYYYYYYYYYYYZ0", stubbedUlid(100000000), "third call with less than")
        assertEquals("01ARYZ6S41YYYYYYYYYYYYYYZ1", stubbedUlid(10000), "fourth call with even more less than")
        assertEquals("01ARYZ6S42YYYYYYYYYYYYYYYY", stubbedUlid(1469918176386), "fifth call with 1 greater than")
    }

    @Test
    fun `isValid rejects invalid characters`() {
        assertFalse(ULID.isValid("O1ARYZ6S4lYYYYYYYYYYYYYYYY"))
    }

    @Test
    fun `isValid rejects incorrect length`() {
        assertFalse(ULID.isValid("01ARYZ6S41TSV4RRFFQ69G5"))
    }

    @Test
    fun `isValid rejects overflowed timestamp`() {
        assertFalse(ULID.isValid("91ARYZ6S41TSV4RRFFQ69G5FAV"))
    }

    @Test
    fun `ulid rejects overflowed timestamp`() {
        assertFails { ulid(0x1000000000000L) }
    }
}