package Expense.Management.service;

import java.util.List;

import org.springframework.stereotype.Service;

import Expense.Management.model.Role;
import Expense.Management.model.User;
import Expense.Management.repository.UserRepository;
import Expense.Management.repository.GroupRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuperAdminService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void assignAdminToGroup(Long groupId, Long userId) {
        groupRepository.findById(groupId).ifPresent(group -> {
            userRepository.findById(userId).ifPresent(user -> {
                user.setRole(Role.ADMIN);
                userRepository.save(user);
            });
        });
    }

    public void revokeAdminFromGroup(Long groupId, Long userId) {
        groupRepository.findById(groupId).ifPresent(group -> {
            userRepository.findById(userId).ifPresent(user -> {
                if (user.getRole() == Role.ADMIN) {
                    user.setRole(Role.USER);
                    userRepository.save(user);
                }
            });
        });
    }

    // public List<User> getActiveUsers() {
    //     // Example logic: Replace with actual implementation to fetch active users
    //     return userRepository.findAll(); // Add logic for determining active users
    // }
}

