package com.expensetracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles the business logic for loading, saving, and manipulating expenses.
 */
public class ExpenseManager {
    private static final String FILE_PATH = "expenses.json";
    private final Gson gson;
    private List<Expense> expenses;

    public ExpenseManager() {
        // Configure Gson to be "pretty" and to use our custom LocalDateTime adapter
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        this.expenses = loadExpenses();
    }

    private List<Expense> loadExpenses() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<Expense>>() {}.getType();
            List<Expense> loadedExpenses = gson.fromJson(reader, listType);
            return loadedExpenses != null ? loadedExpenses : new ArrayList<>();
        } catch (IOException e) {
            // If the file doesn't exist, start with an empty list.
            return new ArrayList<>();
        }
    }

    private void saveExpenses() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(this.expenses, writer);
        } catch (IOException e) {
            System.err.println("Error: Could not save expenses to file. " + e.getMessage());
        }
    }

    public List<Expense> getAllExpenses() {
        return new ArrayList<>(this.expenses);
    }

    public Expense addExpense(String description, BigDecimal amount, String category) {
        String id = UUID.randomUUID().toString().substring(0, 8); // Short & unique ID
        Expense newExpense = new Expense(id, description, amount, category);
        this.expenses.add(newExpense);
        saveExpenses();
        return newExpense;
    }

    public boolean deleteExpense(String id) {
        boolean removed = this.expenses.removeIf(expense -> expense.getId().equals(id));
        if (removed) {
            saveExpenses();
        }
        return removed;
    }

    public Optional<Expense> updateExpense(String id, String newDescription, BigDecimal newAmount, String newCategory) {
        Optional<Expense> expenseToUpdate = this.expenses.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();

        expenseToUpdate.ifPresent(expense -> {
            if (newDescription != null) {
                expense.setDescription(newDescription);
            }
            if (newAmount != null) {
                expense.setAmount(newAmount);
            }
            if (newCategory != null) {
                expense.setCategory(newCategory);
            }
            saveExpenses();
        });

        return expenseToUpdate;
    }
}