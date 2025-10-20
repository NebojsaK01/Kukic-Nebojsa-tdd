package library;

import java.util.*;
class MemoryBookRepository implements IBookRepository {
    private final Map<String, Book> books = new HashMap<>();

    @Override
    public Book findById(String id) {
        return books.get(id);
    }

    @Override
    public void save(Book book) {
        books.put(book.getId(), book);
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }
}

