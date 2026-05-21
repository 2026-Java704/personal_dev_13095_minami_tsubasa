package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Genre;
import com.example.demo.entity.Item;
import com.example.demo.entity.User;
import com.example.demo.model.Account;
import com.example.demo.repository.GenreRepository;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.UserRepository;

@Controller
public class ItemController {

	/*
	 *  repositoryフォルダにあるインターフェースを定義し、
	 *  情報を更新したりする際に必要になります。
	 */
	private final UserRepository userRepository;
	private final GenreRepository genreRepository;
	private final ItemRepository itemRepository;
	private final Account account;

	public ItemController(
			UserRepository userRepository,
			GenreRepository genreRepository,
			ItemRepository itemRepository,
			Account account) {
		this.userRepository = userRepository;
		this.genreRepository = genreRepository;
		this.itemRepository = itemRepository;
		this.account = account;
	}

	public String fileNameGenerate() {
		LocalDateTime now = LocalDateTime.now();

		// フォーマットの定義 (大文字のMMは月、hhは12時間表記、HHは24時間表記)
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss");
		// 文字列へ変換
		String formattedDate = "recipt-" + (now.format(formatter));
		return formattedDate;
	}

	/*
	 * 日付による絞り込みにより、
	 * 当月または指定した範囲内の機関の収支を計算するメソッド
	 */
	public List<Item> incomeBalanceView(List<Item> itemDate, LocalDate initDate, LocalDate finalDate, Model model) {
		int totalBalance = 0;
		int totalIncome = 0;

		// 最大値を取得するための式
		List<Item> itemMax = itemRepository.findByUserIdOrderByPriceAsc(account.getId());

		// 最大出費
		Integer maxPrice = itemMax.get(0).getPrice();
		String maxItem = itemMax.get(0).getItemName();
		model.addAttribute("maxPrice", maxPrice);
		model.addAttribute("maxItem", maxItem);

		// 検索（期間の収支・残高を出すために使用）
		itemDate = itemRepository.findByUserIdAndAddDateBetween(account.getId(), initDate, finalDate);

		// 検索された期間に基づく収入の合計
		List<Item> genreIncome = itemRepository.findByUserIdAndAddDateBetween(
				account.getId(), initDate,
				finalDate);

		for (Item total : itemDate) {
			totalBalance += total.getPrice();
		}
		model.addAttribute("totalBalance", totalBalance);

		for (Item total : genreIncome) {
			if (total.getPrice() >= 0) {
				totalIncome += total.getPrice();
			}
		}
		model.addAttribute("totalIncome", totalIncome);

		// 利用済み合計額
		int expense = totalIncome - totalBalance;

		if (expense >= totalIncome) {
			model.addAttribute("alert", "上限を到達しました");
		} else if (expense >= totalIncome * 0.9) {
			model.addAttribute("alert", "80%を超過しました");
		} else if (expense >= totalIncome * 0.6) {
			model.addAttribute("alert", "60%を超過しました");
		} else {
			model.addAttribute("alert", "収入範囲内です。");
		}

		model.addAttribute("totalExpense", expense);
		return itemDate;
	}

	@GetMapping("/items")
	public String index(

			/*
			 * 5月22日　セッションによる名前表示とログアウトボタンを実装
			 * */
			@RequestParam(defaultValue = "") Integer genreId,
			Model model) {

		// 全カテゴリー一覧を取得
		List<Genre> genreList = genreRepository.findAll();
		model.addAttribute("genres", genreList);

		// まずはItemは空にする
		List<Item> itemList = null;
		// aタグでカテゴリが選択されていない場合
		if (genreId == null) {
			// 全件を表示
			itemList = itemRepository.findByUserId(account.getId());
		} else {
			// itemsテーブルをカテゴリーIDを指定して一覧を取得
			itemList = itemRepository.findByUserIdAndGenreId(account.getId(), genreId);
		}
		// itemsに返してHTML上のtableタグ内に返却
		model.addAttribute("items", itemList);

		/*
		 * 現在の月の合計収支を確認
		 * 
		 * */

		// 現在の月の収支（1日～31日をデフォルト）で取得
		LocalDate nowDate = LocalDate.now();

		// その月の1日～30・31日で取得
		LocalDate initDate = nowDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate finalDate = nowDate.with(TemporalAdjusters.lastDayOfMonth());

		// 当月期間の収支を表示
		incomeBalanceView(itemList, initDate, finalDate, model);

		return "items";
	}

