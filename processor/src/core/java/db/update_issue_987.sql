/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

ALTER TABLE cf_prohibitions
    ADD COLUMN idofdish BIGINT REFERENCES cf_wt_dishes(idofdish) ON DELETE SET NULL,
    ADD COLUMN idofcategory BIGINT REFERENCES cf_wt_categories(idofcategory) ON DELETE SET NULL,
    ADD COLUMN idofcategoryitem BIGINT REFERENCES cf_wt_category_items(idofcategoryitem) ON DELETE SET NULL,
    ALTER COLUMN filtertext DROP NOT NULL,
    ALTER COLUMN prohibitionfiltertype DROP NOT NULL;

COMMENT ON COLUMN cf_prohibitions.idofdish IS 'Идентификатор запрещенного блюда (ссылка на cf_wt_dishes)';
COMMENT ON COLUMN cf_prohibitions.idofcategory IS 'Идентификатор запрещенной категории (ссылка на cf_wt_categories)';
COMMENT ON COLUMN cf_prohibitions.idofcategoryitem IS 'Идентификатор запрещенной подкатегории (ссылка на cf_wt_category_items)';