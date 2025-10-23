package library;

import java.util.*;
interface IReservationRepository {
    void save(Reservation reservation);
    boolean existsByUserAndBook(String userId, String bookId);
    List<Reservation> findByUser(String userId);
    List<Reservation> findByBook(String bookId); // NEW METHOD
    void delete(String userId, String bookId);
}

