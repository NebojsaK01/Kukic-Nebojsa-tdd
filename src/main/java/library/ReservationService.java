package library;

import java.util.*;

public class ReservationService {

    private final IBookRepository bookRepo;
    private final IReservationRepository reservationRepo;

    public ReservationService(IBookRepository bookRepo, IReservationRepository reservationRepo) {
        this.bookRepo = bookRepo;
        this.reservationRepo = reservationRepo;
    }

    /**
     * Reserve a book for a user.
     * Throws IllegalArgumentException if book not found.
     * Throws IllegalStateException if no copies available or user already reserved.
     */
    public void reserve(String userId, String bookId) {
        reserve(userId, bookId, false);
    }

    public void reservePriority(String userId, String bookId) {
        reserve(userId, bookId, true);
    }

    private final Map<String, Queue<String>> waitingLists = new HashMap<>(); // bookId -> queue of userIds

    private void reserve(String userId, String bookId, boolean isPriority) {
        Book book = bookRepo.findById(bookId);

        if (book == null) {
            throw new IllegalArgumentException("Book not found");
        }

        if (reservationRepo.existsByUserAndBook(userId, bookId)) {
            throw new IllegalStateException("The user already reserved this book");
        }

        // Only decrease copies if available
        if (book.getCopiesAvailable() > 0) {
            Reservation reservation = new Reservation(userId, bookId);
            reservationRepo.save(reservation);
            book.setCopiesAvailable(book.getCopiesAvailable() - 1);
            bookRepo.save(book);
        }
        else if (isPriority) {
            //Add to the waiting List
            waitingLists.computeIfAbsent(bookId, k -> new LinkedList<>()).add(userId);
            // Still create reservation for tracking
            Reservation reservation = new Reservation(userId, bookId);
            reservationRepo.save(reservation);
        }
        else {
            throw new IllegalStateException("No copies available");
        }
    }

    /**
     * Cancel an existing reservation for a user.
     * Throws IllegalArgumentException if no such reservation exists.
     */
    public void cancel(String userId, String bookId) {
        // TODO: Implement using TDD
        if (!reservationRepo.existsByUserAndBook(userId, bookId)) {
            throw new IllegalArgumentException("No reservations found");
        }

        reservationRepo.delete(userId, bookId);

        Book book = bookRepo.findById(bookId);
        book.setCopiesAvailable(book.getCopiesAvailable()+  1);
        bookRepo.save(book);
    }

    /**
     * List all active reservations for a given user.
     */
    public List<Reservation> listReservations(String userId) {
        // TODO: Implement using TDD
        return reservationRepo.findByUser(userId);

    }

    /**
     * list all reservations for a book.
     */
    public List<Reservation> listReservationsForBook(String bookId) {
        // TODO: Implement using TDD
        return reservationRepo.findByBook(bookId);
    }


}

