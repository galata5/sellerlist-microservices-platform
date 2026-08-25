package com.sellerlist.app.service;

import com.sellerlist.app.dto.CartDto;
import com.sellerlist.app.dto.UpdateCartRequest;

public interface CartService {
	
	CartDto getCurrentCart(final Integer authenticatedUserId);
	CartDto replaceCurrentCart(final Integer authenticatedUserId, final UpdateCartRequest updateCartRequest);
	void clearCurrentCart(final Integer authenticatedUserId);
	
}
