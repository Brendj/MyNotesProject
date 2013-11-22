/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.axetta.ecafe.processor.core.updater;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SchemaVersionInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Component
public class DBUpdater {
    private final static Logger logger = LoggerFactory.getLogger(DBUpdater.class);
    private final static String UPDATES_ROOT="/db";

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    @Autowired(required = false)
    private RuntimeContext runtimeContext;


    private int[] INITIAL_DB_VERSION;
    private Properties updatesMap = new Properties();

    void lookupDBVersions() throws IOException {
        updatesMap.load(DBUpdater.class.getResourceAsStream(UPDATES_ROOT+ "/update.properties"));
        INITIAL_DB_VERSION = parseVersion(updatesMap.getProperty("initial"));
    }

    private int[] parseVersion(String v) {
        String[] subVersions=v.split("[.]");
        int newMajor, newMiddle, newMinor, newBuild;
        newMajor = Integer.parseInt(subVersions[0]);
        newMiddle = Integer.parseInt(subVersions[1]);
        newMinor = Integer.parseInt(subVersions[2]);
        newBuild = Integer.parseInt((subVersions[3]));
        return new int[]{newMajor, newMiddle, newMinor, newBuild};
    }

    @Transactional()
    public SchemaVersionInfo checkDbVersion() throws Exception {
        try {
            lookupDBVersions();
        } catch (Exception e) {
            logger.error("Failed to load db update map", e);
            throw e;
        }

        try {
            if (!RuntimeContext.isTestRunning()) {
                // Ниже скрипт может не стработать если стоит защита на внутренние таблицы баз данных
                String SQL_CHECK_TEXT_COLUMN = "SELECT attname FROM pg_attribute, pg_type WHERE typname = 'cf_schema_version_info' AND attname = 'committext'";
                List list = entityManager.createNativeQuery(SQL_CHECK_TEXT_COLUMN).getResultList();
                if(list!=null && list.isEmpty()){
                    entityManager.createNativeQuery("ALTER TABLE cf_schema_version_info ADD COLUMN committext text").executeUpdate();
                }
            }
            // Выше скрипт может не стработать если стоит защита на внутренние таблицы баз данных
            String SQL_GET_SCHEMAS="from SchemaVersionInfo order by majorVersionNum desc, middleVersionNum desc, minorVersionNum desc";
            List schemas = entityManager.createQuery(SQL_GET_SCHEMAS).getResultList();
            SchemaVersionInfo curSchemaVer = (SchemaVersionInfo)(schemas.size()==0?null:schemas.get(0));
            if (curSchemaVer !=null) {
                logger.info("DB version: "+ curSchemaVer);
            } else {
                logger.info(String.format("DB version not specified, inserting initial db version marker: %d.%d.%d.%d", INITIAL_DB_VERSION[0], INITIAL_DB_VERSION[1], INITIAL_DB_VERSION[2], INITIAL_DB_VERSION[3]));
                SchemaVersionInfo initialSchemaVersionInfo = new SchemaVersionInfo(INITIAL_DB_VERSION, new Date());
                entityManager.persist(initialSchemaVersionInfo);
                curSchemaVer = initialSchemaVersionInfo;
            }
            boolean bUpdated = false;
            /* если схема верии не пусто и если сервер запущен под MAIN узлом */
            if (curSchemaVer !=null && runtimeContext.isMainNode()) {
                for (;;) {
                    String curVerStr = String.format("%d.%d.%d", curSchemaVer.getMajorVersionNum(), curSchemaVer.getMiddleVersionNum(), curSchemaVer
                            .getMinorVersionNum());
                    String v = updatesMap.getProperty(curVerStr);
                    if (v==null) {
                        if (bUpdated) {
                            logger.info("DB update successful");
                        }
                        return curSchemaVer;
                    }
                    int[] newVer = parseVersion(v);
                    String sqlFile = String.format("update_%s-%d.%d.%d.sql", curVerStr, newVer[0], newVer[1], newVer[2]);
                    InputStream in = DBUpdater.class.getResourceAsStream(UPDATES_ROOT+"/"+sqlFile);
                    if (in==null) {
                        throw new Exception("Update data base script not found: "+sqlFile);
                    }
                    logger.info("Executing update script: "+sqlFile);
                    BufferedReader bufIn = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
                    String sqlCmd="";
                    StringBuilder commitText = new StringBuilder();
                    boolean isQuotesOpened=false;
                    for (;;) {
                        String l = bufIn.readLine();
                        if (l==null) break;
                        l = l.trim();
                        if (l.length()==0) continue;
                        if (l.startsWith("--!")) continue;
                        if (l.startsWith("--")){
                            commitText.append(l.substring(2,l.length()));
                            commitText.append("\n");
                            continue;
                        }
                        if (sqlCmd.length()!=0) sqlCmd+=" ";
                        sqlCmd+=l;
                        if (hasOddQuotes(l)) {
                            if (!isQuotesOpened) {
                                // открылись кавычки для длинной команды
                                isQuotesOpened = true;
                                continue;
                            } else {
                                isQuotesOpened = false;
                            }
                        }
                        if (!isQuotesOpened && sqlCmd.endsWith(";")) {
                            sqlCmd=sqlCmd.substring(0, sqlCmd.length()-1);
                            try {
                                if (sqlCmd.toLowerCase().startsWith("select")) entityManager.createNativeQuery(sqlCmd).getSingleResult();
                                else entityManager.createNativeQuery(sqlCmd).executeUpdate();
                            } catch (Exception e) {
                                throw new Exception("ошибка при выполнении сценария: "+sqlFile+" ("+sqlCmd+")", e);
                            }
                            sqlCmd="";
                        }
                    }
                    SchemaVersionInfo newSchemaVer = new SchemaVersionInfo(newVer, new Date());
                    if(commitText!=null && commitText.length()>0) newSchemaVer.setCommitText(commitText.toString());
                    entityManager.persist(newSchemaVer);
                    curSchemaVer =newSchemaVer;
                    bUpdated = true;
                }
            }
            return curSchemaVer;
        }
        catch (Exception e)
        {
            logger.error("Failed to update DB", e);
            throw e;
        }
    }

    private boolean hasOddQuotes(String l) {
        int n=0;
        for (int i=0;i<l.length();++i) {
            if (l.charAt(i)=='\'') n++;
        }
        return (n%2)==1;
    }
}
