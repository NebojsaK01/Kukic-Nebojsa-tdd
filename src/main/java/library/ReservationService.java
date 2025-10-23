package library;

import java.util.List;
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
        // TODO: Implement using TDD
        Book book = bookRepo.findById(bookId);

        if (book == null) {
            throw new IllegalArgumentException("Book not found");
        }

        if (book.getCopiesAvailable() <= 0) {
            throw new IllegalStateException("No copies available");
        }

        if (reservationRepo.existsByUserAndBook(userId, bookId)) {
            throw new IllegalStateException("The user already reserved this book");
        }

        Reservation reservation = new Reservation(userId, bookId);
        reservationRepo.save(reservation);

        book.setCopiesAvailable(book.getCopiesAvailable() - 1);
        bookRepo.save(book);
    }

    /**
     * Cancel an existing reservation for a user.
     * Throws IllegalArgumentException if no such reservation exists.
     */
    public void cancel(String userId, String bookId) {
        // TODO: Implement using TDD
        Book book = bookRepo.findById(bookId);
        book.setCopiesAvailable(book.getCopiesAvailable()+  1);
        bookRepo.save(book);


    }

    /**
     * List all active reservations for a given user.
     */
    public List<Reservation> listReservations(String userId) {
        // TODO: Implement using TDD
        return null;
    }

    /**
     * list all reservations for a book.
     */
    public List<Reservation> listReservationsForBook(String bookId) {
        // TODO: Implement using TDD
        return null;
    }
}

