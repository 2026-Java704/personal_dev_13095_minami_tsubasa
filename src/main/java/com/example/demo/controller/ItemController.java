package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
 * 1. AccountControllerでリダイレクトされたitems.html
 * に対しての処理をお粉ます。
 * 
 * カートに追加ボタン…の処理は、ここではなくCartController.javaで処理されます。
 * @PostMapping("/cart/add")がこの時呼び出されます。
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
	public String index(Model model) {

		// まずはItemから全権表示
		List<Item> itemList = itemRepository.findAllByOrderByAddDate();

		// itemsに返してHTML上のtableタグ内に返却
		model.addAttribute("items", itemList);

		return "items";
	}

	@GetMapping("/items/add")
	public String add(Model model) {

		// プルダウンで科目一覧を表示するためのコード
		List<Genre> genreList = genreRepository.findAll();
		model.addAttribute("genres", genreList);

		return "addItems";
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

			// プルダウンで科目一覧を表示するためのコード
			List<Genre> genreList = genreRepository.findAll();
			model.addAttribute("genres", genreList);
			return "addItems";
		}
		itemRepository.save(item);

		return "redirect:/items";
	}

}
