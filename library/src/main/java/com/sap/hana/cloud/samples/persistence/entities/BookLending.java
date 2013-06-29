package com.sap.hana.cloud.samples.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@Table(name = "T_BOOK_LENDING")
@NamedQueries({
@NamedQuery(name = "lendingsByUser", query = "select l from BookLending l where l.user = :user"),
@NamedQuery(name = "lendingByBook", query = "select l from BookLending l where l.lendedBook = :lendedBook"),
@NamedQuery(name = "lendingsByUserAndBook", query = "select l from BookLending l where l.user = :user and l.lendedBook = :lendedBook"),
@NamedQuery(name = "deleteLendingByUserAndBook", query = "delete from BookLending l where l.user = :user and l.lendedBook = :lendedBook")
})
public class BookLending {

	@Id
    @GeneratedValue
    private Long id;

	private Book lendedBook;

	private LibraryUser user;

	@Basic
	private int remainingDays;

	@Basic
	private int rating;


	public int getRemainingDays() {
		return remainingDays;
	}

	public void setRemainingDays(int remainingDays) {
		this.remainingDays = remainingDays;
	}

	public Book getLendedBook() {
		return lendedBook;
	}

	public void setLendedBook(Book lendedBook) {
		this.lendedBook = lendedBook;
	}

	public LibraryUser getUser() {
		return user;
	}

	public void setUser(LibraryUser user) {
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

}
