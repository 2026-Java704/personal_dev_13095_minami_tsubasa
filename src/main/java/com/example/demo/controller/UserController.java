
package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.repository.UserRepository;

/*
 * ルートディレクトリまたは/login時のページの処理を
 * 出力します。
 * 
 * 対象は「login.html」
 * 
 * 名前が入力された際・未入力時(name = null)の時の
 * login.htmlにエラーを打ち返します。
 * 
 * ログインボタンが押された際のPostリクエストで、
 * Accountのクラスに対して、setNameで名前を保持し、
 * セッションでログアウトまで持たせます。
 * 
 * 
 */

@Controller
public class UserController {
	// セッションに必要な初期化
	private final UserRepository userRepository;
	private final HttpSession session;
	//		private final Account account;

	/* 
	 * アカウントコントローラクラスのコンストラクタ
	 * Accounはデータベースに直接関連しないオブジェクトのため
	 * modelクラスで管理する。
	*/
	public UserController(HttpSession session, UserRepository userRepository) {
		this.session = session;
		//			this.account = account;
		this.userRepository = userRepository;
	}

	// ログイン画面を表示
	@GetMapping({ "/", "/login", "/logout" })
	public String index() {
		// セッション情報を全てクリアする
		// →リセット
		session.invalidate();

		return "login";
	}
}
