package com.books.spring.Service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.books.spring.Model.BookModel;
import com.books.spring.Model.UserModel;
import com.books.spring.Repository.BookRepository;
import com.books.spring.Repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookService {
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private UserRepository userRepository;

  public List<BookModel> getAllBooks() {
    return bookRepository.findAll();
  }

  public List<BookModel> searchBooks(String keyword) {
    return bookRepository.searchBooks(keyword);
  }

  public Optional<BookModel> getBookById(UUID id) {
    return bookRepository.findById(id);
  }

  public BookModel getBookByLink(String link) {
    return bookRepository.findByLink(link);
  }

  public List<BookModel> getBooksByOwner(UUID owner) {
    return bookRepository.findByOwner(owner);
  }

  public BookModel createBook(BookModel book) {
    Optional<UserModel> owner = userRepository.findById(book.getOwner().getId());
    book.setOwner(owner.orElseThrow(() -> new RuntimeException("Owner not found")));
    book.setCreatedAt(LocalDateTime.now());
    book.setUpdatedAt(LocalDateTime.now());
    return bookRepository.save(book);
  }

  public BookModel updateBook(UUID id, BookModel bookDetails) {
    BookModel book = bookRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Book not found"));
    book.setTitle(bookDetails.getTitle());
    book.setAuthor(bookDetails.getAuthor());
    book.setLink(bookDetails.getLink());
    book.setUpdatedAt(LocalDateTime.now());
    return bookRepository.save(book);
  }

  public void deleteBook(UUID id) {
    bookRepository.deleteById(id);
  }
}