package com.sellerlist.app.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product extends AbstractMappedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id", updatable = false)
	private Integer productId;

	@Column(name = "product_title", nullable = false, length = 180)
	private String title;

	@Column(name = "image_url", length = 500)
	private String imageUrl;

	@Column(nullable = false, unique = true, length = 80)
	private String sku;

	@Column(name = "price_unit", nullable = false, precision = 12, scale = 2)
	private BigDecimal unitPrice;

	@Column(name = "quantity", nullable = false)
	private int stockOnHand;

	@Column(name = "description", length = 1200)
	private String description;

	@Column(name = "listing_status", nullable = false, length = 16)
	private String listingStatus;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	protected Product() {
		// Required by JPA.
	}

	private Product(
			final String title,
			final String imageUrl,
			final String sku,
			final BigDecimal unitPrice,
			final int stockOnHand,
			final String description,
			final Category category) {
		this.title = title;
		this.imageUrl = imageUrl;
		this.sku = sku;
		this.unitPrice = unitPrice;
		this.stockOnHand = stockOnHand;
		this.description = description;
		this.category = category;
		this.listingStatus = "ACTIVE";
	}

	public static Product publish(
			final String title,
			final String imageUrl,
			final String sku,
			final BigDecimal unitPrice,
			final int stockOnHand,
			final String description,
			final Category category) {
		return new Product(title, imageUrl, sku, unitPrice, stockOnHand, description, category);
	}

	public void revise(
			final String title,
			final String imageUrl,
			final String sku,
			final BigDecimal unitPrice,
			final int stockOnHand,
			final String description,
			final Category category) {
		this.title = title;
		this.imageUrl = imageUrl;
		this.sku = sku;
		this.unitPrice = unitPrice;
		this.stockOnHand = stockOnHand;
		this.description = description;
		this.category = category;
	}

	public void withdraw() {
		this.listingStatus = "WITHDRAWN";
	}

	public Integer getProductId() {
		return productId;
	}

	public String getTitle() {
		return title;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getSku() {
		return sku;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public int getStockOnHand() {
		return stockOnHand;
	}

	public String getDescription() {
		return description;
	}

	public boolean isActive() {
		return "ACTIVE".equals(listingStatus);
	}

	public Category getCategory() {
		return category;
	}
}
