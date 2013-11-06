/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.test.synch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.LogManager;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 12.07.13
 * Time: 11:43
 * To change this template use File | Settings | File Templates.
 */
public class TestConfigUtil {

    //protected static final Logger LOGGER = LoggerFactory.getLogger(TestConfigUtil.class);

    @SuppressWarnings("rawtypes")
    public static void runSQL(Class clazz, String schema, String resourceName, EntityManager entityManager) throws Exception {
//        if (!entityManager.getTransaction().isActive()) {
//            entityManager.getTransaction().begin();
//        }
        if (schema != null && schema.trim().length() > 0) {
            schema = schema.trim();
            entityManager.createNativeQuery("CREATE SCHEMA " + schema + " AUTHORIZATION sa").executeUpdate();
            entityManager.createNativeQuery("SET SCHEMA " + schema).executeUpdate();
        }
        InputStream resStream = clazz.getResourceAsStream(resourceName);
        if(resStream == null){
            System.out.println("Illegal argument: resStream is null.");
            return;
            //LOGGER.error("Illegal argument: resStream is null.");
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(resStream, "UTF-8"));
        String statement;
        String sql = "";
        while ((statement = br.readLine()) != null) {
            statement = statement.toLowerCase().replaceAll("with time zone", "");
            if (!statement.startsWith("--") && !statement.trim().isEmpty()) {
                sql = sql + (!sql.isEmpty() ? "\n" : "") + statement;

                if (sql.trim().endsWith(";") || sql.trim().endsWith("/")) {
                    entityManager.createNativeQuery(sql).executeUpdate();
                    sql = "";
                }
            }
        }
        br.close();
        resStream.close();
 //       entityManager.getTransaction().commit();
    }


    public static void loadLoggingProperties(Class clazz, String resourceName) {
        final InputStream inputStream = clazz.getResourceAsStream(resourceName);
        try {
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void setStaticStringField(Class clazz, String fieldName, String newValue ) throws NoSuchFieldException, IllegalAccessException {
        Field oidGeneratorField =  clazz.getDeclaredField(fieldName);
        oidGeneratorField.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(oidGeneratorField, oidGeneratorField.getModifiers() & ~Modifier.FINAL);
        oidGeneratorField.set(null, newValue);
    }



    public static void setEsiaHomeTestPath(Class clazz){
        System.setProperty(TestConstant.HOME, clazz.getResource("/").getPath()  + TestConstant.HOME_PATH);
    }

}
