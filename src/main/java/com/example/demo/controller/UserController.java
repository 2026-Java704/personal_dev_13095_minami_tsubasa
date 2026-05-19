
package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

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

	@GetMapping("/register")
	public String register() {
		// セッション情報を全てクリアする
		// →リセット
		session.invalidate();

		return "userForm";
	}

	// ログインを実行
	@PostMapping("/login")
	public String login(
			@RequestParam String email,
			@RequestParam String password,
			Model model) {
		// まずはEmailが空の場合にエラーとする
		if (email == null || email.length() == 0) {
			// ログインHTMLのthymeleafの${message}へ打ち変えs
			model.addAttribute("message", "メールアドレスを入力してください");
			return "login";
		}

		List<User> findAccount = userRepository.findByEmailAndPasswordEquals(email, password);
		if (findAccount.size() == 1) {

			// セッション管理されたアカウント情報に名前をセット
			// 検索されたリストは１件なので、０番目をとる。
			//			account.setName(findAccount.get(0).getName());
			//			account.setId(findAccount.get(0).getId());

			// items.htmlに引き継ぎ、その後の注文画面に引き継ぐ

			return "items";
		} else {
			// 情報が違うためとどまる
			model.addAttribute("message", "メールアドレスまたはパスワードが違います");
			return "login";
		}
	}

	// アカウント登録処理
	@PostMapping("/register")
	public String store(
			@RequestParam(defaultValue = "") String name,
			@RequestParam(defaultValue = "") String address,
			@RequestParam(defaultValue = "") String tel,
			@RequestParam(defaultValue = "") String email,
			@RequestParam(defaultValue = "") String password,
			@RequestParam(defaultValue = "") String passwordConfilm,
			Model model) {

		User user = new User(name, email, password);

		// エラーの情報を格納する
		List<String> accountErr = new ArrayList();

		// 名前は入力した？
		if ("".equals(name)) {
			accountErr.add("名前は必須です"); // 文字数を渡す
			// htmlではhidden表示の部分で表示し、判定する。
		} else if (!("".equals(name))) {
			model.addAttribute("setName", name);
		}

		if ("".equals(email)) {
			accountErr.add("メールアドレスは必須です"); // 文字数を渡す
			// htmlではhidden表示の部分で表示し、判定する。
		} else if (!("".equals(email))) {
			model.addAttribute("setEmail", email);
		}

		if ("".equals(password)) {
			accountErr.add("パスワードは必須です"); // 文字数を渡す
			// htmlではhidden表示の部分で表示し、判定する。
		} else if (!(passwordConfilm.equals(password))) {
			accountErr.add("パスワードは確認用と一致する必要があります");
			model.addAttribute("setPassword", password);
		} else if (!("".equals(password))) {
			model.addAttribute("setPassword", password);
		}

		List<User> findEmail = userRepository.findByEmailEquals(email);
		if (findEmail.size() > 0) {
			accountErr.add("登録済みのメールアドレスです");
		}

		if (accountErr.size() > 0) {
			model.addAttribute("accountErr", accountErr);
			return "userForm";
		}

		userRepository.save(user);
		// リダイレクト
		return "redirect:/login";
	}
}
