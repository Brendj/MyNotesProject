/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.type;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

public class DateType implements UserType {
    private static final int[] SQL_TYPES = {Types.BIGINT};

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return Date.class;
    }

    public boolean equals(Object x, Object y) {
        if (x == y) {
            return true;
        }
        if (x == null || y == null) {
            return false;
        }
        return x.equals(y);
    }

    public int hashCode(Object x) throws HibernateException {
        if (x == null) {
            return 0;
        }
        return x.hashCode();
    }


    public Object deepCopy(Object value) {
        if (null == value) {
            return null;
        }
        return ((Date) value).clone();
    }

    public boolean isMutable() {
        return true;
    }

    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
            throws HibernateException, SQLException {
        long longValue = resultSet.getLong(names[0]);
        if (resultSet.wasNull()) {
            return null;
        }
        return new Date(longValue);
    }

    public void nullSafeSet(PreparedStatement statement, Object value, int index)
            throws HibernateException, SQLException {
        if (value == null) {
            statement.setNull(index, Types.BIGINT);
        } else {
            statement.setLong(index, ((Date) value).getTime());
        }
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] strings,
            SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws
            HibernateException,
            SQLException {
        return nullSafeGet(resultSet, strings, o);
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object o, int i,
            SharedSessionContractImplementor sharedSessionContractImplementor) throws
            HibernateException,
            SQLException {
        nullSafeSet(preparedStatement, o, i);
    }
}
