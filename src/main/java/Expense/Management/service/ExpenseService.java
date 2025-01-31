package Expense.Management.service;

import Expense.Management.DTO.ExpenseRequest;
import Expense.Management.DTO.ExpenseResponse;
import Expense.Management.model.Expense;
import Expense.Management.model.ExpenseCategory;
import Expense.Management.model.User;
import Expense.Management.repository.ExpenseRepository;
import Expense.Management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    // Add new expense
    @Transactional
    public ExpenseResponse addExpense(String username, ExpenseRequest expenseRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        // Convert category string to enum
        ExpenseCategory category;
        try {
            category = ExpenseCategory.valueOf(expenseRequest.getCategory().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid category. Allowed categories: " + ExpenseCategory.getAllowedCategories());
        }
    
        Expense expense = Expense.builder()
                .user(user)
                .amount(expenseRequest.getAmount())
                .description(expenseRequest.getDescription())
                .expenseDate(expenseRequest.getExpenseDate())
                .category(category) // Pass the converted enum
                .receipt(expenseRequest.getReceipt())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    
        Expense savedExpense = expenseRepository.save(expense);
    
        return mapToExpenseResponse(savedExpense);
    }
    

    // Get all expenses for a user
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpenses(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Expense> expenses = expenseRepository.findByUser(user);
        return expenses.stream()
                .map(this::mapToExpenseResponse)
                .collect(Collectors.toList());
    }

    // Update an existing expense
    @Transactional
public ExpenseResponse updateExpense(String username, Long expenseId, ExpenseRequest expenseRequest) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Expense expense = expenseRepository.findByUserAndId(user, expenseId)
            .orElseThrow(() -> new RuntimeException("Expense not found"));

    // Convert category string to enum
    ExpenseCategory category;
    try {
        category = ExpenseCategory.valueOf(expenseRequest.getCategory().toUpperCase());
    } catch (IllegalArgumentException e) {
        throw new RuntimeException("Invalid category. Allowed categories: " + ExpenseCategory.getAllowedCategories());
    }

    // Update expense details
    expense.setAmount(expenseRequest.getAmount());
    expense.setDescription(expenseRequest.getDescription());
    expense.setExpenseDate(expenseRequest.getExpenseDate());
    expense.setCategory(category); // Pass the converted enum
    expense.setReceipt(expenseRequest.getReceipt());
    expense.setUpdatedAt(LocalDateTime.now());

    Expense updatedExpense = expenseRepository.save(expense);

    return mapToExpenseResponse(updatedExpense);
}


    // Delete an expense
    @Transactional
    public void deleteExpense(String username, Long expenseId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = expenseRepository.findByUserAndId(user, expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        expenseRepository.delete(expense);
    }

   // Helper method to map Expense to ExpenseResponse
   private ExpenseResponse mapToExpenseResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .category(expense.getCategory().name()) // Use the name of the enum
                .expenseDate(expense.getExpenseDate())
                .receipt(expense.getReceipt())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}
