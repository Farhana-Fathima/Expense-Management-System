package Expense.Management.service;

import Expense.Management.DTO.ExpenseSplitRequest;
import Expense.Management.DTO.ExpenseSplitResponse;
import Expense.Management.DTO.ParticipantPaymentRequest;

import java.util.List;

public interface ExpenseSplitService {
    ExpenseSplitResponse createExpenseSplit(ExpenseSplitRequest request, String username);
    
    List<ExpenseSplitResponse> getExpensesByGroup(Long groupId, String username);
    
    ExpenseSplitResponse getExpenseById(Long expenseId, String username);
    
    ExpenseSplitResponse updateExpenseSplit(Long expenseId, ExpenseSplitRequest request, String username);
    
    void deleteExpenseSplit(Long expenseId, String username);
    
    void payExpenseShare(Long expenseId, ParticipantPaymentRequest request, String username);
}
