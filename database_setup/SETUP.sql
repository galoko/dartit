CREATE TABLE PRODUCTS
(
   ID               INTEGER       GENERATED ALWAYS AS IDENTITY,
   NAME             VARCHAR2(128) NOT NULL,
   AMOUNT           INTEGER       NOT NULL,
   PRICE            NUMBER(38, 2) NOT NULL,
   CONSTRAINT ID_PK PRIMARY KEY (ID),
   CONSTRAINT AMOUNT_POSITIVE check (AMOUNT >= 0)
);

CREATE TABLE USERS
(
   LOGIN            VARCHAR2(32)  NOT NULL,
   PASSWORD_HASH    VARCHAR2(256) NOT NULL,
   SALT             VARCHAR2(128) NOT NULL,
   PRIVELEGES       INTEGER       NOT NULL,
   CONSTRAINT LOGIN_PK PRIMARY KEY (LOGIN),
   CONSTRAINT LOGIN_LENGTH check (length(LOGIN) >= 4)
);

CREATE TABLE HISTORY
(
   PRODUCT_ID       INTEGER       NOT NULL,
   USER_LOGIN       VARCHAR2(128) NOT NULL,
   AMOUNT           INTEGER       NOT NULL,
   EVENT_DATE       DATE          NOT NULL
);