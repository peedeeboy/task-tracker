-- Create initial database schema
CREATE TABLE IF NOT EXISTS tasks (
    id          BIGINT                      AUTO_INCREMENT  PRIMARY KEY,
    title       VARCHAR(255)                NOT NULL,
    description CLOB,
    status      VARCHAR(50)                 NOT NULL,
    due_date     TIMESTAMP WITH TIME ZONE    NOT NULL
);