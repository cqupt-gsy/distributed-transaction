## 分布式事务实战

### 命令行参数参考

* 单独运行一个module: ```./mvnw spring-boot:run -pl {module name}```，可以参考https://stackoverflow.com/questions/1114026/maven-modules-building-a-single-specific-module

### 实战细节设计

#### 业务范围

* 支付功能,支持多种形式的组合付款
* 交易记录功能,支付之前,必须先生成交易记录

#### 服务划分

* 交易服务，生成交易信息，事务发起方，依赖转账服务、红包服务、积分服务（交易记录数据库）
* 转账服务，对用户账户扣款或加钱（用户账户数据库x2）
* 红包服务，扣除用户红包（红包数据库）
* 积分服务，扣除积分（积分数据库）

#### 性能测试、压力测试: 待定

#### 技术栈: dubbo，etcd，mycat，mysql，springboot，rocketmq、kafka，fascar，docker

-----------

### MVPs

#### MVP1

* 完成交易服务,能够发起转账
* 完成支付服务,能够正常的在两个数据库之间转账
* 总结同步调用之间的事务处理问题

#### MVP2

* 集成MQ,服务间通过异步调用
* 添加红包服务
* 添加积分服务
* 性能测试、压力测试
* 总结异步调用之间的事务处理问题

#### MVP3

* 集成fascar,其余与MVP2一样
* 总结fascar的优缺点

#### MVP4

* 设计并实现一个分布式事务框架(optional),并且与之集成
