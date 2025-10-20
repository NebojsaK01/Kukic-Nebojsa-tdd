package library;

public class Reservation {
    private String userId;
    private String bookId;

    // Constructors
    public Reservation(String userId, String bookId) {
        this.userId = userId;
        this.bookId = bookId;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getBookId() {
        return bookId;
    }

}
