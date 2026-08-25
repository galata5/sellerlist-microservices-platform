package com.sellerlist.app.domain;

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
@Table(name = "categories")
public class Category extends AbstractMappedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id", updatable = false)
	private Integer categoryId;

	@Column(name = "category_title", nullable = false, length = 120)
	private String title;

	@Column(name = "image_url", length = 500)
	private String imageUrl;

	@Column(name = "is_visible", nullable = false)
	private boolean visible;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_category_id")
	private Category parent;

	protected Category() {
		// Required by JPA.
	}

	private Category(final String title, final String imageUrl, final Category parent) {
		this.title = title;
		this.imageUrl = imageUrl;
		this.parent = parent;
		this.visible = true;
	}

	public static Category create(final String title, final String imageUrl, final Category parent) {
		return new Category(title, imageUrl, parent);
	}

	public void revise(final String title, final String imageUrl, final Category parent, final boolean visible) {
		this.title = title;
		this.imageUrl = imageUrl;
		this.parent = parent;
		this.visible = visible;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public String getTitle() {
		return title;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public boolean isVisible() {
		return visible;
	}

	public Category getParent() {
		return parent;
	}
}
