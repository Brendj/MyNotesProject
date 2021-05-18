/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

-- Пакет обновлений issue 1032

select nextval('cf_clientpayments_id_gen_seq');
alter sequence cf_clientpayments_id_gen_seq increment by 1;