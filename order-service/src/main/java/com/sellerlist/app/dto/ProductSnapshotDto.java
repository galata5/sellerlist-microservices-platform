package com.sellerlist.app.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductSnapshotDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer productId;
	private String productTitle;
	private String sku;
	private String imageUrl;
	private Double priceUnit;
	private Integer quantity;

	@JsonProperty("category")
	private ProductCategorySnapshotDto categoryDto;
}
