package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "genres")
public class Genre {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; // ジャンルID

	@Column(name = "genre_name")
	private String genreName; // 名前

	@Column(name = "is_income")
	private boolean isIncome; // 収入か支出か

	private String comments; // 科目の概要など

	// コンストラクタ
	public Genre() {
	}

	public Genre(String genreName, boolean isIncome, String comments) {
		this.genreName = genreName;

		this.isIncome = isIncome;
		this.comments = comments;
	}

	public Integer getId() {
		return id;
	}

	public Integer setId() {
		return id;
	}

	public String getGenreName() {
		return genreName;
	}

	public void setGenreName(String genreName) {
		this.genreName = genreName;
	}

	public boolean getIsIncome() {
		return isIncome;
	}

	public void setIncome(boolean isIncome) {
		this.isIncome = isIncome;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}
