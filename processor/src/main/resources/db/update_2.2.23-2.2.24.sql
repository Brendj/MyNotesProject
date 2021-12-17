CREATE TABLE CF_ReportInfo (
  IdOfReportInfo BigSerial NOT NULL,
  RuleName varchar(64) NOT NULL,
  DocumentFormat integer,
  ReportName varchar(128) NOT NULL,
  CreatedDate bigint NOT NULL,
  GenerationTime integer NOT NULL,
  StartDate bigint NOT NULL,
  EndDate bigint NOT NULL,
  ReportFile varchar(256) NOT NULL,
  OrgNum varchar(12),
  IdOfOrg bigint,
  Tag varchar(12),
  CONSTRAINT cf_report_info_pk PRIMARY KEY (IdOfReportInfo)
);

CREATE index "cf_report_info_start_date_idx" ON CF_ReportInfo (StartDate);
CREATE index "cf_report_info_end_date_idx" ON CF_ReportInfo (EndDate);
CREATE index "cf_report_info_created_date_idx" ON CF_ReportInfo (CreatedDate);
CREATE index "cf_report_info_orgnum_date_idx" ON CF_ReportInfo (OrgNum);
CREATE index "cf_report_info_rulename_idx" ON CF_ReportInfo (RuleName);

ALTER TABLE CF_ReportHandleRules ADD COLUMN Tag varchar(256);

ALTER TABLE CF_Orgs ADD COLUMN Tag varchar(256);
ALTER TABLE CF_Orgs ADD COLUMN City varchar(128);
ALTER TABLE CF_Orgs ADD COLUMN District varchar(128);
ALTER TABLE CF_Orgs ADD COLUMN Location varchar(128);
ALTER TABLE CF_Orgs ADD COLUMN Latitude varchar(12);
ALTER TABLE CF_Orgs ADD COLUMN Longitude varchar(12);

--! ФИНАЛИЗИРОВАН (Кадыров, 120909) НЕ МЕНЯТЬ