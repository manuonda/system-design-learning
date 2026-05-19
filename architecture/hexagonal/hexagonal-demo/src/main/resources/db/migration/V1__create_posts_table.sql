CREATE TABLE posts (
    id      VARCHAR(36)  NOT NULL,
    title   VARCHAR(255) NOT NULL,
    content TEXT         NOT NULL,
    CONSTRAINT pk_posts PRIMARY KEY (id)
);
