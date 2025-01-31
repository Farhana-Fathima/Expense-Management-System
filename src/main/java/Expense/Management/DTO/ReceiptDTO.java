package Expense.Management.DTO;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReceiptDTO {
    private Long id;
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;
    private UserDTO uploadedBy;


}

