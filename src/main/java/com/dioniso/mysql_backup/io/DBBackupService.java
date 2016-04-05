package com.dioniso.mysql_backup.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Database Backup Service which coordinates the backup of the database to Amazon S3.
 *
 */
public class DBBackupService {
    
    DBBackupServiceProperties backupProperties;
    
    private static final Log LOG = LogFactory.getLog(DBBackupService.class);      
    
    public DBBackupService() {
        backupProperties = new DBBackupServiceProperties();
        
        try {
            backupProperties.init();
        } catch (IOException e) {
            LOG.error("Error loading properties.");
        }        
        
    }
    
    /**
     * This method is the one that coordinates the actual backup. It first makes a dump of the MySQL Database to a file
     * and then it uploads the File to the configured Amazon S3 Bucket.
     * Finally, deletes the file from the local storage.
     */
    public void backupDB(){
        
        MySQLDBBackupService mySQLBackupService = new MySQLDBBackupService();
        
        try {
            String filePath = mySQLBackupService.backupDataWithDatabase(
                    backupProperties.getDumpExePath(), backupProperties.getDBHost(), backupProperties.getDBPort(), 
                    backupProperties.getDBUser(), backupProperties.getDBPass(), backupProperties.getDBName(), 
                    backupProperties.getBackupPath());            
            File dBBackupFile = new File(filePath);
            
            AmazonS3Service amazonS3Service = new AmazonS3Service();        
            amazonS3Service.addToBucket(backupProperties.getS3Bucket(), backupProperties.getS3Key(), dBBackupFile);            
            dBBackupFile.delete();
            
        } catch (IOException e) {
            LOG.error("Failed to backup file to Amazon S3.",e.getCause());
        } catch (InterruptedException e) {
            LOG.error("Failed to backup file to Amazon S3, failed while backing up DB.",e.getCause());
        }
    }

}
