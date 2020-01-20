## 分布式事务实战

### 命令行参数参考

* 单独运行一个module: ```./mvnw spring-boot:run -pl {module name}```，可以参考https://stackoverflow.com/questions/1114026/maven-modules-building-a-single-specific-module
* 配置mycat容器步骤，关于mycat的配置详情可以参考https://github.com/MyCATApache/Mycat-Server，https://github.com/dekuan/docker.mycat， https://www.jianshu.com/p/f81422b1c915
    * 首先进入 ```./zip-docker/mycat```，允许命令 ```docker-compose build```，在本地建立image
    * 其次运行 ```docker image ls``` 找到对应的mycat docker image
    * 最后在 ```./zip-docker/docker-compose.yml``` 中配置mycat service
    * ```docker-compose up mysql, docker-compose up -d```启动时请注意，必须先启动mysql服务，并且创建对应的数据库和表，然后在启动mycat，否则mycat会启动失败

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

#### MVP1-TCC实战

* Tasking：

> 完成交易中心，能够正常记录交易信息，处理交易信息
> 完成转账转出服务，转账转入服务，能支持分片数据库处理
> 总结TCC模式的实践

* 架构设计总结：（详情参考如下时序图），本示例采取资金安全更高的架构

> TM，TO，TI集群模式部署，每台机器都可以处理交易
> TM允许TRY状态的记录重试
> CANCEL和CONFIRM节点采取同步处理，只有当这两个流程处理结束后才可以返回
> TO，TI所有操作必须幂等、可重入锁

![tcc-flow](./tcc-flow.png)

```
前置条件：
1. 必须提交交易记录才能发送消息4和9
2. 4和9是异步消息，相互不干扰
3. 16和20是异步消息，相互不干扰
4. 25和29是异步消息，互相不干扰
5. TM，TO，TI都采用集群部署

异常情况以及影响汇总：
TM崩溃的情况
1. 提交交易记录后，系统崩溃，消息4和9都没有发送
2. 提交交易记录后，发送消息4，系统崩溃，消息9没有发送
3. 提交交易记录后，发送消息4和9，系统崩溃
影响：交易记录已产生TRY状态；用户数据没发生改变；客户端收到超时的结果
解决方案：
* 如果不允许TRY状态重试，产生一条TRY状态脏数据；TO用户数据出现不一致需要恢复、锁被占用导致其他操作无法进行；TI用户数据出现不一致需要恢复、锁被占用导致其他操作无法进行
* 如果允许TRY状态重试，TO需要保持幂等、需要允许重入锁；TI需要保持幂等、需要允许重入锁

TO或TI崩溃的情况
4. 提交交易记录后，消息4返回服务不可达，消息9返回服务不可达
5. 提交交易记录后，消息4返回服务不可达，消息9返回超时
6. 提交交易记录后，消息4返回服务不可达，消息9返回正常
7. 提交交易记录后，消息4返回超时，消息9返回服务不可达
8. 提交交易记录后，消息4返回超时，消息9返回超时
9. 提交交易记录后，消息4返回超时，消息9返回正常
10. 提交交易记录后，消息4返回正常，消息9返回服务不可达
11. 提交交易记录后，消息4返回正常，消息9返回超时
影响：交易记录已产生TRY状态；TO服务数据与TI服务数据记录不一致，某一方会多一条CANCEL记录
解决方法：
* 如果TM不发起主动重试，交易回到CANCLE状态；客户端收到失败结果重新开始新的转账
* 如果TM发起主动重试，交易有可能回到SUCCESS状态，TO和TI必须保证锁可重入且是幂等的（在集群模式下，基本重试1-2次就会恢复正常）

TRY流程返回后TM崩溃，异步CONFIRM或者异步CANCEL状态未知
影响：TM，TO，TI的数据停留在TRY状态，客户端已经收到成功或者失败的结果，但TO和TI可能还维持着锁状态
解决方法：
* 如果不允许自释放锁，用户一直被锁住，其他交易会一直超时，直到TM恢复，并重试TRY流程，这种方案对并发不友好，一旦发生主从切换，热点账户会在一定时间内无法转账，但是资金是绝对安全的
* 如果允许自释放锁，锁时间到期，用户就可以开始其他交易，主从切换时，无需CronJob处理TRY流程，只需记录不一致状态，并转人工处理即可，这种方案对并发较友好，但是存在资金风险，尤其是CANCEL的时候，一旦有其他新的交易完成，CANCEL流程就无法自动恢复，需要转人工

TRY流程结束后，同步CONFIRM或者CANCEL阶段处理，所有阶段处理完成才返回，所以一旦中途挂了，客户端收到的还是超时重试，重试到另外一台机器就会恢复正常
影响：转账周期变长，资金绝对安全，无需自释放锁，无需CronJob等自我恢复机制
在同步CONFRIM时，只有当TO和TI都完成了CONFIRM流程才最后返回，由于是集群部署，所以即使有一台失败了，重试一次就会恢复正常，但是重试带来的代价就是交易时间变长
在同步CANCEL时，只有当TO和TI都完成了CONFIRM流程才最后返回，由于是集群部署，所以即使有一台失败了，重试一次就会恢复正常，但是重试带来的代价就是交易时间变长

总结：
如果要求性能更高的架构，可以采用如下规则：（风险，可能存在部分资金无法执行UNDO，其他交易发生在CANCEL流程执行之前，需要转人工）
1. TM，TO，TI主从结构模式部署
2. TM需要CronJob，定时去处理停留在TRY状态的交易记录
3. TM允许TRY状态的记录重试
4. CANCEL和CONFIRM阶段采用异步处理
5. TO，TI所有操作必须幂等、可重入锁、锁自释放
如果要求资金更高的架构，可以采用如下规则：（每笔交易的返回周期变长，本质处理时间无变化）
1. TM，TO，TI集群模式部署，每台机器都可以处理交易
2. TM允许TRY状态的记录重试
3. CANCEL和CONFIRM节点采取同步处理，只有当这两个流程处理结束后才可以返回
4. TO，TI所有操作必须幂等、可重入锁
```

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