	@GetMapping("/items/add")
	public String add(Model model) {

		// プルダウンで科目一覧を表示するためのコード
		List<Genre> genreList = genreRepository.findAll();
		model.addAttribute("genres", genreList);

		return "addItems";
	}

	/*
	 *  計上追加と編集時の重複する処理をメソッド化
	 *
	 */
	public void redirectView(LocalDate addDate,
			String itemName,
			Integer price,
			String comment,
			Model model) {
		// プルダウンで科目一覧をリクエスト後に表示するためのコード
		List<Genre> genreList = genreRepository.findAll();
		model.addAttribute("genres", genreList);

		model.addAttribute("setAddDate", addDate);
		model.addAttribute("setItemName", itemName);
		model.addAttribute("setPrice", price);
		model.addAttribute("setComment", comment);
	}

	@PostMapping("/items/add")
	public String store(
			@RequestParam(defaultValue = "") LocalDate addDate,
			@RequestParam(defaultValue = "") String itemName,
			@RequestParam(defaultValue = "") Integer genreId,
			@RequestParam(defaultValue = "") Integer price,
			@RequestParam(defaultValue = "") String comment,
			@RequestParam("imageFile") MultipartFile file,
			Model model) throws IOException {

		Genre genre = genreRepository.findById(genreId).get();
		User user = userRepository.findById(account.getId()).get();

		// 保存先
		String uploadDir = "src/main/resources/static/images/";

		// 元ファイル名
		String fileName = file.getOriginalFilename();

		/*
		 * アップロード時の重複防止のため
		 * 自動で変換する処理を行う。
		 * 拡張子を取得
		 * */
		String extension = fileName != null ? fileName.substring(fileName.lastIndexOf(".")) : "";

		// リネームしたい新しいファイル名を指定 (例: recipt-2026-05-21-173625.png
		String newFileName = fileNameGenerate() + extension;

		// 保存先生成
		Path filePath = Paths.get(uploadDir + newFileName);

		// ファイル保存
		Files.copy(file.getInputStream(), filePath);

		// DB保存用URL
		String reciptImage = "/images/" + newFileName;

		Item item = new Item(itemName, user, genre, price, addDate, comment, reciptImage);

		if (addDate == null || itemName == null || genreId == null || price == null || file == null) {
			model.addAttribute("inputErr", "入力項目に不足があります。");

			redirectView(addDate, itemName, price, comment, model);

			return "addItems";
		}

		// 次に計上の不備を確認
		if (price < 0) {
			if (genreId == 1 || genreId == 4) {
				model.addAttribute("priceErr", "科目に対して入力値が不正です");
				redirectView(addDate, itemName, price, comment, model);
				return "addItems";
			}
		} else { // 0以上
			if (!(genreId == 1 || genreId == 4)) {
				model.addAttribute("priceErr", "科目に対して入力値が不正です。");
				redirectView(addDate, itemName, price, comment, model);
				return "addItems";
			}
		}

		itemRepository.save(item);

		return "redirect:/items";
	}

	// 更新画面表示
	@GetMapping("/items/{id}/edit")
	public String edit(@PathVariable Integer id, Model model) {
		Item item = itemRepository.findById(id).get();
		model.addAttribute("item", item);

		// 更新画面でもプルダウンでカテゴリIDを選べるようにするため
		List<Genre> genreList = genreRepository.findAll();
		model.addAttribute("genres", genreList);
		return "editItem";
	}

