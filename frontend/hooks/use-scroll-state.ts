"use client";

import { useEffect, useState } from "react";

export function useScrollState(threshold = 24) {
  const [isPastThreshold, setIsPastThreshold] = useState(false);

  useEffect(() => {
    const onScroll = () => {
      setIsPastThreshold(window.scrollY > threshold);
    };

    onScroll();
    window.addEventListener("scroll", onScroll, { passive: true });

    return () => window.removeEventListener("scroll", onScroll);
  }, [threshold]);

  return isPastThreshold;
}
