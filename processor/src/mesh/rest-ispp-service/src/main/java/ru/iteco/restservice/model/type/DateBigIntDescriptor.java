package ru.iteco.restservice.model.type;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

import java.util.Date;

/**
 * Created by nuc on 05.05.2021.
 */
public class DateBigIntDescriptor extends AbstractTypeDescriptor<Date> {
    public static final DateBigIntDescriptor INSTANCE = new DateBigIntDescriptor();

    public DateBigIntDescriptor() {
        super(Date.class, ImmutableMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(Date date) {
        return null;
    }

    @Override
    public Date fromString(String s) {
        return null;
    }

    @Override
    public <X> X unwrap(Date date, Class<X> aClass, WrapperOptions wrapperOptions) {
        if (date == null)
            return null;

        if (Long.class.isAssignableFrom(aClass)) {
            return (X) new Long(date.getTime());
        }

        throw unknownUnwrap(aClass);
    }

    @Override
    public <X> Date wrap(X x, WrapperOptions wrapperOptions) {
        if (x == null) {
            return null;
        }
        if (x instanceof Long) {
            Long value = (Long) x;
            return new Date(value);
        }
        throw unknownWrap(x.getClass());
    }
}
