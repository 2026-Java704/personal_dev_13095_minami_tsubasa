package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.Item;
import com.example.demo.model.Account;
import com.example.demo.repository.GenreRepository;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.UserRepository;

@Controller
public class dateController {

	/*
	 *  repositoryフォルダにあるインターフェースを定義し、
	 *  情報を更新したりする際に必要になります。
	 */
	private final UserRepository userRepository;
	private final GenreRepository genreRepository;
	private final ItemRepository itemRepository;
	private final Account account;

	public dateController(
			UserRepository userRepository,
			GenreRepository genreRepository,
			ItemRepository itemRepository,
			Account account) {
		this.userRepository = userRepository;
		this.genreRepository = genreRepository;
		this.itemRepository = itemRepository;
		this.account = account;
	}

	@GetMapping("/items/detail")
	public String calender(Model model) {

		List<Item> itemList = itemRepository.findByUserId(account.getId());
		model.addAttribute("items", itemList);

		return "dateView";
	}

	@GetMapping("/items/getInfo")
	public String getInfo(Model model) {

		List<Item> itemList = itemRepository.findByUserId(account.getId());
		model.addAttribute("items", itemList);

		return "dateView";
	}
}
