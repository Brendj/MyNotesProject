/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

-- Пакет обновлений v 284

ALTER TABLE cf_orgs
    ADD COLUMN usewebarmadmin boolean NOT NULL DEFAULT FALSE;
COMMENT ON COLUMN cf_orgs.usewebarmadmin
  IS 'Включено использование веб - модуля АРМа Администратора (true - да. false - нет)';

--! ФИНАЛИЗИРОВАН 14.02.2022, НЕ МЕНЯТЬ