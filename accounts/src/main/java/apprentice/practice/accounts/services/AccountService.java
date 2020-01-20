package apprentice.practice.accounts.services;

import apprentice.practice.accounts.AccountRepository;
import apprentice.practice.accounts.command.CreateAccountCommand;
import apprentice.practice.api.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountService {

  private final AccountRepository accountRepository;

  @Autowired
  public AccountService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public void create(CreateAccountCommand command) {
    Account account =
        Account.createBy(command.getPhoneNumber(), command.getName(), command.getBalance());
    accountRepository.saveAccount(account);
  }
}
