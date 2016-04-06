package com.dioniso.mysql_backup.io;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * MySQL Database backup Service in charge of accessing the DB and running the dump command, storing all the DB info to a .sql file.
 *
 */
public class MySQLDBBackupService {
    
    private static final Log LOG = LogFactory.getLog(DBBackupService.class);
    
    public MySQLDBBackupService() {}
    
    /**
     * This method does the dump of the Database DATA ONLY.
     * @param dumpExePath
     * @param host
     * @param port
     * @param user
     * @param password
     * @param database
     * @param backupPath
     * @return
     */
    public String backupDataWithOutDatabase(String dumpExePath, String host, String port, String user,
            String password, String database, String backupPath) throws IOException, InterruptedException{
        String filepath = null;
        Process p = null;

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        filepath = "backup-" + database + "-" + host + "-(" + dateFormat.format(date) + ").sql";

        String batchCommand = "";
        if (password != "") {
            // only backup the data not included create database
            batchCommand = dumpExePath + " -h " + host + " --port " + port + " -u " + user + " --password="
                    + password + " " + database + " -r \"" + backupPath + filepath + "\"";
        } else {
            batchCommand = dumpExePath + " -h " + host + " --port " + port + " -u " + user + " " + database
                    + " -r \"" + backupPath + filepath + "\"";
        }

        Runtime runtime = Runtime.getRuntime();
        p = runtime.exec(batchCommand);
        int processComplete = p.waitFor();

        if (processComplete == 0) {
            LOG.info("Backup created successfully for DB " + database + " in " + host + ":" + port);
        } else {
            LOG.info("Could not create the backup for DB " + database + " in " + host + ":" + port);
            throw new IOException("Process didn't finish correctly.");
        }
        return filepath;
    }
    
    /**
     * This method does the dump of the Database SCHEMA and DATA.
     * @param dumpExePath
     * @param host
     * @param port
     * @param user
     * @param password
     * @param database
     * @param backupPath
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
	public String backupDataWithDatabase(String dumpExePath, String host, String port, String user, String password,
			String database, String backupPath) throws IOException, InterruptedException {
		String filepath = null;

		Process p = null;

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		filepath = "backup-" + database + "-" + host + "-(" + dateFormat.format(date) + ").sql";

		String batchCommand = "";
		if (password != "") {
			// Backup with database
			batchCommand = dumpExePath + " -h " + host + " --port " + port + " -u " + user + " --password=" + password
					+ " --add-drop-database -B " + database + " -r \"" + backupPath + filepath + "\"";
		} else {
			batchCommand = dumpExePath + " -h " + host + " --port " + port + " -u " + user + " --add-drop-database -B "
					+ database + " -r \"" + backupPath + filepath + "\"";
		}

		Runtime runtime = Runtime.getRuntime();
		p = runtime.exec(batchCommand);
		int processComplete = p.waitFor();

		if (processComplete == 0) {
			LOG.info("Backup created successfully for DB " + database + " in " + host + ":" + port);
		} else {
			LOG.info("Could not create the backup for DB " + database + " in " + host + ":" + port);
			throw new IOException("Process didn't finish correctly.");
		}
		return filepath;
	}

}
