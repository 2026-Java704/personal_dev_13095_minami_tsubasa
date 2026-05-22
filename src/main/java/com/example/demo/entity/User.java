package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; // 顧客ID

	@Column(name = "user_name")
	private String userName; // 名前

	private String email; // メールアドレス

	// パスワードを追加
	private String password; // メールアドレス

	// コンストラクタ
	public User() {
	}

	public User(String userName, String email, String password) {
		this.userName = userName;

		this.email = email;
		this.password = password;
	}

	// ゲッター
	public Integer getId() {
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public String getEmail() {
		return email;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}
}