	@PostMapping("/items/{id}/edit")
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
			@RequestParam(defaultValue = "科目に対して入力値が不正です") String priceErr,
			@RequestParam(defaultValue = "") LocalDate addDate,
			@RequestParam(defaultValue = "") String itemName,
			@RequestParam(defaultValue = "") Integer genreId,
			@RequestParam(defaultValue = "") Integer price,
			@RequestParam(defaultValue = "") String comment,
			@RequestParam("imageFile") MultipartFile file,
			Model model, RedirectAttributes redirectAttributes) throws IOException {

		// 変更された科目IDを使う
		Genre genre = genreRepository.findById(genreId).get();
		User user = userRepository.findById(account.getId()).get();

		Item item = itemRepository.findById(id).get();

		if (addDate == null || itemName == null || genreId == null || price == null) {
			redirectAttributes.addFlashAttribute("inputErr", inputErr);

			redirectView(addDate, itemName, price, comment, model);

			return "redirect:/items/{id}/edit";
		} else if (price < 0) {
			if (genreId == 1 || genreId == 4) {
				redirectAttributes.addFlashAttribute("priceErr", priceErr);
				redirectView(addDate, itemName, price, comment, model);
				return "redirect:/items/{id}/edit";
			}
		} else { // 0以上
			if (!(genreId == 1 || genreId == 4)) {
				redirectAttributes.addFlashAttribute("priceErr", priceErr);
				redirectView(addDate, itemName, price, comment, model);
				return "redirect:/items/{id}/edit";
			}
		}

		// 保存先
		String uploadDir = "src/main/resources/static/images/";

		// 元ファイル名
		String fileName = file.getOriginalFilename();

		/*
		 * アップロード時の重複防止のため
		 * 自動で変換する処理を行う。
		 * 拡張子を取得
		 * */
		String extension = fileName != null ? fileName.substring(fileName.lastIndexOf(".")) : "";

		// リネームしたい新しいファイル名を指定 (例: recipt-2026-05-21-173625.png
		String newFileName = fileNameGenerate() + extension;

		// 保存先生成
		Path filePath = Paths.get(uploadDir + newFileName);

		// ファイル保存
		Files.copy(file.getInputStream(), filePath);

		// DB保存用URL
		String reciptImage = "/images/" + newFileName;

		item.setAddDate(addDate);
		item.setItemName(itemName);
		item.setGenre(genre);
		item.setUser(user);
		item.setPrice(price);
		item.setComment(comment);
		item.setReciptImage(reciptImage);

		// 更新した情報を保存
		itemRepository.save(item);

		return "redirect:/items";
	}

	// 削除処理
	@PostMapping("/items/{id}/delete")
	public String delete(@PathVariable Integer id) {

		// 更新した情報を削除
		// リポジトリ名.deleteByデータベースフィールド名で指定
		itemRepository.deleteById(id);

		return "redirect:/items";
	}

	@PostMapping("/sort")
	public String sort(

			@RequestParam(defaultValue = "") LocalDate initDate,
			@RequestParam(defaultValue = "") LocalDate finalDate,
			Model model) {

		// 対象日付範囲の検索を開始
		List<Item> itemDate = null;

		if (initDate == null || finalDate == null) {
			model.addAttribute("searchErr", "開始と終了期間を入力しなければなりません");
			itemDate = itemRepository.findByUserId(account.getId());
			// エラー時は当月期間で
			// 現在の月の収支（1日～31日をデフォルト）で取得
			LocalDate nowDate = LocalDate.now();

			// その月の1日～30・31日で取得
			initDate = nowDate.with(TemporalAdjusters.firstDayOfMonth());
			finalDate = nowDate.with(TemporalAdjusters.lastDayOfMonth());

			incomeBalanceView(itemDate, initDate, finalDate, model);
			System.out.println("テスト：" + initDate + finalDate);
		} else {
			System.out.println("テスト：" + initDate + finalDate);
		}

		// 指定期間で収支を表示
		List<Item> itemList = incomeBalanceView(itemDate, initDate, finalDate, model);
		// returnされたものを格納して表示
		model.addAttribute("setInitDate", initDate);
		model.addAttribute("setFinalDate", finalDate);
		model.addAttribute("items", itemList);
		return "items";
	}

}
