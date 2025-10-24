package library;

import org.junit.jupiter.api.Test;

import java.util.List;

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


    @Test
    void increaseCopiesAvailableWhenCancellingReservation() {
        IBookRepository bookRepo = new MemoryBookRepository();
        IReservationRepository reservationRepo = new MemoryReservationRepository();
        ReservationService service = new ReservationService(bookRepo, reservationRepo);

        // Create book - id: 1, title: The Bible, copies Available: 10

        Book book = new Book("1", "The Bible", 10);
        bookRepo.save(book); // save the book

        // confirm if copies goes from 10 --> 9 --- same as b4
        service.reserve("Nebojsa",  "1");
        assertEquals(9, book.getCopiesAvailable());

        // Cancel the reservation
        service.cancel("Nebojsa", "1");
        // confirm if copies goes from 9 --> 10
        assertEquals(10, book.getCopiesAvailable());

        // confirm if book has been deleted.
        assertFalse(reservationRepo.existsByUserAndBook("Nebojsa", "1"));
    }


    @Test
    void tryingToCancelReservationWhenThereIsNoReservation() {
        IBookRepository bookRepo = new MemoryBookRepository();
        IReservationRepository reservationRepo = new MemoryReservationRepository();
        ReservationService service = new ReservationService(bookRepo, reservationRepo);

        // Create book - id: 1, title: The Bible, copies Available: 10

        Book book = new Book("1", "The Bible", 10);
        bookRepo.save(book); // save the book

        // No reservation made here.

        // Cancelling the reservation without actually reserving.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.cancel("Nebojsa", "1"));
        assertEquals("No reservations found", exception.getMessage());
    }

    @Test
    void listingTheReservationsForGivenUser() {
        IBookRepository bookRepo = new MemoryBookRepository();
        IReservationRepository reservationRepo = new MemoryReservationRepository();
        ReservationService service = new ReservationService(bookRepo, reservationRepo);

        // Create books
        Book book1 = new Book("1", "The Bible", 10);
        Book book2 = new Book("2", "Java Programming", 5);
        bookRepo.save(book1);
        bookRepo.save(book2);

        // Make reservations for user "Nebojsa"
        service.reserve("Nebojsa", "1");
        service.reserve("Nebojsa", "2");

        // List reservations for Nebojsa
        List<Reservation> reservations = service.listReservations("Nebojsa");

        // Should have 2 reservations
        assertEquals(2, reservations.size());

        // Verify both book IDs are in the list
        List<String> bookIds = reservations.stream()
                .map(Reservation::getBookId)
                .toList();
        assertTrue(bookIds.contains("1"));
        assertTrue(bookIds.contains("2"));
    }

    @Test
    void listReservationsForBook_ShouldReturnBookReservations() {
        IBookRepository bookRepo = new MemoryBookRepository();
        IReservationRepository reservationRepo = new MemoryReservationRepository();
        ReservationService service = new ReservationService(bookRepo, reservationRepo);

        // Create books
        Book book1 = new Book("1", "The Bible", 10);
        Book book2 = new Book("2", "Java Programming", 5);
        bookRepo.save(book1);
        bookRepo.save(book2);

        // Multiple users reserve the same book
        service.reserve("Nebojsa", "1");
        service.reserve("Alice", "1");
        service.reserve("Bob", "2");  // Different book

        // List reservations for book "1"
        List<Reservation> reservations = service.listReservationsForBook("1");

        // Debug output
        System.out.println("Reservations for book 1: " + reservations.size());
        for (Reservation r : reservations) {
            System.out.println(" - User: " + r.getUserId() + ", Book: " + r.getBookId());
        }

        // Should have 2 reservations for book "1"
        assertEquals(2, reservations.size());

        // Verify both users reserved book "1"
        List<String> userIds = reservations.stream()
                .map(Reservation::getUserId)
                .toList();
        assertTrue(userIds.contains("Nebojsa"));
        assertTrue(userIds.contains("Alice"));

        // All reservations should be for book "1"
        assertTrue(reservations.stream().allMatch(r -> r.getBookId().equals("1")));

    }

    @Test
    void listReservationsForBook_ShouldReturnEmptyList_WhenNoReservations() {
        IBookRepository bookRepo = new MemoryBookRepository();
        IReservationRepository reservationRepo = new MemoryReservationRepository();
        ReservationService service = new ReservationService(bookRepo, reservationRepo);

        Book book = new Book("1", "The Bible", 10);
        bookRepo.save(book);

        // List reservations for book with no reservations
        List<Reservation> reservations = service.listReservationsForBook("1");

        System.out.println("Reservations for book with no reservations: " + reservations.size());

        assertTrue(reservations.isEmpty());
    }

    @Test
    void listReservationsForBook_ShouldOnlyReturnCurrentBookReservations() {
        IBookRepository bookRepo = new MemoryBookRepository();
        IReservationRepository reservationRepo = new MemoryReservationRepository();
        ReservationService service = new ReservationService(bookRepo, reservationRepo);

        Book book1 = new Book("1", "The Bible", 10);
        Book book2 = new Book("2", "Java Programming", 5);
        bookRepo.save(book1);
        bookRepo.save(book2);

        // User reserves both books
        service.reserve("Nebojsa", "1");
        service.reserve("Nebojsa", "2");

        // List only reservations for book "1"
        List<Reservation> book1Reservations = service.listReservationsForBook("1");

        System.out.println("Book 1 reservations: " + book1Reservations.size());

        assertEquals(1, book1Reservations.size());
        assertEquals("1", book1Reservations.get(0).getBookId());
        assertEquals("Nebojsa", book1Reservations.get(0).getUserId());
    }

    @Test
    void boundaryTestForReservingLastCopy() {
        // essentially made b4 but not specifically for last copy:
        // Set up the repos and service.
        IBookRepository bookRepo = new MemoryBookRepository();
        IReservationRepository reservationRepo = new MemoryReservationRepository();
        ReservationService service = new ReservationService(bookRepo, reservationRepo);

        // Create book - id: 1, title: The Bible, copies Available: 1
        Book book = new Book("1", "The Bible", 1); // last copy
        bookRepo.save(book); // save the book


        // reserve the book
        service.reserve("Nebojsa",  "1");

        // check if reservation was created + saved.
        assertTrue(reservationRepo.existsByUserAndBook("Nebojsa", "1"));

        //check if the copies goes from 10 ->> 9
        assertEquals(0, book.getCopiesAvailable());
    }

    @Test
    void priorityUser_CanReserve_WhenNoCopiesAvailable() {
        IBookRepository bookRepo = new MemoryBookRepository();
        IReservationRepository reservationRepo = new MemoryReservationRepository();
        ReservationService service = new ReservationService(bookRepo, reservationRepo);

        // Create book with no copies
        Book book = new Book("1", "The Saga of Bobby", 0);
        bookRepo.save(book);

        // Priority user should be able to reserve despite no copies of book
        service.reservePriority("PriorityUser", "1");

        // confirm if reservation exists with user + book.
        assertTrue(reservationRepo.existsByUserAndBook("PriorityUser", "1"));
    }

    @Test
    void cancellation_ShouldAssignToFirstWaitingUser() {
        IBookRepository bookRepo = new MemoryBookRepository();
        IReservationRepository reservationRepo = new MemoryReservationRepository();
        ReservationService service = new ReservationService(bookRepo, reservationRepo);

        // Create book with 0 copies
        Book book = new Book("1", "Popular Book", 0);
        bookRepo.save(book);

        // Priority users join waiting list
        service.reservePriority("PriorityUser1", "1");
        service.reservePriority("PriorityUser2", "1");

        // Regular user cancels (simulating copy return)
        // We need a way to trigger waiting list processing
        // This test will fail until we implement the waiting list logic

    }

    @Test
    void cancellation_ShouldAssignBookToFirstWaitingUser() {
        IBookRepository bookRepo = new MemoryBookRepository();
        IReservationRepository reservationRepo = new MemoryReservationRepository();
        ReservationService service = new ReservationService(bookRepo, reservationRepo);

        // Create book with 0 copies
        Book book = new Book("1", "Popular Book", 0);
        bookRepo.save(book);

        // Priority users join waiting list
        service.reservePriority("PriorityUser1", "1");
        service.reservePriority("PriorityUser2", "1");

        // Someone cancels a reservation (simulate copy return)
        // We need to add a reservation first to cancel
        // This reveals we need to modify our approach
        service.reserve("RegularUser", "1"); // This will fail with 0 copies

        fail("Need to redesign - current approach doesn't handle waiting list assignment");
    }

}


