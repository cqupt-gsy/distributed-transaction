CREATE TABLE IF NOT EXISTS distributed_transactions.transactions
(
    id                  INTEGER                               NOT NULL PRIMARY KEY AUTO_INCREMENT,
    transaction_number  VARCHAR(36)                           NOT NULL UNIQUE, -- 发起转账交易人负责该号的唯一性
    transaction_money   DECIMAL(15, 2)                        NOT NULL,        -- 13代表总的金额位，2代表精度
    transformer_account VARCHAR(11)                           NOT NULL,        -- 转账人账号-手机号
    transformer_name    VARCHAR(100) CHARACTER SET utf8       NOT NULL,        -- 转账人姓名
    transformee_account VARCHAR(11)                           NOT NULL,        -- 被转账人账号-手机号
    transformee_name    VARCHAR(100) CHARACTER SET utf8       NOT NULL,        -- 被转账人姓名
    status              ENUM ('CREATED', 'SUCCESS', 'FAILED') NOT NULL DEFAULT 'CREATED',
    transaction_time    DATETIME                              NOT NULL,
    envelope_id         VARCHAR(36)                                    DEFAULT NULL,
    envelope_money      DECIMAL(6, 2)                                  DEFAULT NULL,
    integral_id         VARCHAR(36)                                    DEFAULT NULL,
    integral            INTEGER                                        DEFAULT 0,
    create_at           DATETIME                              NOT NULL,
    update_at           DATETIME                              NOT NULL
);