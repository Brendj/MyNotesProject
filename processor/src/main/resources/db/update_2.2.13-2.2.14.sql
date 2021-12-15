ALTER TABLE cf_users ADD COLUMN email character varying(128);
INSERT INTO cf_options(idofoption, optiontext)VALUES (10,'');