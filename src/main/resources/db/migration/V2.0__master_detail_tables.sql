DROP TABLE IF EXISTS master;
DROP TABLE IF EXISTS detail;

CREATE TABLE master (
    -- unique by primary key
    doc_number BIGINT PRIMARY KEY,
    creation_date TIMESTAMP NOT NULL,
    cost_value_sum BIGINT NOT NULL DEFAULT 0,
    description TEXT
);

CREATE TABLE detail (
    master_doc_number BIGINT,
    -- constraint for SQL exceptions in non unique clause
    detail_name VARCHAR(255) PRIMARY KEY,
    cost_value BIGINT NOT NULL,
    FOREIGN KEY (master_doc_number) REFERENCES master(doc_number)
                                  ON UPDATE CASCADE
        -- should specification exists without master document?
                                  ON DELETE CASCADE
);

CREATE TRIGGER detail_sum_trigger_update
    AFTER UPDATE
    ON detail
    FOR EACH ROW
    CALL "com.example.vniitesttask.triggers.ReCountDetailSumTrigger";

CREATE TRIGGER detail_sum_trigger_insert
    AFTER INSERT
    ON detail
    FOR EACH ROW
    CALL "com.example.vniitesttask.triggers.ReCountDetailSumTrigger";

CREATE TRIGGER detail_sum_trigger_delete
    AFTER DELETE
    ON detail
    FOR EACH ROW
    CALL "com.example.vniitesttask.triggers.ReCountDetailSumTrigger";
