package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;

public interface PaidAPI {
    /**
     * made payment to a person
     * */
    String personPay(Command command);
}
