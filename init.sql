CREATE
DATABASE springboot_demo;
CREATE
USER 'springboot_demo'@'%' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON springboot_demo.* TO
'springboot_demo'@'%';

CREATE TABLE SPRINGBOOT_DEMO.AUTHORITY
(
    AUTHORITY_ID       BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    CREATED_DATE       DATETIME(6)  NULL,
    LAST_MODIFIED_DATE DATETIME(6)  NULL,
    DESCRIPTION        VARCHAR(255) NULL,
    NAME               VARCHAR(255) NULL
);

CREATE TABLE SPRINGBOOT_DEMO.MEMBER
(
    MEMBER_ID          BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    CREATED_DATE       DATETIME(6)  NULL,
    LAST_MODIFIED_DATE DATETIME(6)  NULL,
    EMAIL              VARCHAR(255) NULL,
    ETC                VARCHAR(255) NULL,
    PASSWORD           VARCHAR(255) NULL
);

CREATE TABLE SPRINGBOOT_DEMO.MEMBER_AUTHORITY
(
    MEMBER_AUTHORITY_ID BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    CREATED_DATE        DATETIME(6) NULL,
    LAST_MODIFIED_DATE  DATETIME(6) NULL,
    AUTHORITY_ID        BIGINT NULL,
    MEMBER_ID           BIGINT NULL,
    CONSTRAINT FK8UF5FF5JR0NUVBJ4YFP5OB9SQ
        FOREIGN KEY (MEMBER_ID) REFERENCES SPRINGBOOT_DEMO.MEMBER (MEMBER_ID),
    CONSTRAINT FKDWWIEDQGXX71NTRY92WVGMMH1
        FOREIGN KEY (AUTHORITY_ID) REFERENCES SPRINGBOOT_DEMO.AUTHORITY (AUTHORITY_ID)
);



INSERT INTO
    SPRINGBOOT_DEMO.AUTHORITY (AUTHORITY_ID, CREATED_DATE, LAST_MODIFIED_DATE, DESCRIPTION, NAME)
VALUES
    (1, NULL, NULL, NULL, 'ROLE_USER');
