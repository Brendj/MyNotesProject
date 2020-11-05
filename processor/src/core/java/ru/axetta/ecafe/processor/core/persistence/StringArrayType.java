/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 31.05.14
 * Time: 21:53
 * To change this template use File | Settings | File Templates.
 */
public class StringArrayType implements UserType {
    protected static final int  SQLTYPE = java.sql.Types.ARRAY;

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names, final SharedSessionContractImplementor sessionImplementor, final Object owner) throws HibernateException, SQLException {
        Array array = rs.getArray(names[0]);
        String[] javaArray = (String[]) array.getArray();
        return javaArray;
    }

    @Override
    public void nullSafeSet(final PreparedStatement statement, final Object object, final int i, final SharedSessionContractImplementor sessionImplementor) throws HibernateException, SQLException {
        Connection connection = statement.getConnection();

        String[] castObject = (String[]) object;
        Array array = connection.createArrayOf("string", castObject);

        statement.setArray(i, array);
    }

    @Override
    public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object deepCopy(final Object o) throws HibernateException {
        return o == null ? null : ((int[]) o).clone();
    }

    @Override
    public Serializable disassemble(final Object o) throws HibernateException {
        return (Serializable) o;
    }

    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        return x == null ? y == null : x.equals(y);
    }

    @Override
    public int hashCode(final Object o) throws HibernateException {
        return o == null ? 0 : o.hashCode();
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
        return original;
    }

    @Override
    public Class<int[]> returnedClass() {
        return int[].class;
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { SQLTYPE };
    }
}
