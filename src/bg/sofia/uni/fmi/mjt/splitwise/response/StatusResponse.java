package bg.sofia.uni.fmi.mjt.splitwise.response;

import bg.sofia.uni.fmi.mjt.splitwise.containers.Debt;

import java.util.List;

public record StatusResponse(ResponseStatus responseStatus, List<String> debts) implements Response {
    public static StatusResponse of(List<Debt> debts) {
        List<String> debtsMessages = debts.stream()
                .map(Debt::debtMessage)
                .toList();
        return new StatusResponse(ResponseStatus.SUCCESSFUL, debtsMessages);
    }

    @Override
    public String getResponse() {
        StringBuilder buildResponse = new StringBuilder("Your notifications are:\n");
        debts.forEach(debt -> buildResponse.append(debt).append('\n'));
        return buildResponse.toString();
    }
}
