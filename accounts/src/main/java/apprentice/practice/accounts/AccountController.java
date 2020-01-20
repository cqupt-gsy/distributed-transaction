package apprentice.practice.accounts;

import apprentice.practice.accounts.command.CreateAccountCommand;
import apprentice.practice.accounts.services.AccountBackUpDTO;
import apprentice.practice.accounts.services.AccountDTO;
import apprentice.practice.accounts.services.AccountService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/accounts")
public class AccountController {

  private final AccountService accountService;

  @Autowired
  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @PostMapping(value = "/create")
  public void create(@RequestBody CreateAccountCommand command) {
    accountService.create(command);
  }

  @GetMapping
  public List<AccountDTO> findAllAccount() {
    return accountService.findAll();
  }

  @GetMapping(value = "/{userId}")
  public AccountDTO findUserById(@PathVariable(value = "userId") Integer userId) {
    return accountService.findById(userId);
  }

  @GetMapping(value = "/first/{first}/second/{second}")
  public List<AccountDTO> findUserByIds(
      @PathVariable(value = "first") Integer first,
      @PathVariable(value = "second") Integer second) {
    return accountService.findByIds(first, second);
  }

  @GetMapping(value = "/backups")
  public List<AccountBackUpDTO> findAllBackUpDTO() {
    return accountService.findAllBackUp();
  }

  @GetMapping(value = "/backups/{userId}")
  public List<AccountBackUpDTO> findBackUpById(@PathVariable(value = "userId") Integer userId) {
    return accountService.findBackUpById(userId);
  }

  @GetMapping(value = "/backups/first/{first}/second/{second}")
  public List<AccountBackUpDTO> findBackUpByIds(
      @PathVariable(value = "first") Integer first,
      @PathVariable(value = "second") Integer second) {
    return accountService.findByBackUpByIds(first, second);
  }
}
