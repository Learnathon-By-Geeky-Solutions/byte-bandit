import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"

/**
 * Concatenates and merges provided class names, resolving any conflicting Tailwind CSS classes.
 *
 * This utility function accepts a variable number of class values, which may include strings, arrays, or condition-based objects.
 * It first combines these values using the `clsx` library, then processes the result with `twMerge` to intelligently merge conflicting
 * Tailwind CSS classes.
 *
 * @example
 * cn('text-sm', 'font-bold', { 'bg-red-500': hasError })
 * // Returns a single optimized string of class names.
 *
 * @param inputs - One or more class values to be combined.
 * @returns A single string with merged Tailwind CSS class names.
 */
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}
