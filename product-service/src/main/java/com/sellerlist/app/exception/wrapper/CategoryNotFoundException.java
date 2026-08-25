package com.sellerlist.app.exception.wrapper;

public final class CategoryNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CategoryNotFoundException(final Integer categoryId) {
		super("No category was found for id " + categoryId + ".");
	}
}
