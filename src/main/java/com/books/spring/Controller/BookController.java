package com.books.spring.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.net.URL;
import java.time.Duration;

import com.amazonaws.HttpMethod;
import com.books.spring.Model.BookModel;
import com.books.spring.Service.BookService;
import com.books.spring.Service.S3Service;

@RestController
@RequestMapping("/book")
public class BookController {

  @Autowired
  private BookService bookService;

  @Autowired
  private S3Service s3Service;

  @GetMapping("/all")
  public ResponseEntity<List<BookModel>> getAllBooks() {
    List<BookModel> books = bookService.getAllBooks();
    return books != null && !books.isEmpty() ? ResponseEntity.status(200).body(books)
        : ResponseEntity.status(404).build();
  }

  @GetMapping("/id/{id}")
  public ResponseEntity<BookModel> getBookById(@PathVariable UUID id) {
    Optional<BookModel> book = bookService.getBookById(id);
    return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(404).build());
  }

  @GetMapping("/owner/{owner}")
  public ResponseEntity<List<BookModel>> getBooksByOwner(@PathVariable UUID owner) {
    List<BookModel> books = bookService.getBooksByOwner(owner);
    return books != null && !books.isEmpty() ? ResponseEntity.status(200).body(books)
        : ResponseEntity.status(404).build();
  }

  @GetMapping("/search/{keyword}")
  public ResponseEntity<List<BookModel>> searchBooks(@PathVariable String keyword) {
    List<BookModel> books = bookService.searchBooks(keyword);
    return books != null && !books.isEmpty() ? ResponseEntity.status(200).body(books)
        : ResponseEntity.status(404).build();
  }

  @PostMapping("/add")
  public ResponseEntity<Object> addBook(@RequestPart("book") BookModel book,
      @RequestPart(value = "file", required = false) MultipartFile file) {
    try {
      // Upload file to S3 if provided
      if (file != null && !file.isEmpty()) {
        String fileName = s3Service.uploadFile(file);
        String fileUrl = s3Service.getFileUrl(fileName);
        book.setLink(fileUrl);
      }

      // Pass the book to the service layer
      BookModel createdBook = bookService.createBook(book);
      return ResponseEntity.status(201).body(createdBook);
    } catch (RuntimeException e) {
      // Handle the case where user is not found
      return ResponseEntity.status(400).body("Error: " + e.getMessage());
    } catch (Exception e) {
      // Handle any other exceptions
      return ResponseEntity.status(500).body("Unexpected error: " + e.getMessage());
    }
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<Object> updateBook(@PathVariable UUID id, @RequestPart("book") BookModel book,
      @RequestPart(value = "file", required = false) MultipartFile file) {
    try {
      // Get existing book to check if we need to remove old file
      Optional<BookModel> existingBook = bookService.getBookById(id);

      // Upload new file to S3 if provided
      if (file != null && !file.isEmpty()) {
        String fileName = s3Service.uploadFile(file);
        String fileUrl = s3Service.getFileUrl(fileName);
        book.setLink(fileUrl);

        // Delete old file if it exists
        if (existingBook.isPresent() && existingBook.get().getLink() != null) {
          String oldFileUrl = existingBook.get().getLink();
          String oldFileName = oldFileUrl.substring(oldFileUrl.lastIndexOf("/") + 1);
          s3Service.deleteFile(oldFileName);
        }
      } else if (existingBook.isPresent()) {
        // Keep existing link if no new file is provided
        book.setLink(existingBook.get().getLink());
      }

      BookModel updatedBook = bookService.updateBook(id, book);
      return ResponseEntity.status(200).body(updatedBook);
    } catch (RuntimeException e) {
      return ResponseEntity.status(400).body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Unexpected error: " + e.getMessage());
    }
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
    Optional<BookModel> book = bookService.getBookById(id);
    if (book.isPresent()) {
      // Delete file from S3 if it exists
      if (book.get().getLink() != null) {
        String fileUrl = book.get().getLink();
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        s3Service.deleteFile(fileName);
      }

      bookService.deleteBook(id);
      return ResponseEntity.status(204).build();
    }
    return ResponseEntity.status(404).build();
  }

  @GetMapping("/{id}/download-url")
  public ResponseEntity<Map<String, String>> getDownloadUrl(@PathVariable UUID id) {
    Optional<BookModel> bookOpt = bookService.getBookById(id);

    if (bookOpt.isPresent() && bookOpt.get().getLink() != null) {
      BookModel book = bookOpt.get();
      String fileUrl = book.getLink();
      String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

      // Generate presigned URL valid for 1 hour
      URL presignedUrl = s3Service.generatePresignedUrl(fileName, HttpMethod.GET, Duration.ofHours(1));

      Map<String, String> response = Map.of(
          "downloadUrl", presignedUrl.toString(),
          "expiresIn", "3600 seconds");

      return ResponseEntity.ok(response);
    }

    return ResponseEntity.notFound().build();
  }
}