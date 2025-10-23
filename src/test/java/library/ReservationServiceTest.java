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

        //check if the copies goes from 10 ->> 9
        assertEquals(9, book.getCopiesAvailable());
    }

    @Test
    void userCannotReserveIfCopiesNotAvailable() {

        // Set up the repos and service.
        IBookRepository bookRepo = new MemoryBookRepository();
        IReservationRepository reservationRepo = new MemoryReservationRepository();
        ReservationService service = new ReservationService(bookRepo, reservationRepo);

        // Create book - id: 2, title: WW2 History, copies Available: 0
        Book book = new Book("2", "WW2 History", 0);
        bookRepo.save(book); // save the book

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.reserve("Nebojsa", "2"));

        assertEquals("No copies available", exception.getMessage());
    }

    @Test
    void userCannotReserveSameBookAlreadyReserved() {
        IBookRepository bookRepo = new MemoryBookRepository();
        IReservationRepository reservationRepo = new MemoryReservationRepository();
        ReservationService service = new ReservationService(bookRepo, reservationRepo);

        // Create book - id: 1, title: The Bible, copies Available: 10
        // can use same details for different tests as they're all in isolation
        Book book = new Book("1", "The Bible", 10);
        bookRepo.save(book); // save the book


        // first reservation
        service.reserve("Nebojsa",  "1");

        // second reservation
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.reserve("Nebojsa", "1"));

        assertEquals("The user already reserved this book", exception.getMessage());
    }

    @Test
    void userCannotReserveIfBookNotFound() {
        IBookRepository bookRepo = new MemoryBookRepository();
        IReservationRepository reservationRepo = new MemoryReservationRepository();
        ReservationService service = new ReservationService(bookRepo, reservationRepo);

        // validate if a book exists - can lead to CRASH.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.reserve("Nebojsa", "NON_EXISTENT_BOOK"));

        assertEquals("Book not found", exception.getMessage());
    }
}
