package apprentice.practice.transactions.controllers;

import apprentice.practice.api.accounts.services.AccountService;
import apprentice.practice.accounts.command.CreateAccountCommand;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountsController {

  @Reference
  private AccountService accountService;

  @PostMapping("/create")
  public void create(@RequestBody CreateAccountCommand command) {
    accountService.create(command);
  }
}
