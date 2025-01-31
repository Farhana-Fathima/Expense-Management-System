package Expense.Management.controller;

import Expense.Management.DTO.ExpenseSplitRequest;
import Expense.Management.DTO.ExpenseSplitResponse;
import Expense.Management.DTO.ParticipantPaymentRequest;
import Expense.Management.service.ExpenseSplitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expense-splits")
@RequiredArgsConstructor
public class ExpenseSplitController {

    private final ExpenseSplitService expenseSplitService;

    /**
     * Create an expense split within a group. 
     * Only the group admin or a member of the group can create an expense.
     */
    @PostMapping
    public ResponseEntity<ExpenseSplitResponse> createExpenseSplit(
            @RequestBody ExpenseSplitRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ExpenseSplitResponse expenseSplit = expenseSplitService.createExpenseSplit(request, userDetails.getUsername());
        return ResponseEntity.ok(expenseSplit);
    }

    /**
     * Retrieve all expense splits for a specific group.
     * Only group members can view the expenses.
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ExpenseSplitResponse>> getExpensesByGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ExpenseSplitResponse> expenses = expenseSplitService.getExpensesByGroup(groupId, userDetails.getUsername());
        return ResponseEntity.ok(expenses);
    }

    /**
     * Get details of a specific expense split.
     * Only group members can access it.
     */
    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseSplitResponse> getExpenseById(
            @PathVariable Long expenseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        ExpenseSplitResponse expense = expenseSplitService.getExpenseById(expenseId, userDetails.getUsername());
        return ResponseEntity.ok(expense);
    }

    /**
     * Update an expense split.
     * Only the creator or group admin can update.
     */
    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseSplitResponse> updateExpenseSplit(
            @PathVariable Long expenseId,
            @RequestBody ExpenseSplitRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ExpenseSplitResponse updatedExpense = expenseSplitService.updateExpenseSplit(expenseId, request, userDetails.getUsername());
        return ResponseEntity.ok(updatedExpense);
    }

    /**
     * Delete an expense split.
     * Only the creator or group admin can delete.
     */
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<String> deleteExpenseSplit(
            @PathVariable Long expenseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        expenseSplitService.deleteExpenseSplit(expenseId, userDetails.getUsername());
        return ResponseEntity.ok("Expense split deleted successfully.");
    }

    /**
     * Pay a share of the expense.
     * Only the participant of that expense can pay their share.
     */
    @PostMapping("/{expenseId}/pay")
    public ResponseEntity<String> payExpenseShare(
            @PathVariable Long expenseId,
            @RequestBody ParticipantPaymentRequest paymentRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        expenseSplitService.payExpenseShare(expenseId, paymentRequest, userDetails.getUsername());
        return ResponseEntity.ok("Payment recorded successfully.");
    }
}
























// package Expense.Management.controller;

// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import Expense.Management.DTO.ExpenseSplitRequest;
// import Expense.Management.DTO.ExpenseSplitResponse;
// import Expense.Management.model.User;
// import Expense.Management.service.ExpenseSplitService;
// import jakarta.transaction.Transactional;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/api/expenses/splits")
// @RequiredArgsConstructor
// public class ExpenseSplitController {
//     private final ExpenseSplitService expenseSplitService;

//     // Create expense split
//     @PostMapping("/{groupId}")
//     @Transactional
//     public ResponseEntity<ExpenseSplitResponse> createExpenseSplit(
//             @PathVariable Long groupId,
//             @Valid @RequestBody ExpenseSplitRequest request,
//             @AuthenticationPrincipal User currentUser) {
//         return ResponseEntity.ok(expenseSplitService.createExpenseSplit(groupId, request, currentUser));
//     }

//     // Update expense split
//     @PutMapping("/{groupId}/{expenseSplitId}")
//     public ResponseEntity<ExpenseSplitResponse> updateExpenseSplit(
//             @PathVariable Long groupId,
//             @PathVariable Long expenseSplitId,
//             @Valid @RequestBody ExpenseSplitRequest request,
//             @AuthenticationPrincipal User currentUser) {
//         return ResponseEntity.ok(expenseSplitService.updateExpenseSplit(groupId, expenseSplitId, request, currentUser));
//     }

//     // Delete expense split
//     @DeleteMapping("/{groupId}/{expenseSplitId}")
//     public ResponseEntity<Void> deleteExpenseSplit(
//             @PathVariable Long groupId,
//             @PathVariable Long expenseSplitId,
//             @AuthenticationPrincipal User currentUser) {
//         expenseSplitService.deleteExpenseSplit(groupId, expenseSplitId, currentUser);
//         return ResponseEntity.noContent().build();
//     }

//     // Mark share as paid
//     @PostMapping("/{groupId}/{expenseSplitId}/pay")
//     public ResponseEntity<ExpenseSplitResponse> markShareAsPaid(
//             @PathVariable Long groupId,
//             @PathVariable Long expenseSplitId,
//             @AuthenticationPrincipal User currentUser) {
//         return ResponseEntity.ok(expenseSplitService.markShareAsPaid(groupId, expenseSplitId, currentUser));
//     }
// }

