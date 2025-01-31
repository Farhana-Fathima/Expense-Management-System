package Expense.Management.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GroupReportDTO {
    private Long groupId;
    private String groupName;
    private String description;
    private String adminName;
    private List<ExpenseSplitDTO> expenseSplits;
}
