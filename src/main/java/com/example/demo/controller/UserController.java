
package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.User;
import com.example.demo.model.Account;
import com.example.demo.repository.UserRepository;

@Controller
public class UserController {

	@Autowired
	private PasswordEncoder passwordEncoder;

	// セッションに必要な初期化
	private final UserRepository userRepository;
	private final HttpSession session;
	private final Account account;

	/* 
	 * アカウントコントローラクラスのコンストラクタ
	 * Accountはデータベースに直接関連しないオブジェクトのため
	 * modelクラスで管理する。
	*/
	public UserController(HttpSession session,
			UserRepository userRepository,
			Account account,
			PasswordEncoder passwordEncoder) {
		this.session = session;
		this.account = account;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
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
			// パスワードがハッシュ化されたものとマッチするか検証
		}

		List<User> findAccount = userRepository.findByEmailEquals(email);
		// mapを使って、findEmailでヒットした０行目のメールアドレスをキーに
		// ハッシュ化されたPWを取得
		String savedPW = findAccount.get(0).getPassword();

		//		List<User> findAccount = userRepository.findByEmailAndPasswordEquals(email, password);
		if (passwordEncoder.matches(password, savedPW)) {

			// セッション管理されたアカウント情報に名前をセット
			// 検索されたリストは１件なので、０番目をとる。
			account.setName(findAccount.get(0).getUserName());
			account.setEmail(findAccount.get(0).getEmail());
			account.setId(findAccount.get(0).getId());

			// items.htmlに引き継ぎ、その後の注文画面に引き継ぐ

			return "redirect:/items";
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
			@RequestParam(defaultValue = "") String email,
			@RequestParam(defaultValue = "") String password,
			@RequestParam(defaultValue = "") String passwordConfilm,
			Model model) {

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

		// 全てを突破したら…
		String hashedPW = passwordEncoder.encode(password);
		User user = new User(name, email, hashedPW);

		userRepository.save(user);
		// リダイレクト
		return "redirect:/login";
	}

	// ログイン画面を表示
	@GetMapping("/account")
	public String accountGet(Model model) {

		// 表示させる
		model.addAttribute("userId", account.getId());
		model.addAttribute("userName", account.getName());
		model.addAttribute("userEmail", account.getEmail());

		return "accountInfo";
	}

	// ログイン画面を表示
	@PostMapping("/account/update")
	public String accountSet(
			@RequestParam(defaultValue = "") String userName,
			@RequestParam(defaultValue = "") String userEmail,
			Model model) {

		Integer userId = account.getId();

		User user = userRepository.findById(userId).get();

		user.setUserName(userName);
		user.setUserEmail(userEmail);

		userRepository.save(user);

		return "redirect:/items";
	}
}
