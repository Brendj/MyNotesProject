package ru.iteco.restservice.model.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.BigIntTypeDescriptor;

import java.util.Date;

/**
 * Created by nuc on 05.05.2021.
 */
public class DateBigIntType extends AbstractSingleColumnStandardBasicType<Date> {
    public DateBigIntType() {
        super(BigIntTypeDescriptor.INSTANCE, DateBigIntDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "DateBigInt";
    }
}
