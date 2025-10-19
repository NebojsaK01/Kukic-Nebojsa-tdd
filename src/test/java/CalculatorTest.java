import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    @Test
    void add() {
        Calculator c = new Calculator();
        assertEquals(10, c.add(7,3));
    }

    @Test
    void subtract() {
        Calculator c = new Calculator();
        assertEquals(5, c.subtract(12, 7));
    }
    @Test
    void multiply() {
        Calculator c = new Calculator();
        assertEquals(21, c.multiply(7, 3));
    }
}