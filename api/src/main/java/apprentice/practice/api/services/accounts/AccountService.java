package apprentice.practice.api.services.accounts;

import apprentice.practice.api.services.command.CreateAccountCommand;
import apprentice.practice.api.services.command.TransferFromCommand;
import apprentice.practice.api.services.command.TransferToCommand;

public interface AccountService {

  void create(CreateAccountCommand createAccountCommand);

  boolean transferFrom(TransferFromCommand transferFromCommand);

  boolean transferTo(TransferToCommand transferToCommand);
}
