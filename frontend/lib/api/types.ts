export type CollectionResponse<T> = {
  collection: T[];
};

export type ServiceCollection<T> = {
  items: T[];
  sourceAvailable: boolean;
  message?: string;
  status?: number;
};

export type Category = {
  categoryId: number;
  categoryTitle: string;
  imageUrl?: string | null;
  parentCategory?: Category | null;
};

export type Product = {
  productId: number;
  productTitle: string;
  imageUrl?: string | null;
  sku?: string | null;
  priceUnit: number;
  quantity: number;
  description?: string | null;
  category?: Category | null;
};

export type CreateProductInput = {
  productTitle: string;
  imageUrl?: string | null;
  sku: string;
  priceUnit: number;
  quantity: number;
  description?: string | null;
  categoryId?: number;
  categoryTitle: string;
};

export type User = {
  userId?: number;
  firstName: string;
  lastName: string;
  imageUrl?: string | null;
  email: string;
  phone?: string | null;
};

export type RoleBasedAuthority = "ROLE_USER" | "ROLE_ADMIN";

export type Credential = {
  credentialId?: number;
  username: string;
  password?: string;
  roleBasedAuthority: RoleBasedAuthority;
  isEnabled: boolean;
  isAccountNonExpired: boolean;
  isAccountNonLocked: boolean;
  isCredentialsNonExpired: boolean;
  user?: User;
};

export type Cart = {
  cartId: number;
  userId?: number | null;
  items: CartItem[];
  user?: User | null;
  orderDtos?: Order[];
};

export type CartItem = {
  cartItemId?: number;
  productId: number;
  productTitle: string;
  sku?: string | null;
  imageUrl?: string | null;
  categoryId?: number | null;
  categoryTitle?: string | null;
  priceUnit: number;
  quantity: number;
};

export type CartMutationItem = {
  productId: number;
  quantity: number;
};

export type Order = {
  orderId: number;
  orderDate?: string | null;
  orderDesc?: string | null;
  orderFee: number;
  userId?: number | null;
  cart?: Cart | null;
};

export type PaymentStatus = "NOT_STARTED" | "IN_PROGRESS" | "COMPLETED";

export type Payment = {
  paymentId: number;
  isPayed: boolean;
  paymentStatus: PaymentStatus;
  userId?: number | null;
  order?: Order | null;
};

export type CheckoutInput = {
  fullName: string;
  phoneNumber: string;
  city: string;
  streetAddress: string;
  postalCode: string;
  email?: string;
  paymentMethod: "CASH_ON_DELIVERY";
  notes: string;
};

export type AuthRequest = {
  username: string;
  password: string;
};

export type AuthResponse = {
  userId?: number;
  username: string;
  authenticated: boolean;
};

export type RegisterRequest = {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  username: string;
  password: string;
};
