 package Expense.Management.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import Expense.Management.DTO.ExpenseDTO;
import Expense.Management.DTO.ExpenseSplitDTO;
import Expense.Management.DTO.GroupReportDTO;
import Expense.Management.model.Expense;
import Expense.Management.model.ExpenseSplit;
import Expense.Management.model.Group;
import Expense.Management.repository.ExpenseRepository;
import Expense.Management.repository.GroupRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;

    public List<ExpenseDTO> getUserExpenses(Long userId, String period) {
        LocalDateTime startDate = calculateStartDate(period);
        List<Expense> expenses = expenseRepository.findByUserIdAndCreatedAtAfter(userId, startDate);
    
        return expenses.stream().map(expense -> new ExpenseDTO(
            expense.getId(),
            expense.getDescription(),
            expense.getAmount(),
            expense.getCreatedAt()
        )).collect(Collectors.toList());
    }
    
    
    public List<GroupReportDTO> getGroupReports(String period) {
        LocalDateTime startDate = calculateStartDate(period);
        List<Group> groups = groupRepository.findAll();
    
        return groups.stream().map(group -> {
            // Defensive copy to avoid ConcurrentModificationException
            List<ExpenseSplit> expenseSplitsCopy = new ArrayList<>(group.getExpenseSplits());
    
            List<ExpenseSplitDTO> splits = expenseSplitsCopy.stream()
                .filter(split -> split.getExpense() != null && split.getExpense().getCreatedAt().isAfter(startDate)) // Ensure expense is not null
                .map(split -> new ExpenseSplitDTO(
                    split.getExpense().getId(),
                    split.getExpense().getDescription(),
                    split.getExpense().getCreatedAt()
                ))
                .collect(Collectors.toList());
    
            return new GroupReportDTO(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getAdmin().getUsername(),
                splits
            );
        }).collect(Collectors.toList());
    }
    
    private LocalDateTime calculateStartDate(String period) {
        String normalizedPeriod = period.replace(" ", "").toLowerCase(); // Remove spaces
    
        switch (normalizedPeriod) {
            case "3months": return LocalDateTime.now().minusMonths(3);
            case "6months": return LocalDateTime.now().minusMonths(6);
            case "9months": return LocalDateTime.now().minusMonths(9);
            case "1year": return LocalDateTime.now().minusYears(1);
            default: throw new IllegalArgumentException("Invalid period. Allowed values: 3 months, 6 months, 9 months, 1 year.");
        }
    }

}
//     import java.io.ByteArrayInputStream;
//     import java.io.ByteArrayOutputStream;
//     import java.io.IOException;
//     import java.time.LocalDate;
//     import java.time.LocalDateTime;
//     import java.util.ArrayList;
//     import java.util.Collections;
//     import java.util.List;
//     import java.util.stream.Collectors;
    
//     import org.apache.poi.ss.usermodel.Row;
//     import org.apache.poi.ss.usermodel.Sheet;
//     import org.apache.poi.ss.usermodel.Workbook;
//     import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//     import org.springframework.stereotype.Service;
    
//     import Expense.Management.DTO.ExpenseDTO;
//     import Expense.Management.DTO.ExpenseSplitDTO;
//     import Expense.Management.DTO.GroupReportDTO;
//     import Expense.Management.model.Expense;
//     import Expense.Management.model.ExpenseSplit;
//     import Expense.Management.model.Group;
//     import Expense.Management.repository.ExpenseRepository;
//     import Expense.Management.repository.GroupRepository;
//     import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class ReportService {
//     private final ExpenseRepository expenseRepository;
//     private final GroupRepository groupRepository;

//     // Method to fetch user expenses based on time period
//     private List<Expense> getUserExpenses(Long userId, String period) {
//         LocalDateTime startDate = getStartDate(period);
//         return expenseRepository.findByUserIdAndCreatedAtAfter(userId, startDate);
//     }

