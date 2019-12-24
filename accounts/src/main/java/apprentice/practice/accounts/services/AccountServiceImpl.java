package apprentice.practice.accounts.services;

import apprentice.practice.accounts.AccountRepository;
import apprentice.practice.accounts.model.Account;
import apprentice.practice.api.services.accounts.AccountService;
import apprentice.practice.api.services.command.CreateAccountCommand;
import apprentice.practice.api.services.command.TransferFromCommand;
import apprentice.practice.api.services.command.TransferToCommand;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;

  @Autowired
  public AccountServiceImpl(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public void create(CreateAccountCommand command) {
    Account account = Account.createBy(command);
    accountRepository.save(account);
  }

  @Override
  @Transactional
  public boolean transferFrom(TransferFromCommand command) {
    return accountRepository.transferFrom(command);
  }

  @Override
  @Transactional
  public boolean transferTo(TransferToCommand command) {
    return accountRepository.transferTo(command);
  }
}
