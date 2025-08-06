package com.expensetracker;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Command(name = "expense",
        mixinStandardHelpOptions = true,
        version = "Expense Tracker 1.0",
        description = "A simple command-line expense tracker.",
        subcommands = {
                ExpenseApp.AddCommand.class,
                ExpenseApp.ViewCommand.class,
                ExpenseApp.UpdateCommand.class,
                ExpenseApp.DeleteCommand.class,
                ExpenseApp.SummaryCommand.class
        })
public class ExpenseApp implements Runnable {

    // This method is called if the user runs the app with no command
    @Override
    public void run() {
        System.out.println("No command specified. Use '--help' to see available commands.");
    }

    public static void main(String[] args) {
        // The magic of picocli: execute the command based on user arguments
        int exitCode = new CommandLine(new ExpenseApp()).execute(args);
        System.exit(exitCode);
    }

    // Command to ADD an expense
    @Command(name = "add", description = "Add a new expense.")
    static class AddCommand implements Runnable {
        @Option(names = {"-d", "--description"}, required = true, description = "Description of the expense.")
        private String description;

        @Option(names = {"-a", "--amount"}, required = true, description = "Amount of the expense.")
        private BigDecimal amount;

        @Option(names = {"-c", "--category"}, description = "Category of the expense.", defaultValue = "Uncategorized")
        private String category;

        @Override
        public void run() {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.err.println("Error: Amount must be positive.");
                return;
            }
            ExpenseManager manager = new ExpenseManager();
            Expense newExpense = manager.addExpense(description, amount, category);
            System.out.println("✅ Expense added successfully! ID: " + newExpense.getId());
        }
    }

    // Command to VIEW all expenses
    @Command(name = "view", description = "View all expenses.")
    static class ViewCommand implements Runnable {
        @Override
        public void run() {
            ExpenseManager manager = new ExpenseManager();
            List<Expense> expenses = manager.getAllExpenses();
            if (expenses.isEmpty()) {
                System.out.println("No expenses found. Try adding one with the 'add' command.");
                return;
            }

            System.out.println("--- All Expenses ---");
            System.out.printf("%-10s %-12s %-30s %-15s %s%n", "ID", "Amount", "Description", "Category", "Date");
            System.out.println("-".repeat(90));
            for (Expense e : expenses) {
                System.out.printf("%-10s $%-11.2f %-30s %-15s %s%n",
                        e.getId(), e.getAmount(), e.getDescription(), e.getCategory(), e.getDate().toLocalDate());
            }
        }
    }

    // Command to UPDATE an expense
    @Command(name = "update", description = "Update an existing expense.")
    static class UpdateCommand implements Runnable {
        @Parameters(index = "0", description = "The ID of the expense to update.")
        private String id;

        @Option(names = {"-d", "--description"}, description = "New description for the expense.")
        private String description;

        @Option(names = {"-a", "--amount"}, description = "New amount for the expense.")
        private BigDecimal amount;

        @Option(names = {"-c", "--category"}, description = "New category for the expense.")
        private String category;

        @Override
        public void run() {
            if (description == null && amount == null && category == null) {
                System.err.println("Error: You must specify at least one field to update (--description, --amount, or --category).");
                return;
            }
            if (amount != null && amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.err.println("Error: Amount must be positive.");
                return;
            }
            
            ExpenseManager manager = new ExpenseManager();
            Optional<Expense> updated = manager.updateExpense(id, description, amount, category);

            if (updated.isPresent()) {
                System.out.println("✅ Expense '" + id + "' updated successfully.");
            } else {
                System.err.println("Error: Expense with ID '" + id + "' not found.");
            }
        }
    }

    // Command to DELETE an expense
    @Command(name = "delete", description = "Delete an expense by its ID.")
    static class DeleteCommand implements Runnable {
        @Parameters(index = "0", description = "The ID of the expense to delete.")
        private String id;

        @Override
        public void run() {
            ExpenseManager manager = new ExpenseManager();
            boolean deleted = manager.deleteExpense(id);
            if (deleted) {
                System.out.println("✅ Expense '" + id + "' deleted successfully.");
            } else {
                System.err.println("Error: Expense with ID '" + id + "' not found.");
            }
        }
    }

    // Command to get a SUMMARY of expenses
    @Command(name = "summary", description = "Show a summary of expenses.")
    static class SummaryCommand implements Runnable {
        @Option(names = {"-m", "--month"}, description = "The month to summarize (1-12). If not specified, summarizes all expenses.")
        private Integer month;

        @Override
        public void run() {
            ExpenseManager manager = new ExpenseManager();
            List<Expense> expenses = manager.getAllExpenses();
            int currentYear = Year.now().getValue();

            if (month != null) {
                if (month < 1 || month > 12) {
                    System.err.println("Error: Invalid month. Please provide a number between 1 and 12.");
                    return;
                }
                // Filter expenses for the specified month of the current year
                expenses.removeIf(e -> e.getDate().getMonthValue() != month || e.getDate().getYear() != currentYear);
                System.out.println("--- Summary for " + Month.of(month).name() + " " + currentYear + " ---");
            } else {
                System.out.println("--- Overall Summary ---");
            }

            if (expenses.isEmpty()) {
                System.out.println("No expenses found for this period.");
                return;
            }

            BigDecimal total = expenses.stream()
                                       .map(Expense::getAmount)
                                       .reduce(BigDecimal.ZERO, BigDecimal::add);

            System.out.println("Total Number of Expenses: " + expenses.size());
            System.out.printf("Total Amount: $%.2f%n", total);
        }
    }
}