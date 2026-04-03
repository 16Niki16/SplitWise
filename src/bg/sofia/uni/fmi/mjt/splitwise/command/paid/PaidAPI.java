package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;

public interface PaidAPI {
    /**
     * made payment to a person
     * */
    String personPay(CommandLine command);
}
