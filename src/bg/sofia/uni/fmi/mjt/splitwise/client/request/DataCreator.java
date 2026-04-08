package bg.sofia.uni.fmi.mjt.splitwise.client.request;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.AddFriendData;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.CreateAccountData;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.CreateGroupData;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.PayData;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.SplitData;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.SplitGroupData;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandType;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.CommandNotKnownException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DataCreator {
    private static final Map<CommandType, DataParser> DATA = new EnumMap<>(CommandType.class);

    public DataCreator() {
        DATA.put(CommandType.CREATE_ACCOUNT, args -> new CreateAccountData(args[0], args[1]));
        DATA.put(CommandType.LOGIN, args -> new LoginData(args[0], args[1]));
        DATA.put(CommandType.HELP, null);
        DATA.put(CommandType.ADD_FRIEND, args -> new AddFriendData(args[0]));
        DATA.put(CommandType.CREATE_GROUP, args -> new CreateGroupData(args[0], getParticipants(args)));
        DATA.put(CommandType.GET_STATUS, null);
        DATA.put(CommandType.SPLIT, args -> new SplitData(args[1], args[2], new BigDecimal(args[0])));
        DATA.put(CommandType.SPLIT_GROUP, args -> new SplitGroupData(args[1], new BigDecimal(args[0]), args[2]));
        DATA.put(CommandType.PAID, args -> new PayData(args[1], new BigDecimal(args[0])));
    }

    public Data createData(CommandLine commandLine) {
        CommandType commandType = CommandType.of(commandLine.line());
        DataParser parser = DATA.get(commandType);
        if (parser == null) {
            throw new CommandNotKnownException("The provided command is not in the list!");
        }

        return parser.parse(commandLine.args());
    }

    private Set<String> getParticipants(String[] args) {
        return Arrays.stream(args)
                .skip(1)
                .collect(Collectors.toSet());
    }
}
