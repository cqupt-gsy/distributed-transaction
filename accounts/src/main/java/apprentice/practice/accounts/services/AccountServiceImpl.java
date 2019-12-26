package apprentice.practice.accounts.services;

import apprentice.practice.accounts.AccountRepository;
import apprentice.practice.accounts.model.Account;
import apprentice.practice.api.services.accounts.AccountService;
import apprentice.practice.api.services.command.CreateAccountCommand;
import apprentice.practice.api.services.command.TransferCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
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
  public boolean transferFrom(TransferCommand command) {
    log.info("Transfer out from {} with {}", command.getTransferId(), command.getBalance());
    return accountRepository.transferFrom(command);
  }

  @Override
  @Transactional
  public boolean transferTo(TransferCommand command) {
    log.info("Transfer in to {} with {}", command.getTransferId(), command.getBalance());
    return accountRepository.transferTo(command);
  }
}
