package Expense.Management.controller;

import Expense.Management.DTO.GroupRequest;
import Expense.Management.DTO.GroupResponse;
import Expense.Management.DTO.ReceiptDTO;
import Expense.Management.model.Receipt;
import Expense.Management.service.GroupService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    // creator is the admin
    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@RequestBody GroupRequest request, Authentication authentication) {
        String creatorUsername = authentication.getName();
        return ResponseEntity.ok(groupService.createGroup(request, creatorUsername));
    }

    // for admin
    @PutMapping("/{groupId}")
    public ResponseEntity<GroupResponse> updateGroup(
            @PathVariable Long groupId,
            @RequestBody GroupRequest request,
            Authentication authentication) {
        String adminUsername = authentication.getName();
        return ResponseEntity.ok(groupService.updateGroup(groupId, request, adminUsername));
    }

    // for admin
    @PostMapping("/{groupId}/add-participant")
    public ResponseEntity<Void> addParticipant(@PathVariable Long groupId, @RequestParam String username, Authentication authentication) {
        String adminUsername = authentication.getName();
        groupService.addParticipant(groupId, username, adminUsername);
        return ResponseEntity.ok().build();
    }

    // for admin
    @DeleteMapping("/{groupId}/remove-participant")
    public ResponseEntity<Void> removeParticipant(@PathVariable Long groupId, @RequestParam String username, Authentication authentication) {
        String adminUsername = authentication.getName();
        groupService.removeParticipant(groupId, username, adminUsername);
        return ResponseEntity.ok().build();
    }

    // for admin
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId, Authentication authentication) {
        String adminUsername = authentication.getName();
        groupService.deleteGroup(groupId, adminUsername);
        return ResponseEntity.ok().build();
    }

    // for admin
    @PutMapping("/{groupId}/transfer-admin")
    public ResponseEntity<Void> transferAdmin(@PathVariable Long groupId, @RequestParam String newAdminUsername, Authentication authentication) {
        String adminUsername = authentication.getName();
        groupService.transferAdmin(groupId, newAdminUsername, adminUsername);
        return ResponseEntity.ok().build();
    }

    // for every user(group member))
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(@PathVariable Long groupId, Authentication authentication) {
        String username = authentication.getName();
        groupService.leaveGroup(groupId, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{groupId}/upload-receipt")
    public ResponseEntity<String> uploadReceipt(
            @PathVariable Long groupId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        String username = authentication.getName();
        String response = groupService.uploadReceipt(groupId, file, username);
        return ResponseEntity.ok(response);
    }

   @GetMapping("/{groupId}/receipts")
public ResponseEntity<List<ReceiptDTO>> getReceipts(
        @PathVariable Long groupId,
        Authentication authentication) {
    String username = authentication.getName();
    List<ReceiptDTO> receipts = groupService.getReceipts(groupId, username);
    return ResponseEntity.ok(receipts);
}
}

