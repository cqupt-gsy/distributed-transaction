package apprentice.practice.api.services.accounts;

import apprentice.practice.api.services.command.CreateAccountCommand;

public interface AccountService {

  void create(CreateAccountCommand createAccountCommand);
}
