package com.sellerlist.app.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_items")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"cart"})
@Data
@Builder
public class CartItem extends AbstractMappedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_item_id", unique = true, nullable = false, updatable = false)
	private Integer cartItemId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cart_id", nullable = false)
	private Cart cart;

	@Column(name = "product_id", nullable = false)
	private Integer productId;

	@Column(name = "product_title", nullable = false)
	private String productTitle;

	@Column(name = "sku")
	private String sku;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "category_id")
	private Integer categoryId;

	@Column(name = "category_title")
	private String categoryTitle;

	@Column(name = "price_unit", nullable = false, columnDefinition = "decimal")
	private Double priceUnit;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;
}
