import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CalculatorTest {
    @Test
    void testAdd() {
        Calculator c = new Calculator();
        assertEquals(10, c.add(7,3));
    }

    @Test
    void testSubtract() {
        Calculator c = new Calculator();
        assertEquals(5, c.subtract(12, 7));
    }

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
            "7, 3, 10"
    })
    void testAddParameterized(int a, int b, int expected) {
        Calculator c = new Calculator();
        assertEquals(expected, c.add(a, b));
    }
}