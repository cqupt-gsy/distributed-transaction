package apprentice.practice.api.enums;

public enum Results {
  CREATE_TRANSACTION_SUCCESS("Transaction create success and in TRY status."),
  TRYING_STATUS("Transaction still in TRY status, retry again."),
  CONFIRM_STATUS("Transaction already finished with CONFIRM status, please do not retry."),
  CANCEL_STATUS(
      "Transaction already finished with CANCEL status, please start a new transaction and do retry."),
  DUPLICATE_KEY(
      "Transaction with same transaction number start too many times, please wait a minute for retry."),
  UNKNOWN_EXCEPTION("Transaction failed with unknown reason, please try later"),

  TRY_SUCCESS("Transfer success, can continue CONFIRM stage."),
  TRY_FAILED("Transfer failed, can continue CANCEL stage"),

  TRANSACTION_SUCCESS("OK! transaction success."),
  TRANSACTION_FAILED("Ops, transaction failed.");

  private String message;

  Results(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
