DROP SEQUENCE IF EXISTS HIP_CTY_TASKS_SEQ;
CREATE SEQUENCE HIP_CTY_TASKS_SEQ start 1 increment 1;

DROP TABLE IF EXISTS HIP_CTY_TASKS;
CREATE TABLE HIP_CTY_TASKS
(
    id                          BIGINT        PRIMARY KEY DEFAULT NEXTVAL('HIP_CTY_TASKS_SEQ'),
    name                        VARCHAR(60)   NOT NULL,
    description                 VARCHAR(255),
    status                      VARCHAR(3)    NOT NULL CHECK (status IN ('ACT', 'INA')),
    job_id                      VARCHAR(30)   NOT NULL,
    device_id                   VARCHAR(30)   NOT NULL,
    device_operation            VARCHAR(10)   NOT NULL CHECK (device_operation IN ('ACTIVATE', 'DEACTIVATE')),
    device_execution_command    VARCHAR(90),
    task_hour                   SMALLINT      NOT NULL CHECK (task_hour >= 0 AND task_hour <= 23),
    task_minute                 SMALLINT      NOT NULL CHECK (task_minute >= 0 AND task_minute <= 59),
    execution_days              VARCHAR(30)   NOT NULL,
    execute_until               TIMESTAMP     NOT NULL CHECK (execute_until > CURRENT_TIMESTAMP),
    created_at                  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMIT;
