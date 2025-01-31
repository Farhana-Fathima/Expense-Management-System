package Expense.Management.service;

import Expense.Management.DTO.GroupRequest;
import Expense.Management.DTO.GroupResponse;
import Expense.Management.DTO.ReceiptDTO;
import Expense.Management.DTO.UserDTO;
import Expense.Management.model.Group;
import Expense.Management.model.Receipt;
import Expense.Management.model.User;
import Expense.Management.repository.GroupRepository;
import Expense.Management.repository.ReceiptRepository;
import Expense.Management.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ReceiptRepository receiptRepository;

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


    @Value("${FILE_UPLOAD_DIR}")
    private String uploadDir;

    @Transactional
    public String uploadReceipt(Long groupId, MultipartFile file, String username) {
        if (file.isEmpty() || file.getSize() > 2 * 1024 * 1024) {
            throw new ValidationException("File must not be empty and should be less than 2MB");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ValidationException("Group not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ValidationException("User not found"));

        if (!group.getParticipants().contains(user)) {
            throw new ValidationException("User is not a member of the group");
        }

        try {
            Path directory = Paths.get(uploadDir);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = directory.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath);

            Receipt receipt = Receipt.builder()
                    .group(group)
                    .uploadedBy(user)
                    .fileName(uniqueFileName)
                    .filePath(filePath.toString())
                    .uploadedAt(LocalDateTime.now())
                    .build();
            receiptRepository.save(receipt);
            return "Receipt uploaded successfully";
        } catch (IOException e) {
            throw new ValidationException("Error saving file: " + e.getMessage());
        }
    }

    @Transactional
    public List<ReceiptDTO> getReceipts(Long groupId, String username) {
    Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new ValidationException("Group not found"));
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ValidationException("User not found"));

    if (!group.getParticipants().contains(user)) {
        throw new ValidationException("User is not a member of the group");
    }

    List<Receipt> receipts = receiptRepository.findByGroup(group);
    return receipts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}

private ReceiptDTO convertToDTO(Receipt receipt) {
    ReceiptDTO dto = new ReceiptDTO();
    dto.setId(receipt.getId());
    dto.setFileName(receipt.getFileName());
    dto.setFilePath(receipt.getFilePath());
    dto.setUploadedAt(receipt.getUploadedAt());

    UserDTO userDTO = new UserDTO();
    userDTO.setId(receipt.getUploadedBy().getId());
    userDTO.setUsername(receipt.getUploadedBy().getUsername());
    dto.setUploadedBy(userDTO);

    return dto;
}
}

