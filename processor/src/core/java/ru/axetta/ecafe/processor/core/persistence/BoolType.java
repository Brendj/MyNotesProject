/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.EnhancedUserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 05.08.2009
 * Time: 14:31:41
 * To change this template use File | Settings | File Templates.
 */
public class BoolType implements EnhancedUserType {

    private static final int[] SQL_TYPES = {Types.INTEGER};

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return Boolean.class;
    }

    public boolean equals(Object x, Object y) {
        return x == y || !(x == null || y == null) && x.equals(y);
    }

    public int hashCode(Object x) throws HibernateException {
        if (x == null) {
            return 0;
        }
        return x.hashCode();
    }

    public Object deepCopy(Object value) {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
            throws HibernateException, SQLException {
        int intValue = resultSet.getInt(names[0]);
        if (resultSet.wasNull()) {
            return null;
        }
        return intValue != 0;
    }

    public void nullSafeSet(PreparedStatement statement, Object value, int index)
            throws HibernateException, SQLException {
        if (value == null) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, ((Boolean) value) ? 1 : 0);
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

    public void nullSafeSet(PreparedStatement preparedStatement, Object o, int i, SharedSessionContractImplementor sessionImplementor)
            throws HibernateException, SQLException {
        nullSafeSet(preparedStatement, o, i);
    }

    public Object nullSafeGet(ResultSet resultSet, String[] strings, SharedSessionContractImplementor sessionImplementor, Object o)
            throws HibernateException, SQLException {
        return nullSafeGet(resultSet, strings, o);
    }

    @Override
    public String objectToSQLString(Object o) {
        return toXMLString(o);
    }

    @Override
    public String toXMLString(Object o) {
        return ((Boolean) o) ? "1" : "0";
    }

    @Override
    public Object fromXMLString(String s) {
        return s.equals("1") ? Boolean.TRUE : Boolean.FALSE;
    }
}