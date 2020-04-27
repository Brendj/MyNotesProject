/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by nuc on 03.04.2020.
 */
public class ClientGuardianRelationSQLType implements UserType {
    private static final int[] SQL_TYPES = {Types.INTEGER};

    @Override
    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    @Override
    public Class returnedClass() {
        return ClientGuardianRelationType.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y || !(x == null || y == null) && x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        if (x == null) {
            return 0;
        }
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor var3, Object owner) throws HibernateException,
            SQLException {
        String value = resultSet.getString(names[0]);
        if (resultSet.wasNull()) {
            return null;
        }
        return ClientGuardianRelationType.fromInteger(Integer.parseInt(value));
    }

    @Override
    public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor var4) throws HibernateException, SQLException {
        if (value == null) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, ((ClientGuardianRelationType)value).getCode());
        }
    }

    @Override
    public Object deepCopy(Object var1) throws HibernateException {
        return var1;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object var1) throws HibernateException {
        return (Serializable) var1;
    }

    @Override
    public Object assemble(Serializable var1, Object var2) throws HibernateException {
        return var1;
    }

    @Override
    public Object replace(Object var1, Object var2, Object var3) throws HibernateException {
        return deepCopy(var1);
    }

}
