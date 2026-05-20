package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Integer> {

	List<Item> findAllByOrderByAddDate();

	List<Item> findByGenreId(Integer genreId);

	List<Item> findByAddDateBetween(LocalDate initDate, LocalDate finalDate);
}
