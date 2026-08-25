package com.sellerlist.app.favourite;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sellerlist.app.domain.Favourite;
import com.sellerlist.app.domain.FavouriteId;

@Service
@Transactional
public class FavouriteService {

	private final FavouriteRepository repository;

	public FavouriteService(final FavouriteRepository repository) {
		this.repository = repository;
	}

	public List<Favourite> getAll() {
		return repository.findAll();
	}

	public Favourite getById(final FavouriteId id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Favourite not found"));
	}

	public Favourite save(final Favourite favourite) {
		return repository.save(favourite);
	}

	public void deleteById(final FavouriteId id) {
		repository.deleteById(id);
	}
}
