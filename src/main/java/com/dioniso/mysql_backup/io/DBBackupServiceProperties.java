package com.dioniso.mysql_backup.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Properties class which holds all the DBBackupService properties, read from a config.properties file.
 *
 */
public class DBBackupServiceProperties {
    
    private static final String S3_BUCKET_PROPERTY_KEY = "s3.bucket";
    private static final String S3_KEY_PROPERTY_KEY = "s3.key";
    private static final String DUMP_EXE_PATH_PROPERTY_KEY = "dump.exe.path";
    private static final String DB_HOST_PROPERTY_KEY = "db.host";
    private static final String DB_PORT_PROPERTY_KEY = "db.port";
    private static final String DB_USER_PROPERTY_KEY = "db.user";
    private static final String DB_PASS_PROPERTY_KEY = "db.pass";
    private static final String DB_PROPERTY_KEY = "db.name";
    private static final String BACKUP_PATH_PROPERTY_KEY = "backup.path";
    
    private static final String PROPERTIES_FILE_NAME = "config.properties";
    
    private String s3Bucket;
    private String s3Key;
    private String dumpExePath;
    private String dBHost;
    private String dBPort;
    private String dBUser;
    private String dBPass;
    private String dBName;
    private String backupPath;
    
    public DBBackupServiceProperties() {
        
    }
    
    public void init() throws IOException {
        InputStream inputStream;
        
        Properties prop = new Properties();
 
            inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
 
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + PROPERTIES_FILE_NAME + "' not found in the classpath");
        }
        inputStream.close();
        
        s3Bucket = prop.getProperty(S3_BUCKET_PROPERTY_KEY);
        s3Key = prop.getProperty(S3_KEY_PROPERTY_KEY);
        dumpExePath = prop.getProperty(DUMP_EXE_PATH_PROPERTY_KEY);
        dBHost = prop.getProperty(DB_HOST_PROPERTY_KEY);
        dBPort = prop.getProperty(DB_PORT_PROPERTY_KEY);
        dBUser = prop.getProperty(DB_USER_PROPERTY_KEY);
        dBPass = prop.getProperty(DB_PASS_PROPERTY_KEY);
        dBName = prop.getProperty(DB_PROPERTY_KEY);
        backupPath = prop.getProperty(BACKUP_PATH_PROPERTY_KEY);
    }

    public String getS3Bucket() {
        return s3Bucket;
    }

    public String getS3Key() {
        return s3Key;
    }

    public String getDumpExePath() {
        return dumpExePath;
    }

    public String getDBHost() {
        return dBHost;
    }

    public String getDBPort() {
        return dBPort;
    }

    public String getDBUser() {
        return dBUser;
    }

    public String getDBPass() {
        return dBPass;
    }

    public String getDBName() {
        return dBName;
    }

    public String getBackupPath() {
        return backupPath;
    }
}
