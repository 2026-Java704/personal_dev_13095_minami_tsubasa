package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Genre;
import com.example.demo.model.Account;
import com.example.demo.repository.GenreRepository;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.UserRepository;

@Controller
public class genreController {

	/*
	 *  repositoryフォルダにあるインターフェースを定義し、
	 *  情報を更新したりする際に必要になります。
	 */
	private final UserRepository userRepository;
	private final GenreRepository genreRepository;
	private final ItemRepository itemRepository;
	private final Account account;

	public genreController(
			UserRepository userRepository,
			GenreRepository genreRepository,
			ItemRepository itemRepository,
			Account account) {
		this.userRepository = userRepository;
		this.genreRepository = genreRepository;
		this.itemRepository = itemRepository;
		this.account = account;
	}

	// ユーザーの計上記録をカレンダーに渡す
	@GetMapping("/genres")
	public String genre(Model model) {

		List<Genre> genreList = genreRepository.findAll();

		model.addAttribute("genres", genreList);
		model.addAttribute("userName", account.getName());
		return "genres";
	}

	@GetMapping("/genres/add")
	public String genreAdd(Model model) {

		model.addAttribute("userName", account.getName());
		return "addGenres";
	}

	@PostMapping("/genres/add")
	public String genreStore(
			@RequestParam(defaultValue = "") String genreName,
			@RequestParam(defaultValue = "") boolean isIncome,
			@RequestParam(defaultValue = "") String comments,
			Model model) {

		Genre genre = new Genre(genreName, isIncome, comments);

		if (genreName == null) {
			model.addAttribute("inputErr", "科目名を入力してください");

			return "addGenres";
		}

		genreRepository.save(genre);

		return "redirect:/genres";
	}

	// 更新画面表示
	@GetMapping("/genres/{id}/edit")
	public String genreEdit(@PathVariable Integer id, Model model) {
		Genre genre = genreRepository.findById(id).get();
		model.addAttribute("genres", genre);

		return "editGenres";
	}

	@PostMapping("/genres/{id}/edit")
	public String update(
			// edit.htmlの更新情報をフォームから受ける

			/*
			 * 復習：PathVariableは引き渡された情報をURLに含めるために使う。
			 * 情報に応じてリンク分け・整理が可能
			 * */
			@PathVariable Integer id, // 主キー（商品ID）を引数で取る

			// エラー時に通常のreturnだとIDの問題が発生するため、
			// リダイレクト時の値を受け取ってエラーを表示させる。
			@RequestParam(defaultValue = "入力項目に不足があります。") String inputErr,
			@RequestParam(defaultValue = "") String genreName,
			@RequestParam(defaultValue = "") boolean isIncome,
			@RequestParam(defaultValue = "") String comments,

			Model model, RedirectAttributes redirectAttributes) {

		// 変更された科目IDを使う
		Genre genre = genreRepository.findById(id).get();

		if (genreName == null) {
			redirectAttributes.addFlashAttribute("inputErr", inputErr);

			return "redirect:/items/{id}/edit";
		}

		Genre genreEdit = new Genre(genreName, isIncome, comments);

		genreRepository.save(genreEdit);
		return "redirect:/genres";
	}
}
