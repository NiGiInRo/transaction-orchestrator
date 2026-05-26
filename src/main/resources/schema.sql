-- ===========================================================
-- Transaction Orchestrator — Esquema de Base de Datos
-- Compatible con: H2 (dev) / PostgreSQL (prod)
-- ===========================================================

-- Catálogo de métodos de pago disponibles (PSE, tarjeta, etc.)
-- La PK es un código legible (no UUID) para facilitar referencias
CREATE TABLE IF NOT EXISTS payment_methods (
    id      VARCHAR(50)  NOT NULL,
    name    VARCHAR(100) NOT NULL,
    enabled BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_payment_methods PRIMARY KEY (id)
);

-- Datos del pagador. Se almacena independiente para evitar
-- duplicar información cuando un mismo cliente realiza varias transacciones
CREATE TABLE IF NOT EXISTS customers (
    id                UUID         NOT NULL,
    document_type     VARCHAR(20)  NOT NULL,
    document_number   VARCHAR(30)  NOT NULL,
    country_code      VARCHAR(10)  NOT NULL,
    phone             VARCHAR(20)  NOT NULL,
    email             VARCHAR(150) NOT NULL,
    first_name        VARCHAR(80)  NOT NULL,
    middle_name       VARCHAR(80),
    last_name         VARCHAR(80)  NOT NULL,
    second_last_name  VARCHAR(80),
    CONSTRAINT pk_customers PRIMARY KEY (id)
);

-- Registro de cada intento de pago orquestado por el microservicio.
-- amount se almacena en centavos (BIGINT) para evitar errores de punto flotante
CREATE TABLE IF NOT EXISTS transactions (
    id                    UUID         NOT NULL,
    client_transaction_id VARCHAR(100) NOT NULL,
    amount                BIGINT       NOT NULL,
    currency              VARCHAR(3)   NOT NULL,
    country               VARCHAR(2)   NOT NULL,
    payment_method_id     VARCHAR(50)  NOT NULL,
    customer_id           UUID         NOT NULL,
    webhook_url           VARCHAR(500) NOT NULL,
    redirect_url          VARCHAR(500) NOT NULL,
    description           VARCHAR(255),
    expiration_time       TIMESTAMP,
    status                VARCHAR(20)  NOT NULL,
    processed_at          TIMESTAMP    NOT NULL,
    CONSTRAINT pk_transactions         PRIMARY KEY (id),
    CONSTRAINT fk_transactions_customer FOREIGN KEY (customer_id)       REFERENCES customers(id),
    CONSTRAINT fk_transactions_payment  FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id)
);

-- ===========================================================
-- Datos semilla: catálogo inicial de métodos de pago
-- ===========================================================
INSERT INTO payment_methods (id, name, enabled) VALUES
    ('PSE',         'PSE — Pagos Seguros en Línea', TRUE),
    ('CREDIT_CARD', 'Tarjeta de Crédito',           TRUE),
    ('DEBIT_CARD',  'Tarjeta Débito',               TRUE),
    ('CASH',        'Efectivo (Corresponsal)',       FALSE);
