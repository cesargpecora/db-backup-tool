package com.dioniso.mysql_backup;

import com.dioniso.mysql_backup.io.DBBackupService;

/**
 * Main java Class in charge of starting the DB backup process.
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        DBBackupService dBBackupService = new DBBackupService();
        if (args.length > 0 && args[0].equals("amazon")){
        	dBBackupService.backupDBToAmazonS3();	        
        }
        else{
        	dBBackupService.backupDBToLocalStorage();
        }
    }
}