//     // Method to fetch group reports based on time period
//     private List<GroupReportDTO> getGroupReports(String period) {
//         LocalDateTime startDate = getStartDate(period);
//         return groupRepository.findGroupReportsAfter(startDate);
//     }

//     // Converts the period into a LocalDateTime for filtering
//     private LocalDateTime getStartDate(String period) {
//         LocalDateTime now = LocalDateTime.now();
//         switch (period) {
//             case "3 months":
//                 return now.minusMonths(3);
//             case "6 months":
//                 return now.minusMonths(6);
//             case "9 months":
//                 return now.minusMonths(9);
//             case "1 year":
//                 return now.minusYears(1);
//             default:
//                 throw new IllegalArgumentException("Invalid period format. Choose from: 3 months, 6 months, 9 months, 1 year.");
//         }
//     }

//     // Generates an Excel file for user expenses
//     public ByteArrayInputStream generateUserExpensesExcel(Long userId, String period) {
//         List<Expense> expenses = getUserExpenses(userId, period);
//         try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//             Sheet sheet = workbook.createSheet("User Expenses");

//             Row headerRow = sheet.createRow(0);
//             headerRow.createCell(0).setCellValue("ID");
//             headerRow.createCell(1).setCellValue("Description");
//             headerRow.createCell(2).setCellValue("Amount");
//             headerRow.createCell(3).setCellValue("Category");
//             headerRow.createCell(4).setCellValue("Created At");

//             int rowIdx = 1;
//             for (Expense expense : expenses) {
//                 Row row = sheet.createRow(rowIdx++);
//                 row.createCell(0).setCellValue(expense.getId());
//                 row.createCell(1).setCellValue(expense.getDescription());
//                 row.createCell(2).setCellValue(expense.getAmount());
//                 row.createCell(3).setCellValue(expense.getCategory().toString());
//                 row.createCell(4).setCellValue(expense.getCreatedAt().toString());
//             }

//             workbook.write(out);
//             return new ByteArrayInputStream(out.toByteArray());
//         } catch (IOException e) {
//             throw new RuntimeException("Failed to generate Excel file", e);
//         }
//     }

//     // Generates an Excel file for group reports
//     public ByteArrayInputStream generateGroupReportsExcel(String period) {
//         List<GroupReportDTO> reports = getGroupReports(period);
//         try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//             Sheet sheet = workbook.createSheet("Group Reports");

//             Row headerRow = sheet.createRow(0);
//             headerRow.createCell(0).setCellValue("Group ID");
//             headerRow.createCell(1).setCellValue("Group Name");
//             headerRow.createCell(2).setCellValue("Description");
//             headerRow.createCell(3).setCellValue("Admin Name");
//             headerRow.createCell(4).setCellValue("Expense ID");
//             headerRow.createCell(5).setCellValue("Expense Description");
//             headerRow.createCell(6).setCellValue("Created At");

//             int rowIdx = 1;
//             for (GroupReportDTO report : reports) {
//                 for (ExpenseSplitDTO expenseSplit : report.getExpenseSplits()) {
//                     Row row = sheet.createRow(rowIdx++);
//                     row.createCell(0).setCellValue(report.getGroupId());
//                     row.createCell(1).setCellValue(report.getGroupName());
//                     row.createCell(2).setCellValue(report.getDescription());
//                     row.createCell(3).setCellValue(report.getAdminName());
//                     row.createCell(4).setCellValue(expenseSplit.getExpenseId());
//                     row.createCell(5).setCellValue(expenseSplit.getExpenseDescription());
//                     row.createCell(6).setCellValue(expenseSplit.getCreatedAt().toString());
//                 }
//             }

//             workbook.write(out);
//             return new ByteArrayInputStream(out.toByteArray());
//         } catch (IOException e) {
//             throw new RuntimeException("Failed to generate Excel file", e);
//         }
//     }
// }




    
    
    
    
