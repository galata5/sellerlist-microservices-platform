package com.sellerlist.app.favourite;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellerlist.app.domain.Favourite;
import com.sellerlist.app.domain.FavouriteId;

public interface FavouriteRepository extends JpaRepository<Favourite, FavouriteId> {
}
