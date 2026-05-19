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
	private boolean isIncome; // メールアドレス

	// コンストラクタ
	public Genre() {
	}

	public Genre(String genreName, boolean isIncome) {
		this.genreName = genreName;

		this.isIncome = isIncome;
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

	public boolean isIncome() {
		return isIncome;
	}

	public void setIncome(boolean isIncome) {
		this.isIncome = isIncome;
	}

}
