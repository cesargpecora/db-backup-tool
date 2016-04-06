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
     * Creates the DB Dump File from the MySQL Database
     * @return dump file path
     */
    private String createDBDumpFile(){
    	MySQLDBBackupService mySQLBackupService = new MySQLDBBackupService();
    	String filePath = null;
    	
    	String backupPath = System.getProperty("user.dir") + File.separator + "tmp" + File.separator;
        
        try {
            filePath = mySQLBackupService.backupDataWithDatabase(
                    backupProperties.getDumpExePath(), backupProperties.getDBHost(), backupProperties.getDBPort(), 
                    backupProperties.getDBUser(), backupProperties.getDBPass(), backupProperties.getDBName(), 
                    backupPath);            
        } catch (IOException e) {
            LOG.error("Failed while backing up DB.",e.getCause());
        } catch (InterruptedException e) {
        	LOG.error("Failed while backing up DB, when trying to run script.",e.getCause());
        }
        return filePath;
    }
    
    /**
     * Uploads the backup File to the configured Amazon S3 Bucket.
     * Finally, deletes the file from the local storage.
     * @param filePath
     */
    private void uploadFileToAmazonS3(String filePath){
    	try {
            
            File dBBackupFile = new File(filePath);
            
        	String s3Key = backupProperties.getS3KeyPrefix() + dBBackupFile.getName();
            AmazonS3Service amazonS3Service = new AmazonS3Service();            
            
            amazonS3Service.addToBucket(backupProperties.getS3Bucket(), s3Key, dBBackupFile);            
            dBBackupFile.delete();
            
        } catch (IOException e) {
            LOG.error("Failed to upload file to Amazon S3.",e.getCause());
        }
    }
    
    /**
     * This method is the one that coordinates the actual backup. It first makes a dump of the MySQL Database to a file
     * and then it uploads the File to the configured Amazon S3 Bucket.
     * Finally, deletes the file from the local storage.
     */
    public void backupDBToAmazonS3(){
    	String filePath = createDBDumpFile();
        if (filePath != null){    	
        	uploadFileToAmazonS3(filePath);
        }
    }
    
    /**
     * This method is the one that coordinates the actual backup. It first makes a dump of the MySQL Database to a file
     * and then it uploads the File to the configured Amazon S3 Bucket.
     * Finally, deletes the file from the local storage.
     */
    public void backupDBToLocalStorage(){
    	createDBDumpFile();
    }

}
