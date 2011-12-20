-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Update from protoVersion 0.1.7 to protoVersion 0.1.8

CONNECT 'jdbc:derby:ecafe_processor_db';

CREATE TABLE CF_SchedulerJobs (
  IdOfSchedulerJob        BIGINT          NOT NULL,
  JobClass                VARCHAR(512)    NOT NULL,
  CronExpression          VARCHAR(128)    NOT NULL,
  JobName                 VARCHAR(128)    NOT NULL,
  Enabled                 INTEGER         NOT NULL,
  CONSTRAINT CF_SchedulerJobs_pk PRIMARY KEY (IdOfSchedulerJob)
);

ALTER TABLE CF_Generators ADD IdOfSchedulerJob BIGINT NOT NULL DEFAULT 0;