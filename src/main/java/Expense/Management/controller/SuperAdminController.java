package Expense.Management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import Expense.Management.service.SuperAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/superadmin")
@RequiredArgsConstructor
public class SuperAdminController {
    private final SuperAdminService superAdminService;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(superAdminService.getAllUsers());
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

    // @GetMapping("/active-users")
    // public ResponseEntity<?> getActiveUsers() {
    //     return ResponseEntity.ok(superAdminService.getActiveUsers());
    // }
}

