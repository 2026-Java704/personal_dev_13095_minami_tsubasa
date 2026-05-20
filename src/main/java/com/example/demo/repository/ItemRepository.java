package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Integer> {

	List<Item> findAllByOrderByAddDate();

	List<Item> findByGenreId(Integer genreId);

	List<Item> findByAddDateBetween(LocalDate initDate, LocalDate finalDate);

	// Trueの部分の合計（収入全体）を算出
	List<Item> findByUserIdAndGenre_IsIncomeTrueAndAddDateBetween(Integer id, LocalDate initDate, LocalDate finalDate);

	List<Item> findByUserIdAndAddDateBetween(Integer id, LocalDate initDate, LocalDate finalDate);

	List<Item> findByUserId(Integer id);

	List<Item> findByUserIdAndGenreId(Integer id, Integer genreId);

	// 期間の最大値を求める

}
