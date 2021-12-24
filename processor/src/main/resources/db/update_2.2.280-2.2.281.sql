/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

-- Пакет обновлений v 281

ALTER TABLE cf_preorder_complex
    ADD COLUMN externalSystem INTEGER;

--! ФИНАЛИЗИРОВАН 24.12.2021, НЕ МЕНЯТЬ