package Expense.Management.service;

import Expense.Management.DTO.ExpenseSplitRequest;
import Expense.Management.DTO.ExpenseSplitResponse;
import Expense.Management.DTO.ParticipantPaymentRequest;
import Expense.Management.exception.UnauthorizedException;
import Expense.Management.model.*;
import Expense.Management.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ExpenseSplitServiceImpl implements ExpenseSplitService {

    private final ExpenseSplitRepository expenseSplitRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ParticipantShareRepository participantShareRepository;

    public ExpenseSplitServiceImpl(ExpenseSplitRepository expenseSplitRepository, 
                                 GroupRepository groupRepository,
                                 UserRepository userRepository, 
                                 ParticipantShareRepository participantShareRepository) {
        this.expenseSplitRepository = expenseSplitRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.participantShareRepository = participantShareRepository;
    }

    @Override
    @Transactional
    public ExpenseSplitResponse createExpenseSplit(ExpenseSplitRequest request, String username) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new NoSuchElementException("Group not found"));

        if (!group.getParticipants().contains(creator)) {
            throw new UnauthorizedException("Only group members can create an expense.");
        }

        ExpenseSplit expenseSplit = createExpenseSplitEntity(request, creator, group);
        expenseSplit = expenseSplitRepository.save(expenseSplit);

        List<ParticipantShare> shares = createParticipantShares(request, expenseSplit);
        participantShareRepository.saveAll(shares);
        
        expenseSplit.setParticipantShares(shares);
        expenseSplit = expenseSplitRepository.save(expenseSplit);

        return ExpenseSplitResponse.fromEntity(expenseSplit);
    }

    private ExpenseSplit createExpenseSplitEntity(ExpenseSplitRequest request, User creator, Group group) {
        ExpenseSplit expenseSplit = new ExpenseSplit();
        expenseSplit.setGroup(group);
        expenseSplit.setCreator(creator);
        expenseSplit.setTitle(request.getTitle());
        expenseSplit.setAmount(request.getAmount());
        expenseSplit.setCategory(request.getCategory());
        return expenseSplit;
    }

    private List<ParticipantShare> createParticipantShares(ExpenseSplitRequest request, ExpenseSplit expenseSplit) {
        List<ParticipantShare> shares = new ArrayList<>();
        int participantCount = request.getParticipantIds().size();
        BigDecimal shareAmount = expenseSplit.getAmount()
                .divide(BigDecimal.valueOf(participantCount), RoundingMode.HALF_UP);
        BigDecimal sharePercentage = BigDecimal.valueOf(100)
                .divide(BigDecimal.valueOf(participantCount), RoundingMode.HALF_UP);

        for (Long participantId : request.getParticipantIds()) {
            User participant = userRepository.findById(participantId)
                    .orElseThrow(() -> new NoSuchElementException("Participant not found"));

            if (!expenseSplit.getGroup().getParticipants().contains(participant)) {
                throw new UnauthorizedException("Only group members can participate in an expense.");
            }

            shares.add(createParticipantShare(expenseSplit, participant, shareAmount, sharePercentage));
        }
        return shares;
    }

    private ParticipantShare createParticipantShare(ExpenseSplit expenseSplit, User participant, 
            BigDecimal amount, BigDecimal percentage) {
        ParticipantShare share = new ParticipantShare();
        share.setExpenseSplit(expenseSplit);
        share.setParticipant(participant);
        share.setAmount(amount);
        share.setPercentage(percentage);
        share.setPaid(false);
        return share;
    }

    @Override
    public List<ExpenseSplitResponse> getExpensesByGroup(Long groupId, String username) {
        validateUser(username);
        return expenseSplitRepository.findByGroupId(groupId).stream()
                .map(ExpenseSplitResponse::fromEntity)
                .toList();
    }

    @Override
    public ExpenseSplitResponse getExpenseById(Long expenseId, String username) {
        validateUser(username);
        return expenseSplitRepository.findById(expenseId)
                .map(ExpenseSplitResponse::fromEntity)
                .orElseThrow(() -> new NoSuchElementException("Expense not found"));
    }

    @Override
    @Transactional
    public ExpenseSplitResponse updateExpenseSplit(Long expenseId, ExpenseSplitRequest request, String username) {
        User user = validateUser(username);
        ExpenseSplit existingExpense = expenseSplitRepository.findById(expenseId)
                .orElseThrow(() -> new NoSuchElementException("Expense not found"));

        validateExpenseCreator(existingExpense, user);

        existingExpense.setTitle(request.getTitle());
        existingExpense.setAmount(request.getAmount());
        existingExpense.setCategory(request.getCategory());
        
        return ExpenseSplitResponse.fromEntity(expenseSplitRepository.save(existingExpense));
    }

    @Override
    @Transactional
    public void deleteExpenseSplit(Long expenseId, String username) {
        User user = validateUser(username);
        ExpenseSplit expense = expenseSplitRepository.findById(expenseId)
                .orElseThrow(() -> new NoSuchElementException("Expense not found"));

        validateExpenseCreator(expense, user);
        expenseSplitRepository.delete(expense);
    }

    @Override
    @Transactional
    public void payExpenseShare(Long expenseId, ParticipantPaymentRequest request, String username) {
        validateUser(username);
        ParticipantShare share = participantShareRepository
                .findByExpenseSplitIdAndParticipant_Username(expenseId, username)
                .orElseThrow(() -> new NoSuchElementException("Participant share not found"));

        validatePayment(share, request.getAmount());
        
        share.setPaid(true);
        share.setPaidAt(LocalDateTime.now());
        participantShareRepository.save(share);
    }

    private User validateUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    private void validateExpenseCreator(ExpenseSplit expense, User user) {
        if (!expense.getCreator().equals(user)) {
            throw new UnauthorizedException("Only the creator can modify this expense.");
        }
    }

    private void validatePayment(ParticipantShare share, BigDecimal paymentAmount) {
        if (share.isPaid()) {
            throw new IllegalStateException("This expense share has already been paid.");
        }
        if (paymentAmount.compareTo(share.getAmount()) < 0) {
            throw new IllegalArgumentException("Payment amount is less than required.");
        }
    }
}



