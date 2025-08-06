package com.expensetracker;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a single expense record.
 */
public class Expense {
    private String id;
    private String description;
    private BigDecimal amount;
    private LocalDateTime date;
    private String category;

    // No-argument constructor required by Gson for deserialization
    public Expense() {}

    public Expense(String id, String description, BigDecimal amount, String category) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getDate() { return date; }
    public String getCategory() { return category; }

    // Setters
    public void setDescription(String description) { this.description = description; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return "Expense{" +
               "id='" + id + '\'' +
               ", description='" + description + '\'' +
               ", amount=" + amount +
               ", date=" + date +
               ", category='" + category + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}