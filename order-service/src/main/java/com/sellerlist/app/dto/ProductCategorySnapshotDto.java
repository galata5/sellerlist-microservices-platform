package com.sellerlist.app.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductCategorySnapshotDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer categoryId;
	private String categoryTitle;
}
