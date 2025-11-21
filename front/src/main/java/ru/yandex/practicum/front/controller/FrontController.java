package ru.yandex.practicum.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.yandex.practicum.front.dto.AccountWithUsersDto;
import ru.yandex.practicum.front.enums.ActionEnum;
import ru.yandex.practicum.front.service.AccountService;
import ru.yandex.practicum.front.service.CashService;
import ru.yandex.practicum.front.service.TransferService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FrontController {
    private final AccountService accountService;
    private final CashService cashService;
    private final TransferService transferService;

    @GetMapping("/")
    public String mainPage(@AuthenticationPrincipal UserDetails customUserDetails,
                           Model model) {
        AccountWithUsersDto accountWithUsersDto = accountService.getAccountWithAllUsers(customUserDetails.getUsername());
        model.addAttribute("login", accountWithUsersDto.getEmail());
        model.addAttribute("firstName", accountWithUsersDto.getFirstName());
        model.addAttribute("lastName", accountWithUsersDto.getLastName());
        model.addAttribute("birthdate", accountWithUsersDto.getBirthDate());
        model.addAttribute("usersList", accountWithUsersDto.getShortAccountDtoList());
        return "main";
    }

    @GetMapping("/sign-up")
    public String signUp(Model model) {
        model.addAttribute("login", model.getAttribute("login"));
        model.addAttribute("password", model.getAttribute("password"));
        model.addAttribute("firstName", model.getAttribute("firstName"));
        model.addAttribute("lastName", model.getAttribute("lastName"));
        model.addAttribute("email", model.getAttribute("email"));
        model.addAttribute("birthdate", model.getAttribute("birthdate"));
        model.addAttribute("errorList", model.getAttribute("errorList"));
        return "signup";
    }

    @PostMapping("/account")
    public String createAccount(@RequestParam(name = "login") String login,
                                @RequestParam(name = "password") String password,
                                @RequestParam(name = "firstName") String firstName,
                                @RequestParam(name = "lastName") String lastName,
                                @RequestParam(name = "email") String email,
                                @RequestParam(name = "birthdate") String birthdate,
                                Model model) {
        List<String> errorList = accountService.createAccount(login, password, firstName, lastName, birthdate, email);
        if (errorList.isEmpty()) {
            return "redirect:/";
        } else {
            model.addAttribute("login", login);
            model.addAttribute("password", password);
            model.addAttribute("firstName", firstName);
            model.addAttribute("lastName", lastName);
            model.addAttribute("email", email);
            model.addAttribute("birthdate", birthdate);
            model.addAttribute("errorList", errorList);
            return "signup";
        }
    }

    @PostMapping("/account/password")
    public String changePassword(@AuthenticationPrincipal UserDetails customUserDetails,
                                 @RequestParam(name = "password") String password,
                                 RedirectAttributes redirectAttributes) {
        List<String> errorList = accountService.changePassword(customUserDetails.getUsername(), password);
        redirectAttributes.addFlashAttribute("errorPasswordList", errorList);
        return "redirect:/";
    }

    @PostMapping("/account/info")
    public String changeInfo(@AuthenticationPrincipal UserDetails customUserDetails,
                             @RequestParam(name = "firstName") String firstName,
                             @RequestParam(name = "lastName") String lastName,
                             @RequestParam(name = "birthdate") String birthdate,
                             RedirectAttributes redirectAttributes) {
        List<String> errorList = accountService.changeInfo(customUserDetails.getUsername(), firstName, lastName, birthdate);
        redirectAttributes.addFlashAttribute("errorInfoList", errorList);
        return "redirect:/";
    }

    @PostMapping("/account/balance")
    public String changeAccountBalance(@AuthenticationPrincipal UserDetails customUserDetails,
                                       @RequestParam(name = "action") ActionEnum actionEnum,
                                       @RequestParam(name = "balance") BigDecimal balance,
                                       RedirectAttributes redirectAttributes) {
        List<String> errorList = cashService.changeAccountBalance(customUserDetails.getUsername(), actionEnum, balance);
        redirectAttributes.addFlashAttribute("errorBalanceList", errorList);
        return "redirect:/";
    }

    @PostMapping("/account/transfer")
    public String transfer(@AuthenticationPrincipal UserDetails customUserDetails,
                           @RequestParam(name = "loginTo") String loginTo,
                           @RequestParam(name = "transferAmount") BigDecimal transferAmount,
                           RedirectAttributes redirectAttributes) {
        List<String> errorList = transferService.transfer(customUserDetails.getUsername(), loginTo, transferAmount);
        redirectAttributes.addFlashAttribute("errorTransferList", errorList);
        return "redirect:/";
    }
}