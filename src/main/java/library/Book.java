package library;

public class Book {
    private String id;
    private String title;
    private int copiesAvailable;

    // Constructor
    public Book(String id, String title, int copiesAvailable) {
        this.id = id;
        this.title = title;
        this.copiesAvailable = copiesAvailable;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getCopiesAvailable() {
        return copiesAvailable;
    }

    // Single Setter
    public void setCopiesAvailable(int copiesAvailable) {
        this.copiesAvailable = copiesAvailable;
    }

}
