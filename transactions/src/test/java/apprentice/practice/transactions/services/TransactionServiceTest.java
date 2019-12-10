package apprentice.practice.transactions.services;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import apprentice.practice.transactions.TransactionRepository;
import apprentice.practice.transactions.command.TransactionCommand;
import java.math.BigDecimal;
import java.security.InvalidParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

  private TransactionCommand command;

  @Mock
  private TransactionRepository repository;

  @InjectMocks
  private TransactionService service;

  @BeforeEach
  void setUp() {
    command = new TransactionCommand();
    command.setTransactionNumber("transactionNumber");
    command.setTransactionMoney(BigDecimal.valueOf(0));
    command.setTransformerAccount("transformerAccount");
    command.setTransformerName("transformerName");
    command.setTransformeeAccount("transformeeAccount");
    command.setTransformeeName("transformeeName");
    command.setEnvelopeId("envelopId");
    command.setEnvelopeMoney(BigDecimal.valueOf(0));
    command.setIntegralId("integralId");
    command.setIntegral(100);
  }

  @Test
  void should_throw_exception_when_transaction_money_is_null() {
    command.setTransactionMoney(null);

    assertThatThrownBy(() -> service.begin(command))
        .hasMessage("Transaction money invalid")
        .isInstanceOf(InvalidParameterException.class);
  }

  @Test
  void should_throw_exception_when_transaction_money_is_smaller_than_0() {
    command.setTransactionMoney(BigDecimal.valueOf(-1));

    assertThatThrownBy(() -> service.begin(command))
        .hasMessage("Transaction money invalid")
        .isInstanceOf(InvalidParameterException.class);
  }

  @Test
  void should_throw_exception_when_transformer_account_is_blank() {
    command.setTransformerAccount(" ");

    assertThatThrownBy(() -> service.begin(command))
        .hasMessage("Transaction account invalid")
        .isInstanceOf(InvalidParameterException.class);
  }

  @Test
  void should_throw_exception_when_transformer_name_is_blank() {
    command.setTransformerAccount(null);

    assertThatThrownBy(() -> service.begin(command))
        .hasMessage("Transaction account invalid")
        .isInstanceOf(InvalidParameterException.class);
  }

  @Test
  void should_throw_exception_when_transformee_account_is_blank() {
    command.setTransformeeAccount("");

    assertThatThrownBy(() -> service.begin(command))
        .hasMessage("Transaction account invalid")
        .isInstanceOf(InvalidParameterException.class);
  }

  @Test
  void should_throw_exception_when_transformee_name_is_blank() {
    command.setTransformeeAccount(null);

    assertThatThrownBy(() -> service.begin(command))
        .hasMessage("Transaction account invalid")
        .isInstanceOf(InvalidParameterException.class);
  }

  @Test
  void should_throw_exception_when_envelope_id_is_blank_envelope_non_null() {
    command.setEnvelopeId("");

    assertThatThrownBy(() -> service.begin(command))
        .hasMessage("Transaction envelope invalid")
        .isInstanceOf(InvalidParameterException.class);
  }

  @Test
  void should_throw_exception_when_envelope_id_is_not_blank_envelope_is_null() {
    command.setEnvelopeMoney(null);

    assertThatThrownBy(() -> service.begin(command))
        .hasMessage("Transaction envelope invalid")
        .isInstanceOf(InvalidParameterException.class);
  }

  @Test
  void should_throw_exception_when_envelope_id_is_not_blank_envelope_is_smaller_than_zero() {
    command.setEnvelopeMoney(BigDecimal.valueOf(-1));

    assertThatThrownBy(() -> service.begin(command))
        .hasMessage("Transaction envelope invalid")
        .isInstanceOf(InvalidParameterException.class);
  }

  @Test
  void should_throw_exception_when_integral_id_is_blank_integral_non_null() {
    command.setIntegralId("");

    assertThatThrownBy(() -> service.begin(command))
        .hasMessage("Transaction integral invalid")
        .isInstanceOf(InvalidParameterException.class);
  }


  @Test
  void should_throw_exception_when_integral_id_is_not_blank_integral_is_null() {
    command.setIntegral(null);

    assertThatThrownBy(() -> service.begin(command))
        .hasMessage("Transaction integral invalid")
        .isInstanceOf(InvalidParameterException.class);
  }

  @Test
  void should_throw_exception_when_integral_id_is_not_blank_integral_is_smaller_than_zero() {
    command.setIntegral(-1);

    assertThatThrownBy(() -> service.begin(command))
        .hasMessage("Transaction integral invalid")
        .isInstanceOf(InvalidParameterException.class);
  }

  @Test
  void should_throw_exception_when_transaction_under_processing() {
    given(repository.existBy(command.getTransactionNumber())).willReturn(true);

    assertThatThrownBy(() -> service.begin(command))
        .hasMessage("Transaction is already started, please wait a minute...")
        .isInstanceOf(InvalidParameterException.class);
  }
}