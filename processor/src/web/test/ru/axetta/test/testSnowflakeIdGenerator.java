/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.test;

import junit.framework.TestCase;

import ru.axetta.ecafe.processor.core.utils.idGenerator.IIdGenerator;
import ru.axetta.ecafe.processor.core.utils.idGenerator.OrganizationUniqueGeneratorId;

public class testSnowflakeIdGenerator extends TestCase {

    public void testCreateId(){
        IIdGenerator<Long> orderIdGenerator = OrganizationUniqueGeneratorId.getInstance(5L);
        Long id = orderIdGenerator.createId();
    }

}
