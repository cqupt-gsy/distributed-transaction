package apprentice.practice.accounts.services;

import apprentice.practice.accounts.AccountRepository;
import apprentice.practice.accounts.model.Account;
import apprentice.practice.api.services.accounts.AccountService;
import apprentice.practice.api.services.command.CreateAccountCommand;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;

  @Autowired
  public AccountServiceImpl(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public void create(CreateAccountCommand createAccountCommand) {
    Account account = Account.createBy(createAccountCommand);
    accountRepository.save(account);
  }
}
