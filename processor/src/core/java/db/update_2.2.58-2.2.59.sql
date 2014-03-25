-- Добавлена возможность описания ошибки для изменений в реестре
ALTER TABLE CF_RegistryChange ADD COLUMN Error character varying(256) DEFAULT null;