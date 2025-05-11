package com.fges.model;

/**
 * A record representing a grocery item for web display.
 * Immutable data structure with name, quantity, and category.
 */
public record WebGroceryItem(String name, int quantity, String category) {
} 