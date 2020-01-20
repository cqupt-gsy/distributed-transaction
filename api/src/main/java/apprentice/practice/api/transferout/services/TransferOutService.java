package apprentice.practice.api.transferout.services;

import apprentice.practice.api.enums.Results;
import apprentice.practice.api.transferout.command.TransferOutCommand;

public interface TransferOutService {

  Results tryTransferOut(TransferOutCommand command);

  void confirmTransferOut(TransferOutCommand command);

  void cancelTransferOut(TransferOutCommand command);
}
