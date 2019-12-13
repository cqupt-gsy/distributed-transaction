package apprentice.practice.accounts.services;

import apprentice.practice.api.services.HelloWorld;
import org.apache.dubbo.config.annotation.Service;

@Service
public class DemoServiceImpl implements HelloWorld {

  @Override
  public String sayHello(String name) {
    return String.format("[%s] : Hello, %s", "accounts", name);
  }
}
