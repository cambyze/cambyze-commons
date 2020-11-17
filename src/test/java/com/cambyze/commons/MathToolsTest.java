package com.cambyze.commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Unit test for mathematics tools
 * 
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
class MathToolsTest {
  @Test
  void test() {
    Double number = 5.12536;
    assertEquals(5.1, MathTools.roundWithDecimals(number, 1));
    assertEquals(5.13, MathTools.roundWithDecimals(number, 2));
    assertEquals(5.125, MathTools.roundWithDecimals(number, 3));
    assertEquals(5.1254, MathTools.roundWithDecimals(number, 4));
    assertEquals(5.12536, MathTools.roundWithDecimals(number, 5));
  }
}
