import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    @Test
    void add() {
        Calculator c = new Calculator();
        assertEquals(10, c.add(7,3));
    }
}