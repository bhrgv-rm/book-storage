package com.books.spring.Repository;

import com.books.spring.Model.BookModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookModel, UUID> {

  boolean existsByTitle(String title);

  boolean existsByAuthor(String author);

  boolean existsByLink(String link);

  BookModel findByTitle(String title);

  BookModel findByAuthor(String author);

  BookModel findByLink(String link);

  @Query("SELECT b FROM BookModel b WHERE b.owner = ?1")
  List<BookModel> findByOwner(UUID owner);

  @Query("SELECT b FROM BookModel b WHERE b.title LIKE %?1% OR b.author LIKE %?1% OR b.link LIKE %?1%")
  List<BookModel> searchBooks(String keyword);

}
