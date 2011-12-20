-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Update from protoVersion 0.0.4 to protoVersion 0.0.5

CONNECT 'jdbc:derby:ecafe_processor_db';

CREATE TABLE CF_ReportHandleRules (
  IdOfReportHandleRule    BIGINT          NOT NULL,
  RuleName                VARCHAR(64)     NOT NULL,
  DocumentFormat          INTEGER         NOT NULL,
  Subject                 VARCHAR(128)    NOT NULL,
  Route0                  VARCHAR(128)    NOT NULL,
  Route1                  VARCHAR(128),
  Route2                  VARCHAR(128),
  Route3                  VARCHAR(128),
  Route4                  VARCHAR(128),
  Route5                  VARCHAR(128),
  Route6                  VARCHAR(128),
  Route7                  VARCHAR(128),
  Route8                  VARCHAR(128),
  Route9                  VARCHAR(128),
  Remarks                 VARCHAR(1024),
  Enabled                 INTEGER         NOT NULL,        
  CONSTRAINT CF_ReportHandleRules_pk PRIMARY KEY (IdOfReportHandleRule)
);

CREATE TABLE CF_RuleConditions (
  IdOfRuleCondition       BIGINT          NOT NULL,
  IdOfReportHandleRule    BIGINT          NOT NULL,
  ConditionOperation      INTEGER         NOT NULL,
  ConditionArgument       VARCHAR(128),
  ConditionConstant       VARCHAR(128),
  CONSTRAINT CF_RuleConditions_pk PRIMARY KEY (IdOfRuleCondition),
  CONSTRAINT CF_RuleConditions_IdOfReportHandleRule_fk FOREIGN KEY (IdOfReportHandleRule) REFERENCES CF_ReportHandleRules (IdOfReportHandleRule)
);

ALTER TABLE CF_Generators ADD IdOfReportHandleRule BIGINT NOT NULL DEFAULT 0;
ALTER TABLE CF_Generators ADD IdOfRuleCondition BIGINT NOT NULL DEFAULT 0; 

