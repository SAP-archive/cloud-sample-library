package com.sap.hana.cloud.samples.persistence.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "T_BOOK")
@NamedQueries({
@NamedQuery(name = "AllBooks", query = "select b from Book b"),
@NamedQuery(name = "bookByTitleAndAuthor", query = "select b from Book b where b.bookName = :bookName and b.authorName = :authorName"),
@NamedQuery(name = "removeBookByTitleAndAuthor", query = "delete from Book b where b.bookName = :bookName and b.authorName = :authorName"),
})
public class Book implements Serializable {

	private static final long serialVersionUID = 5162585057257322348L;

	@Id
    @GeneratedValue
    private Long id;

    private String isbn;

	@Basic
	private String bookName;

	@Basic
    private String authorName;

	@Basic
    private float bookRating;

	@Basic
	private int numberOfRatings;

	@Basic
	private long sumOfRatings;

	@Basic
    private boolean reserved;

	@Basic
	private String reservedByUser;

	@Basic
	private String reservedByUserId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date reservedUntil;



	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public float getBookRating() {
		return bookRating;
	}

	public void setBookRating(float bookRating) {
		this.bookRating = bookRating;
	}

	public Long getId() {
		return id;
	}

	public boolean isReserved() {
		return reserved;
	}

	public void setReserved(boolean reserved) {
		this.reserved = reserved;
	}

	public String getReservedBy() {
		return reservedByUser;
	}

	public void setReservedBy(String reservedBy) {
		this.reservedByUser = reservedBy;
	}

	public Date getReservedUntil() {
		return reservedUntil;
	}

	public void setReservedUntil(Date reservedUntil) {
		this.reservedUntil = reservedUntil;
	}

	public int getNumberOfRatings() {
		return numberOfRatings;
	}

	public void setNumberOfRatings(int numberOfRatings) {
		this.numberOfRatings = numberOfRatings;
	}

	public long getSumOfRatings() {
		return sumOfRatings;
	}

	public void setSumOfRatings(long sumOfRatings) {
		this.sumOfRatings = sumOfRatings;
	}

	public String getReservedByUserId() {
		return reservedByUserId;
	}

	public void setReservedByUserId(String reservedByUserId) {
		this.reservedByUserId = reservedByUserId;
	}

}
