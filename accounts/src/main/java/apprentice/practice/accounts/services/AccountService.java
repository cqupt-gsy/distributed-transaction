package apprentice.practice.accounts.services;

import static java.util.stream.Collectors.toList;

import apprentice.practice.accounts.AccountRepository;
import apprentice.practice.accounts.command.CreateAccountCommand;
import apprentice.practice.api.model.Account;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AccountService {

  private final AccountRepository accountRepository;

  @Autowired
  public AccountService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Transactional
  public void create(CreateAccountCommand command) {
    Account account =
        Account.createBy(command.getPhoneNumber(), command.getName(), command.getBalance());
    accountRepository.saveAccount(account);
  }

  public List<AccountDTO> findAll() {
    return accountRepository.findAll().stream().map(AccountDTO::createBy).collect(toList());
  }

  public AccountDTO findById(Integer userId) {
    return AccountDTO.createBy(accountRepository.findAccountBy(userId));
  }

  public List<AccountDTO> findByIds(Integer... userIds) {
    return accountRepository.findAccountsBy(getIdsCondition(userIds)).stream()
        .map(AccountDTO::createBy)
        .collect(toList());
  }

  public List<AccountBackUpDTO> findAllBackUp() {
    return accountRepository.findAllBackUp().stream()
        .map(AccountBackUpDTO::createBy)
        .collect(toList());
  }

  public List<AccountBackUpDTO> findBackUpById(Integer userId) {
    return accountRepository.findBackUpBy(userId).stream()
        .map(AccountBackUpDTO::createBy)
        .collect(toList());
  }

  public List<AccountBackUpDTO> findByBackUpByIds(Integer... userIds) {
    return accountRepository.findBackUpsBy(getIdsCondition(userIds)).stream()
        .map(AccountBackUpDTO::createBy)
        .collect(toList());
  }

  private String getIdsCondition(Integer... userIds) {
    StringBuilder idBuilder = new StringBuilder();
    idBuilder.append("(");
    Stream.of(userIds)
        .forEach(
            id -> {
              idBuilder.append(id);
              idBuilder.append(",");
            });
    idBuilder.deleteCharAt(idBuilder.length() - 1);
    idBuilder.append(")");
    return idBuilder.toString();
  }
}
