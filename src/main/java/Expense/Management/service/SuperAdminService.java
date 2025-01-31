package Expense.Management.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Expense.Management.model.Group;
import Expense.Management.model.Role;
import Expense.Management.model.User;
import Expense.Management.repository.GroupRepository;
import Expense.Management.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuperAdminService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

public ByteArrayInputStream generateUsersExcel() {
        List<User> users = getAllUsers();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Users");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Email");
            headerRow.createCell(3).setCellValue("Role");
            headerRow.createCell(4).setCellValue("Active");

            int rowIdx = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getEmail());
                row.createCell(3).setCellValue(user.getRole().toString());
                row.createCell(4).setCellValue(user.isActive());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel file", e);
        }
    }

    public ByteArrayInputStream generateGroupsExcel() {
        List<Group> groups = getAllGroups();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Groups");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Description");

            int rowIdx = 1;
            for (Group group : groups) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(group.getId());
                row.createCell(1).setCellValue(group.getName());
                row.createCell(2).setCellValue(group.getDescription());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel file", e);
        }
    }


    public void assignAdminToGroup(Long groupId, Long userId) {
        // Fetch the group
        var group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found with ID: " + groupId));
    
        // Fetch the user
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));
    
        // Check if the user is a participant of the group
        if (!group.getParticipants().contains(user)) {
            throw new IllegalStateException("User with ID " + userId + " is not a participant of group with ID " + groupId);
        }
    
        // Assign ADMIN role
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }
    
    public void revokeAdminFromGroup(Long groupId, Long userId) {
        // Fetch the group
        var group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found with ID: " + groupId));
    
        // Fetch the user
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));
    
        // Check if the user is a participant of the group
        if (!group.getParticipants().contains(user)) {
            throw new IllegalStateException("User with ID " + userId + " is not a participant of group with ID " + groupId);
        }
    
        // Revoke ADMIN role only if the user is currently an ADMIN
        if (user.getRole() == Role.ADMIN) {
            user.setRole(Role.USER);
            userRepository.save(user);
        } else {
            throw new IllegalStateException("User with ID " + userId + " does not have ADMIN role in group with ID " + groupId);
        }
    }
    

    @Transactional
    public void removeGroup(Long groupId) {
    Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new NoSuchElementException("Group not found with ID: " + groupId));
    groupRepository.delete(group);
}

public void deactivateUser(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));
    user.setActive(false);
    userRepository.save(user);
}

public void reactivateUser(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));
    user.setActive(true);
    userRepository.save(user);
}

}
