package apprentice.practice.transactions;

import apprentice.practice.transactions.command.TransactionCommand;
import apprentice.practice.transactions.model.Transaction;
import apprentice.practice.transactions.services.TransactionManagerService;
import apprentice.practice.transactions.services.TransactionService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/transactions")
public class TransactionController {

  private final TransactionService transactionService;
  private final TransactionManagerService transactionManagerService;

  public TransactionController(
      TransactionService transactionService, TransactionManagerService transactionManagerService) {
    this.transactionService = transactionService;
    this.transactionManagerService = transactionManagerService;
  }

  @GetMapping
  public List<Transaction> findAllTransactions() {
    return transactionService.findAll();
  }

  @PostMapping("/create")
  public String create(@RequestBody TransactionCommand transactionCommand) {
    return transactionManagerService.execute(transactionCommand);
  }
}
