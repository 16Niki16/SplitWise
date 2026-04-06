package bg.sofia.uni.fmi.mjt.splitwise.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApplicationServices {
    private UserService userService;
    private GroupService groupService;
    private DebtsService debtsService;
    private CurrencyService currencyService;
}
