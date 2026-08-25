package com.sellerlist.app.favourite;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sellerlist.app.constant.AppConstant;
import com.sellerlist.app.domain.Favourite;
import com.sellerlist.app.domain.FavouriteId;

@RestController
@RequestMapping("/api/favourites")
public class FavouriteController {

	private static final DateTimeFormatter FORMATTER =
			DateTimeFormatter.ofPattern(AppConstant.LOCAL_DATE_TIME_FORMAT);

	private final FavouriteService service;

	public FavouriteController(final FavouriteService service) {
		this.service = service;
	}

	@GetMapping
	public List<Favourite> getAll() {
		return service.getAll();
	}

	@GetMapping("/{userId}/{productId}/{likeDate}")
	public Favourite getById(
			@PathVariable final Integer userId,
			@PathVariable final Integer productId,
			@PathVariable final String likeDate) {
		return service.getById(new FavouriteId(userId, productId, LocalDateTime.parse(likeDate, FORMATTER)));
	}

	@PostMapping
	public Favourite create(@RequestBody @Valid final Favourite favourite) {
		return service.save(favourite);
	}

	@PutMapping
	public Favourite update(@RequestBody @Valid final Favourite favourite) {
		return service.save(favourite);
	}

	@DeleteMapping("/{userId}/{productId}/{likeDate}")
	public ResponseEntity<Void> delete(
			@PathVariable final Integer userId,
			@PathVariable final Integer productId,
			@PathVariable final String likeDate) {
		service.deleteById(new FavouriteId(userId, productId, LocalDateTime.parse(likeDate, FORMATTER)));
		return ResponseEntity.noContent().build();
	}
}
