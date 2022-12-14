DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS ITEMS CASCADE;
DROP TABLE IF EXISTS BOOKINGS CASCADE;
DROP TABLE IF EXISTS REQUESTS CASCADE;
DROP TABLE IF EXISTS COMMENTS CASCADE;


CREATE TABLE IF NOT EXISTS USERS
(
    USER_ID   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    USER_NAME VARCHAR(255)                            NOT NULL,
    EMAIL     VARCHAR(512)                            NOT NULL,
    CONSTRAINT PK_USER PRIMARY KEY (USER_ID),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (EMAIL)
);
CREATE TABLE IF NOT EXISTS REQUESTS
(
    REQUEST_ID   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    REQUEST_DESCRIPTION  VARCHAR(500),
    REQUESTOR_ID BIGINT,
    REQUEST_CREATEDATE  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT PK_REQUEST PRIMARY KEY (REQUEST_ID),
    FOREIGN KEY (REQUESTOR_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ITEMS
(
    ITEM_ID      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    ITEM_NAME    VARCHAR(255)                            NOT NULL,
    ITEM_DESCRIPTION  VARCHAR(500),
    IS_AVAILABLE BOOLEAN NOT NULL,
    OWNER_ID     BIGINT NOT NULL,
    REQUEST_ID   BIGINT,
    CONSTRAINT PK_ITEM PRIMARY KEY (ITEM_ID),
    FOREIGN KEY (OWNER_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    FOREIGN KEY (REQUEST_ID) REFERENCES REQUESTS (REQUEST_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS BOOKINGS
(
    BOOKING_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    START_DATE TIMESTAMP WITHOUT TIME ZONE,
    END_DATE   TIMESTAMP WITHOUT TIME ZONE,
    ITEM_ID    BIGINT ,
    BOOKER_ID  BIGINT,
    STATUS  VARCHAR(10),
    CONSTRAINT PK_BOOKING PRIMARY KEY (BOOKING_ID),
    FOREIGN KEY (ITEM_ID) REFERENCES ITEMS (ITEM_ID) ON DELETE CASCADE,
    FOREIGN KEY (BOOKER_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS COMMENTS
(
    COMMENT_ID   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    COMMENT_TEXT VARCHAR(500),
    ITEM_ID      BIGINT NOT NULL,
    AUTHOR_ID    BIGINT NOT NULL,
    CONSTRAINT PK_COMMENT PRIMARY KEY (COMMENT_ID),
    FOREIGN KEY (AUTHOR_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    FOREIGN KEY (ITEM_ID) REFERENCES ITEMS (ITEM_ID) ON DELETE CASCADE
);

