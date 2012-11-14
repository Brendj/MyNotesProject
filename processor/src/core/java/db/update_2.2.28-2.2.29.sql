CREATE TABLE IF NOT EXISTS cf_projectstate_data
  (
  Period bigint NOT NULL,
  Type int NOT NULL,
  StringKey character varying(128),
  StringValue character varying(128),
  generationdate bigint NOT NULL,

  CONSTRAINT cf_complexinfo_discountdetail_pk PRIMARY KEY (Period, Type, StringKey)
  );