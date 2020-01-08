package apprentice.practice.api.services.accounts;

import apprentice.practice.api.services.command.CreateAccountCommand;
import apprentice.practice.api.services.command.TransferCommand;
import apprentice.practice.api.services.enums.Results;

public interface AccountService {

  void create(CreateAccountCommand createAccountCommand);

  Results tryTransferFrom(TransferCommand transferCommand);

  void confirmTransferFrom(TransferCommand transferCommand);

  void cancelTransferFrom(TransferCommand transferCommand);

  Results tryTransferTo(TransferCommand transferCommand);

  void confirmTransferTo(TransferCommand transferCommand);

  void cancelTransferTo(TransferCommand transferCommand);
}
