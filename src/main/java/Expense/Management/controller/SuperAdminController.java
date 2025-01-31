package Expense.Management.controller;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import Expense.Management.DTO.ExpenseDTO;
import Expense.Management.DTO.GroupReportDTO;
import Expense.Management.service.ReportService;
import Expense.Management.service.SuperAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/superadmin")
@RequiredArgsConstructor
public class SuperAdminController {
    private final SuperAdminService superAdminService;
    private final ReportService reportService;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(superAdminService.getAllUsers());
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/groups")
    public ResponseEntity<?> getAllGroups() {
        return ResponseEntity.ok(superAdminService.getAllGroups());
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/groups/{groupId}/assign-admin/{userId}")
    public ResponseEntity<?> assignAdminToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        superAdminService.assignAdminToGroup(groupId, userId);
        return ResponseEntity.ok("Admin role assigned to user for the group.");
    }
    
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/groups/{groupId}/revoke-admin/{userId}")
    public ResponseEntity<?> revokeAdminFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        superAdminService.revokeAdminFromGroup(groupId, userId);
        return ResponseEntity.ok("Admin role revoked from user for the group.");
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/groups/{groupId}")
    public ResponseEntity<?> removeGroup(@PathVariable Long groupId) {
        superAdminService.removeGroup(groupId);
        return ResponseEntity.ok("Group removed successfully.");
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/users/{userId}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable Long userId) {
        superAdminService.deactivateUser(userId);
        return ResponseEntity.ok("User with ID " + userId + " has been deactivated.");
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/users/{userId}/reactivate")
    public ResponseEntity<String> reactivateUser(@PathVariable Long userId) {
        superAdminService.reactivateUser(userId);
        return ResponseEntity.ok("User with ID " + userId + " has been reactivated.");
    }


    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/users/excel")
    public ResponseEntity<InputStreamResource> downloadUsersExcel() {
        var excelStream = superAdminService.generateUsersExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(excelStream));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/groups/excel")
    public ResponseEntity<InputStreamResource> downloadGroupsExcel() {
        var excelStream = superAdminService.generateGroupsExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=groups.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(excelStream));
    }




    @PreAuthorize("hasRole('SUPER_ADMIN')")
@GetMapping("/users/{userId}/expenses")
public ResponseEntity<?> getUserExpenses(@PathVariable Long userId, @RequestParam String period) {
    try {
        List<ExpenseDTO> expenses = reportService.getUserExpenses(userId, period);
        return ResponseEntity.ok(expenses);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}


    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/groups/reports")
    public ResponseEntity<?> getGroupReports(@RequestParam String period) {
        try {
            List<GroupReportDTO> reports = reportService.getGroupReports(period);
            return ResponseEntity.ok(reports);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


// @PreAuthorize("hasRole('SUPER_ADMIN')")
//     @GetMapping("/users/{userId}/expenses/excel")
//     public ResponseEntity<InputStreamResource> downloadUserExpensesExcel(@PathVariable Long userId, @RequestParam String period) {
//         ByteArrayInputStream excelStream = reportService.generateUserExpensesExcel(userId, period);
//         return ResponseEntity.ok()
//                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user_expenses.xlsx")
//                 .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
//                 .body(new InputStreamResource(excelStream));
//     }

//     @PreAuthorize("hasRole('SUPER_ADMIN')")
//     @GetMapping("/groups/reports/excel")
//     public ResponseEntity<InputStreamResource> downloadGroupReportsExcel(@RequestParam String period) {
//         ByteArrayInputStream excelStream = reportService.generateGroupReportsExcel(period);
//         return ResponseEntity.ok()
//                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=group_reports.xlsx")
//                 .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
//                 .body(new InputStreamResource(excelStream));
//     }
}
