package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "items")
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; // ID自動採番

	@Column(name = "item_name")
	private String itemName; // 名前

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "genre_id")
	private Genre genre;

	private Integer price; // 金額

	@Column(name = "add_date")
	private LocalDate addDate;

	private String comment;

	// コンストラクタ
	public Item() {
	}

	public Item(String itemName, User user, Genre genre,
			Integer price, LocalDate addDate, String Comment) {
		this.itemName = itemName;
		this.user = user;
		this.genre = genre;
		this.price = price;
		this.addDate = addDate;
		this.comment = comment;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getId() {
		return id;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public LocalDate getAddDate() {
		return addDate;
	}

	public void setAddDate(LocalDate addDate) {
		this.addDate = addDate;
	}

	public Genre getGenre() {
		return genre;
	}

	public void setGenre(Genre genre) {
		this.genre = genre;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
