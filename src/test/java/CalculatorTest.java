import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CalculatorTest {

    @Test
    void testMultiply() {
        Calculator c = new Calculator();
        assertEquals(21, c.multiply(7, 3));
    }

    @Test
    void testDivide() {
        Calculator c = new Calculator();
        assertEquals(5, c.divide(20, 4));
    }

    @Test
    void testDivideByZero() {
        Calculator c = new Calculator();
        assertThrows(IllegalArgumentException.class, () -> c.divide(20, 0));
    }

    // Parameterized Tests:
    @ParameterizedTest
    @CsvSource({
            "7, 3, 10",
            "0, 0, 0",
            "31, 0, 31",
            "0, -10, -10",
            "-123, -123, -246",
            "0, 1, 1",
            "-1, 0, -1",
            "2147483647, 0, 2147483647", // MAX VALUE
            "0, -2147483648, -2147483648", // MIN VALUE
            "1_000_000, 1_000_000, 2_000_000"
    })
    void testAddParameterized(int a, int b, int expected) {
        Calculator c = new Calculator();
        assertEquals(expected, c.add(a, b));
    }

    // Parameterized Tests:
    @ParameterizedTest
    @CsvSource({
            "12, 7, 5",
            "0, 0, 0"

    })
    void testSubtractParameterized(int a, int b, int expected) {
        Calculator c = new Calculator();
        assertEquals(expected, c.subtract(a, b));
    }


}