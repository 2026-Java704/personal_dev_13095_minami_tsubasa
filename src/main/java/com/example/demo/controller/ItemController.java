package com.example.demo.controller;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Genre;
import com.example.demo.entity.Item;
import com.example.demo.entity.User;
import com.example.demo.model.Account;
import com.example.demo.repository.GenreRepository;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.UserRepository;

/*
 * 5/20にやること！
 * input入力欄から数値入力1~12 年
 * addDateを使ってfindByで月の範囲（1~31日）を検索
 * 
 * 例： List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
 * 変数はそれぞれinputタグのnameから取る。
 * 
 * その範囲のpriceを出す。正と負の値での結果を出す。
 * 
 * */

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

	// 商品一覧表示
	/*
	 * AccountControllerからリダイレクトした際に
	 * items.htmlをここで起動します。
	 * 
	 * 起動後は、データベースのデータを保管するEntity内で項目定義した
	 * Itemクラスと、ソートなどで使用する同様なCategoryを
	 * Listでデータの存在分すべてを格納し、
	 * Splingの仕様でそこから簡単に使用可能なrepositoryインターフェースで
	 * ここではデータベースの全件表示を行います。
	 * 
	 */

	@GetMapping("/items")
	public String index(
			@RequestParam(defaultValue = "") Integer genreId,
			Model model) {

		// 全カテゴリー一覧を取得
		List<Genre> genreList = genreRepository.findAll();
		model.addAttribute("genres", genreList);

		// 商品一覧情報の取得

		/*
		 * 補足：items.htmlのaタグ操作によってここの処理は決まります。
		 * th:each="category:${categories}"のthymeleaf機能で
		 * カテゴリデータベース上のカテゴリを全権表示
		 * 
		 * ここで、各カテゴリをth:hrefでクリックして下記の処理につながります。
		 * 
		 * まず、初期ではItemを空にして置き、
		 * 下の条件分岐で初めて表示を柔軟にできるようにしています。
		 * 
		 * */

		// まずはItemは空にする
		List<Item> itemList = null;
		// aタグでカテゴリが選択されていない場合
		if (genreId == null) {
			// 全件を表示
			itemList = itemRepository.findAll();
		} else {
			// itemsテーブルをカテゴリーIDを指定して一覧を取得
			itemList = itemRepository.findByGenreId(genreId);
		}
		// itemsに返してHTML上のtableタグ内に返却
		model.addAttribute("items", itemList);

		/*
		 * 現在の月の合計収支を確認
		 * 
		 * */
		int totalBalance = 0;

		// 現在の月の収支（1日～31日をデフォルト）で取得
		LocalDate nowDate = LocalDate.now();

		// その月の1日～30・31日で取得
		LocalDate firstDay = nowDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate lastDay = nowDate.with(TemporalAdjusters.lastDayOfMonth());

		// 上記とは別で日付
		List<Item> itemDate = itemRepository.findByAddDateBetween(firstDay, lastDay);

		for (Item total : itemDate) {
			totalBalance += total.getPrice();
		}
		model.addAttribute("totalBalance", totalBalance);

		// 確認したい収支のマイナス・90%を超えた場合のアラート

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
			Model model) {

		Genre genre = genreRepository.findById(genreId).get();
		User user = userRepository.findById(account.getId()).get();

		Item item = new Item(itemName, user, genre, price, addDate, comment);

		if (addDate == null || itemName == null || genreId == null || price == null) {
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

			@RequestParam(defaultValue = "") LocalDate addDate,
			@RequestParam(defaultValue = "") String itemName,
			@RequestParam(defaultValue = "") Integer genreId,
			@RequestParam(defaultValue = "") Integer price,
			@RequestParam(defaultValue = "") String comment,
			Model model) {

		// 変更された科目IDを使う
		Genre genre = genreRepository.findById(genreId).get();
		User user = userRepository.findById(account.getId()).get();

		Item item = itemRepository.findById(id).get();

		item.setAddDate(addDate);
		item.setItemName(itemName);
		item.setGenre(genre);
		item.setUser(user);
		item.setPrice(price);
		item.setComment(comment);

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
		int totalBalance = 0;

		if (initDate == null || finalDate == null) {
			model.addAttribute("searchErr", "開始と終了期間を入力しなければなりません");
			itemDate = itemRepository.findAll();
		} else {
			System.out.println("テスト：" + initDate + finalDate);
			itemDate = itemRepository.findByAddDateBetween(initDate, finalDate);
		}

		for (Item total : itemDate) {
			totalBalance += total.getPrice();
		}

		model.addAttribute("items", itemDate);
		model.addAttribute("totalBalance", totalBalance);

		// 更新した情報を保存

		return "items";
	}

}
