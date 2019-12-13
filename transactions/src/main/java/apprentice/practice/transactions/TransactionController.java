package apprentice.practice.transactions;

import apprentice.practice.api.services.HelloWorld;
import apprentice.practice.transactions.command.TransactionCommand;
import apprentice.practice.transactions.model.Transaction;
import apprentice.practice.transactions.services.TransactionService;
import java.util.List;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/transaction")
public class TransactionController {

  @Reference(version = "${demo.service.version}")
  private HelloWorld helloWorld;

  private final TransactionService transactionService;

  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @GetMapping
  public List<Transaction> findAllTransactions() {
    return transactionService.findAll();
  }

  @PostMapping("/create")
  public void begin(@RequestBody TransactionCommand transactionCommand) {
    transactionService.begin(transactionCommand);
  }

  @GetMapping("/hello")
  public String hello() {
    return helloWorld.sayHello("transactions");
  }
}
