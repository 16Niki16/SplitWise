package bg.sofia.uni.fmi.mjt.splitwise.service;

import bg.sofia.uni.fmi.mjt.splitwise.repository.DebtsRepository;
import bg.sofia.uni.fmi.mjt.splitwise.user.Debt;

import java.math.BigDecimal;

public class DebtsService {
    private final DebtsRepository debtsRepository;

    public DebtsService(DebtsRepository debtsRepository) {
        this.debtsRepository = debtsRepository;
    }

    public void addDebt(String from, String to, BigDecimal amount) {
        Debt direct = debtsRepository.findDebt(from, to);
        Debt reverse = debtsRepository.findDebt(to, from);

        if (reverse != null) {
            int cmp = reverse.getAmount().compareTo(amount);

            if (cmp > 0) {
                reverse.paid(amount);
            } else if (cmp < 0) {
                debtsRepository.removeDebt(reverse);
                debtsRepository.addDebt(new Debt(from, to, amount.subtract(reverse.getAmount())));
            } else {
                debtsRepository.removeDebt(reverse);
            }

        } else if (direct != null) {
            direct.paid(amount);
        } else {
            debtsRepository.addDebt(new Debt(from, to, amount));
        }
    }
}
