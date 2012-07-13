/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.axetta.ecafe.processor.core.updater;

import ru.axetta.ecafe.processor.core.persistence.SchemaVersionInfo;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DBUpdater {
    final static Logger logger = LoggerFactory.getLogger(DBUpdater.class);
    final static String UPDATES_ROOT="/db";

    @PersistenceContext
    EntityManager em;


    int[] INITIAL_DB_VERSION;
    Properties updatesMap = new Properties();

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
            String SQL_CHECK_TEXT_COLUMN = "SELECT attname FROM pg_attribute, pg_type WHERE typname = 'cf_schema_version_info' AND attname = 'committext'";
            List list = em.createNativeQuery(SQL_CHECK_TEXT_COLUMN).getResultList();
            if(list!=null && list.isEmpty()){
                em.createNativeQuery("ALTER TABLE cf_schema_version_info ADD COLUMN committext text").executeUpdate();
            }
            String SQL_GET_SCHEMAS="from SchemaVersionInfo order by majorVersionNum desc, middleVersionNum desc, minorVersionNum desc";
            List schemas = em.createQuery(SQL_GET_SCHEMAS).getResultList();
            SchemaVersionInfo curSchemaVer = (SchemaVersionInfo)(schemas.size()==0?null:schemas.get(0));
            if (curSchemaVer !=null) {
                logger.info("DB version: "+ curSchemaVer);
            } else {
                logger.info(String.format("DB version not specified, inserting initial db version marker: %d.%d.%d.%d", INITIAL_DB_VERSION[0], INITIAL_DB_VERSION[1], INITIAL_DB_VERSION[2], INITIAL_DB_VERSION[3]));
                SchemaVersionInfo initialSchemaVersionInfo = new SchemaVersionInfo(INITIAL_DB_VERSION, new Date());
                em.persist(initialSchemaVersionInfo);
                curSchemaVer = initialSchemaVersionInfo;
            }
            boolean bUpdated = false;
            if (curSchemaVer !=null) {
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
                        throw new Exception("не найден сценарий обновления базы: "+sqlFile);
                    }
                    logger.info("Executing update script: "+sqlFile);
                    BufferedReader bufIn = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
                    String sqlCmd="";
                    StringBuilder commitText = new StringBuilder();
                    for (;;) {
                        String l = bufIn.readLine();
                        if (l==null) break;
                        l = l.trim();
                        if (l.length()==0) continue;
                        if (l.startsWith("--")){
                            commitText.append(l.substring(2,l.length()));
                            commitText.append("\n");
                            continue;
                        }
                        if (sqlCmd.length()!=0) sqlCmd+=" ";
                        sqlCmd+=l;
                        if (sqlCmd.endsWith(";")) {
                            sqlCmd=sqlCmd.substring(0, sqlCmd.length()-1);
                            try {
                                em.createNativeQuery(sqlCmd).executeUpdate();
                            } catch (Exception e) {
                                throw new Exception("ошибка при выполнении сценария: "+sqlFile+" ("+sqlCmd+")", e);
                            }
                            sqlCmd="";
                        }
                    }
                    SchemaVersionInfo newSchemaVer = new SchemaVersionInfo(newVer, new Date());
                    if(commitText!=null && commitText.length()>0) newSchemaVer.setCommitText(commitText.toString());
                    em.persist(newSchemaVer);
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
}
