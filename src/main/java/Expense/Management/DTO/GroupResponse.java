package Expense.Management.DTO;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupResponse {
    private Long id;
    private String name;
    private String description;
    private String admin;
    private List<String> participants;
}
