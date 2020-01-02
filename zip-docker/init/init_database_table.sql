CREATE TABLE IF NOT EXISTS distributed_transactions.transactions
(
    id                 INTEGER        NOT NULL PRIMARY KEY AUTO_INCREMENT,
    transaction_number VARCHAR(36)    NOT NULL UNIQUE, -- 发起转账交易人负责该号的唯一性
    transaction_money  DECIMAL(15, 2) NOT NULL,        -- 13代表总的金额位，2代表精度
    transformer_id     INTEGER        NOT NULL,        -- 转账人ID
    transformee_id     INTEGER        NOT NULL,        -- 被转账人ID
    transaction_time   DATETIME       NOT NULL,
    envelope_id        VARCHAR(36)   DEFAULT NULL,
    envelope_money     DECIMAL(6, 2) DEFAULT NULL,
    integral_id        VARCHAR(36)   DEFAULT NULL,
    integral           INTEGER       DEFAULT 0,
    create_at          DATETIME       NOT NULL,
    update_at          DATETIME       NOT NULL
);

CREATE TABLE IF NOT EXISTS user_account_1.account
(
    id           INTEGER                         NOT NULL PRIMARY KEY AUTO_INCREMENT,
    phone_number VARCHAR(11)                     NOT NULL UNIQUE, -- 转账人账号-手机号
    name         VARCHAR(100) CHARACTER SET utf8 NOT NULL,        -- 转账人姓名
    balance      DECIMAL(15, 2)                  NOT NULL,        -- 13代表总的金额位，2代表精度
    create_at    DATETIME                        NOT NULL,
    update_at    DATETIME                        NOT NULL
);

CREATE TABLE IF NOT EXISTS user_account_2.account
(
    id           INTEGER                         NOT NULL PRIMARY KEY AUTO_INCREMENT,
    phone_number VARCHAR(11)                     NOT NULL UNIQUE, -- 转账人账号-手机号
    name         VARCHAR(100) CHARACTER SET utf8 NOT NULL,        -- 转账人姓名
    balance      DECIMAL(15, 2)                  NOT NULL,        -- 13代表总的金额位，2代表精度
    create_at    DATETIME                        NOT NULL,
    update_at    DATETIME                        NOT NULL
);

CREATE TABLE IF NOT EXISTS user_account_1.account_back_up
(
    id                 INTEGER        NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id            INTEGER        NOT NULL,
    transaction_number VARCHAR(36)    NOT NULL UNIQUE,
    original_balance   DECIMAL(15, 2) NOT NULL,
    new_balance        DECIMAL(15, 2) NOT NULL,
    transaction_money  DECIMAL(15, 2) NOT NULL,
    create_at          DATETIME       NOT NULL,
    update_at          DATETIME       NOT NULL
);

CREATE TABLE IF NOT EXISTS user_account_1.distributed_lock
(
    id                 INTEGER     NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id            INTEGER     NOT NULL UNIQUE,
    transaction_number VARCHAR(36) NOT NULL UNIQUE,
    lock_until         DATETIME    NOT NULL,
    create_at          DATETIME    NOT NULL,
    update_at          DATETIME    NOT NULL
);


CREATE TABLE IF NOT EXISTS user_account_2.account_back_up
(
    id                 INTEGER        NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id            INTEGER        NOT NULL,
    transaction_number VARCHAR(36)    NOT NULL UNIQUE,
    original_balance   DECIMAL(15, 2) NOT NULL,
    new_balance        DECIMAL(15, 2) NOT NULL,
    transaction_money  DECIMAL(15, 2) NOT NULL,
    create_at          DATETIME       NOT NULL,
    update_at          DATETIME       NOT NULL
);

CREATE TABLE IF NOT EXISTS user_account_2.distributed_lock
(
    id                 INTEGER     NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id            INTEGER     NOT NULL UNIQUE,
    transaction_number VARCHAR(36) NOT NULL UNIQUE,
    lock_until         DATETIME    NOT NULL,
    create_at          DATETIME    NOT NULL,
    update_at          DATETIME    NOT NULL
);
