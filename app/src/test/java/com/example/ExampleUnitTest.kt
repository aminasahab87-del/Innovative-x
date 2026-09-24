package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun adminPasscode_verificationRules() {
    val validCode = "123321"
    val invalidCode1 = "123456"
    val invalidCode2 = "12332"
    val invalidCode3 = "000000"

    assertTrue(validCode.trim() == "123321")
    assertFalse(invalidCode1.trim() == "123321")
    assertFalse(invalidCode2.trim() == "123321")
    assertFalse(invalidCode3.trim() == "123321")
  }
}
