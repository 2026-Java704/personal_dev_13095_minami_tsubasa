package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "items")
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; // 顧客ID

	@Column(name = "item_name")
	private String itemName; // 名前

	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "genre_id")
	private Integer genreId;

	private Integer price; // 金額

	@Column(name = "add_date")
	private LocalDate addDate;

	private String comment;

	// コンストラクタ
	public Item() {
	}

	public Item(String itemName, Integer genreId,
			Integer price, LocalDate addDate, String Comment) {
		this.itemName = itemName;
		this.genreId = genreId;
		this.price = price;
		this.addDate = addDate;
		this.comment = comment;
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

	public Integer getGenreId() {
		return genreId;
	}

	public void setGenreId(Integer genreId) {
		this.genreId = genreId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
