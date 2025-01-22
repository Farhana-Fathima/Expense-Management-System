package Expense.Management.controller;

import Expense.Management.DTO.ExpenseRequest;
import Expense.Management.DTO.ExpenseResponse;
import Expense.Management.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
                                                      //    user's personal
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    // Add a new expense
    @PostMapping
    public ExpenseResponse addExpense(@RequestBody ExpenseRequest expenseRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return expenseService.addExpense(username, expenseRequest);
    }

    // Get all expenses for a user
    @GetMapping
    public List<ExpenseResponse> getAllExpenses() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return expenseService.getExpenses(username);
    }

    // Update an existing expense
    @PutMapping("/{expenseId}")
    public ExpenseResponse updateExpense(@PathVariable Long expenseId, @RequestBody ExpenseRequest expenseRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return expenseService.updateExpense(username, expenseId, expenseRequest);
    }

    // Delete an expense
    @DeleteMapping("/{expenseId}")
    public void deleteExpense(@PathVariable Long expenseId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        expenseService.deleteExpense(username, expenseId);
    }
}


