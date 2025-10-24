package library;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ReservationService {

    private final IBookRepository bookRepo;
    private final IReservationRepository reservationRepo;
    private final Map<String, Queue<String>> waitingLists = new HashMap<>();

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


    private void reserve(String userId, String bookId, boolean isPriority) {
        Book book = bookRepo.findById(bookId);

        if (book == null) {
            throw new IllegalArgumentException("Book not found");
        }

        if (reservationRepo.existsByUserAndBook(userId, bookId)) {
            throw new IllegalStateException("The user already reserved this book");
        }

        if (book.getCopiesAvailable() > 0) {
            // Normal reservation - decrease copies
            Reservation reservation = new Reservation(userId, bookId);
            reservationRepo.save(reservation);
            book.setCopiesAvailable(book.getCopiesAvailable() - 1);
            bookRepo.save(book);
        } else if (isPriority) {
            // Priority user - add to waiting list
            waitingLists.computeIfAbsent(bookId, k -> new LinkedList<>()).add(userId);
            // Still create reservation for tracking
            Reservation reservation = new Reservation(userId, bookId);
            reservationRepo.save(reservation);
        } else {
            throw new IllegalStateException("No copies available");
        }
    }

    /**
     * Cancel an existing reservation for a user.
     * Throws IllegalArgumentException if no such reservation exists.
     */
    public void cancel(String userId, String bookId) {
        if (!reservationRepo.existsByUserAndBook(userId, bookId)) {
            throw new IllegalArgumentException("No reservations found");
        }

        // Get book BEFORE deleting reservation to check current state
        Book book = bookRepo.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found");
        }

        // Store whether book had available copies before cancellation
        boolean hadAvailableCopies = book.getCopiesAvailable() > 0;

        reservationRepo.delete(userId, bookId);

        // Check if there are users waiting for this book
        Queue<String> waitingList = waitingLists.get(bookId);
        if (waitingList != null && !waitingList.isEmpty() && !hadAvailableCopies) {
            // Book had 0 copies and has waiting users - assign to first waiting user
            String nextUserId = waitingList.poll();
            // Don't create new reservation - waiting user already has one
            // Just remove them from waiting list (already done by poll())
            System.out.println("Assigned book to waiting user: " + nextUserId);
        } else {
            // Either no waiting list OR book had available copies - increase copies
            book.setCopiesAvailable(book.getCopiesAvailable() + 1);
            bookRepo.save(book);
        }

        // Clean up empty waiting lists
        if (waitingList != null && waitingList.isEmpty()) {
            waitingLists.remove(bookId);
        }
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

