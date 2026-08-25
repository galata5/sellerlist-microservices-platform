package com.sellerlist.app.exception.wrapper;

public final class ProductNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProductNotFoundException(final Integer productId) {
		super("No active product was found for id " + productId + ".");
	}
}
