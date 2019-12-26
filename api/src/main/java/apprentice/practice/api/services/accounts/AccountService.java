package apprentice.practice.api.services.accounts;

import apprentice.practice.api.services.command.CreateAccountCommand;
import apprentice.practice.api.services.command.TransferCommand;

public interface AccountService {

  void create(CreateAccountCommand createAccountCommand);

  boolean transferFrom(TransferCommand transferCommand);

  boolean transferTo(TransferCommand transferCommand);
}
