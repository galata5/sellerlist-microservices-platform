import { request, requestOptional, unwrapCollection } from "@/lib/api/client";
import type {
  Category,
  CollectionResponse,
  CreateProductInput,
  Product,
  ServiceCollection
} from "@/lib/api/types";

export async function getProducts() {
  const response = await request<CollectionResponse<Product>>("/products");

  return unwrapCollection(response);
}

export async function getProduct(productId: string) {
  return request<Product>(`/products/${productId}`);
}

export async function getProductCatalog(): Promise<ServiceCollection<Product>> {
  const response = await requestOptional<CollectionResponse<Product>>("/products");

  return {
    items: response.data ? unwrapCollection(response.data) : [],
    sourceAvailable: Boolean(response.data),
    message: response.message,
    status: response.status
  };
}

export async function getCategories(): Promise<ServiceCollection<Category>> {
  const response = await requestOptional<CollectionResponse<Category>>("/categories");

  return {
    items: response.data ? unwrapCollection(response.data) : [],
    sourceAvailable: Boolean(response.data),
    message: response.message,
    status: response.status
  };
}

export async function createCategory(categoryTitle: string) {
  return request<Category>("/categories", {
    method: "POST",
    body: {
      categoryTitle: categoryTitle.trim()
    }
  });
}

export async function createProduct(input: CreateProductInput) {
  const category =
    input.categoryId !== undefined
      ? { categoryId: input.categoryId, categoryTitle: input.categoryTitle }
      : await createCategory(input.categoryTitle);

  return request<Product>("/products", {
    method: "POST",
    body: {
      productTitle: input.productTitle.trim(),
      imageUrl: input.imageUrl?.trim() || null,
      sku: input.sku.trim(),
      priceUnit: input.priceUnit,
      quantity: input.quantity,
      description: input.description?.trim() || null,
      category: { categoryId: category.categoryId }
    }
  });
}
