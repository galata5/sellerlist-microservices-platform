"use client";

import { useState } from "react";

type ProductArtworkProps = {
  imageUrl?: string | null;
  productTitle: string;
  categoryTitle?: string | null;
  frameClassName: string;
  imageClassName: string;
  fallbackClassName: string;
};

export function ProductArtwork({
  imageUrl,
  productTitle,
  categoryTitle,
  frameClassName,
  imageClassName,
  fallbackClassName
}: ProductArtworkProps) {
  const [imageFailed, setImageFailed] = useState(false);
  const showImage = Boolean(imageUrl) && !imageFailed;

  if (showImage) {
    return (
      <div className={frameClassName}>
        <img
          src={imageUrl ?? undefined}
          alt={productTitle}
          className={imageClassName}
          onError={() => setImageFailed(true)}
        />
      </div>
    );
  }

  return (
    <div className={fallbackClassName}>
      <div>
        <p className="mb-3 text-xs font-medium uppercase tracking-[0.18em] text-[#6B7280]">
          {categoryTitle ?? "Product"}
        </p>
        <p className="text-2xl font-semibold leading-tight text-text">{productTitle}</p>
      </div>
    </div>
  );
}
