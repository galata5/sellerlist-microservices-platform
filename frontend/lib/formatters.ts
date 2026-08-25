export function formatCurrency(value: number) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
    maximumFractionDigits: 2
  }).format(value);
}

export function formatDate(value?: string | Date | null) {
  if (!value) {
    return "Pending";
  }

  const date = value instanceof Date ? value : new Date(value);

  if (Number.isNaN(date.getTime())) {
    return String(value);
  }

  return new Intl.DateTimeFormat("en-US", {
    month: "short",
    day: "numeric",
    year: "numeric"
  }).format(date);
}

export function formatDateTimeForBackend(value: Date) {
  const pad = (input: number, size = 2) => String(input).padStart(size, "0");

  return [
    pad(value.getDate()),
    pad(value.getMonth() + 1),
    value.getFullYear()
  ].join("-") +
    "__" +
    [
      pad(value.getHours()),
      pad(value.getMinutes()),
      pad(value.getSeconds()),
      pad(value.getMilliseconds(), 6)
    ].join(":");
}

export function formatCompactNumber(value: number) {
  return new Intl.NumberFormat("en-US", {
    notation: "compact",
    maximumFractionDigits: 1
  }).format(value);
}
