package library;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReservationServiceTest {

    @Test
    void userCanReserveIfCopiesAvailable() {

        // Set up the repos and service.
        IBookRepository bookRepo = new MemoryBookRepository();
        IReservationRepository reservationRepo = new MemoryReservationRepository();
        ReservationService service = new ReservationService(bookRepo, reservationRepo);

        // Create book - id: 1, title: The Bible, copies Available: 10
        Book book = new Book("1", "The Bible", 10);
        bookRepo.save(book); // save the book


        // reserve the book
        service.reserve("Nebojsa",  "1");

        // check if reservation was created + saved.
        assertTrue(reservationRepo.existsByUserAndBook("Nebojsa", "1"));
    }
}
