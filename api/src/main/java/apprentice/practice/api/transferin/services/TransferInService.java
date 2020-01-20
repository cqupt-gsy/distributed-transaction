package apprentice.practice.api.transferin.services;

import apprentice.practice.api.enums.Results;
import apprentice.practice.api.transferin.command.TransferInCommand;

public interface TransferInService {

  Results tryTransferIn(TransferInCommand command);

  void confirmTransferIn(TransferInCommand command);

  void cancelTransferIn(TransferInCommand command);
}
