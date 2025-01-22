package Expense.Management.service;

import Expense.Management.DTO.GroupRequest;
import Expense.Management.DTO.GroupResponse;
import Expense.Management.model.Group;
import Expense.Management.model.User;
import Expense.Management.repository.GroupRepository;
import Expense.Management.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Transactional
    public GroupResponse createGroup(GroupRequest request, String creatorUsername) {
        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new ValidationException("Creator not found"));

        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setAdmin(creator);
        group.setParticipants(List.of(creator));
        group.setCreatedAt(LocalDateTime.now());
        group.setUpdatedAt(LocalDateTime.now());

        groupRepository.save(group);

        return mapToResponse(group);
    }

    @Transactional
    public GroupResponse updateGroup(Long groupId, GroupRequest request, String adminUsername) {
        Group group = getGroupIfAdmin(groupId, adminUsername);
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setUpdatedAt(LocalDateTime.now());
        groupRepository.save(group);
        return mapToResponse(group);
    }

    @Transactional
    public void addParticipant(Long groupId, String username, String adminUsername) {
        Group group = getGroupIfAdmin(groupId, adminUsername);
        User participant = userRepository.findByUsername(username)
                .orElseThrow(() -> new ValidationException("User not found"));
        if (!group.getParticipants().contains(participant)) {
            group.getParticipants().add(participant);
            group.setUpdatedAt(LocalDateTime.now());
            groupRepository.save(group);
        }
    }

    @Transactional
    public void removeParticipant(Long groupId, String username, String adminUsername) {
        Group group = getGroupIfAdmin(groupId, adminUsername);
        User participant = userRepository.findByUsername(username)
                .orElseThrow(() -> new ValidationException("User not found"));
        if (!participant.equals(group.getAdmin()) && group.getParticipants().remove(participant)) {
            group.setUpdatedAt(LocalDateTime.now());
            groupRepository.save(group);
        }
    }

    @Transactional
    public void deleteGroup(Long groupId, String adminUsername) {
        Group group = getGroupIfAdmin(groupId, adminUsername);
        groupRepository.delete(group);
    }

    @Transactional
    public void transferAdmin(Long groupId, String newAdminUsername, String adminUsername) {
        Group group = getGroupIfAdmin(groupId, adminUsername);
        User newAdmin = userRepository.findByUsername(newAdminUsername)
                .orElseThrow(() -> new ValidationException("New admin not found"));
        if (group.getParticipants().contains(newAdmin)) {
            group.setAdmin(newAdmin);
            group.setUpdatedAt(LocalDateTime.now());
            groupRepository.save(group);
        } else {
            throw new ValidationException("New admin must be a participant of the group");
        }
    }

    @Transactional
    public void leaveGroup(Long groupId, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ValidationException("Group not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ValidationException("User not found"));

        if (user.equals(group.getAdmin())) {
            throw new ValidationException("Admin cannot leave the group without transferring admin rights");
        }

        if (group.getParticipants().remove(user)) {
            group.setUpdatedAt(LocalDateTime.now());
            groupRepository.save(group);
        }
    }

    private Group getGroupIfAdmin(Long groupId, String adminUsername) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ValidationException("Group not found"));
        if (!group.getAdmin().getUsername().equals(adminUsername)) {
            throw new ValidationException("User is not the admin of the group");
        }
        return group;
    }

    private GroupResponse mapToResponse(Group group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .admin(group.getAdmin().getUsername())
                .participants(group.getParticipants().stream().map(User::getUsername).toList())
                .build();
    }
}

