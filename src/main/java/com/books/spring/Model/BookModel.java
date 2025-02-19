package com.books.spring.Model;

import java.util.UUID;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class BookModel {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private String title;

  private String author;

  @ManyToOne
  @JoinColumn(name = "ownerId")
  public UserModel owner;

  public boolean privateBook;

  @Column(unique = true)
  private String link;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public UserModel getOwner() {
    return owner;
  }

  public void setOwner(UserModel owner) {
    this.owner = owner;
  }

  public boolean isPrivateBook() {
    return privateBook;
  }

  public void setPrivateBook(boolean privateBook) {
    this.privateBook = privateBook;
  }
}
