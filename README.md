# db-backup-tool
This is a Windows Database Backup Tool to help in automated backup of a MySQL database to Amazon S3.

## Running the Tool
In order to run this app, you should either 
* download the source code
* change the properties files and put your specific Amazon and Database information
* run a "mvn clean install" 
* copy the .jar and lib folders from the target folder to whichever place you are going to run the Tool
* run the Tool from the copied place using: 
** java -jar "mysql-backup-1.0.0.jar" -> if you want to backup the data to the local storage
** java -jar "mysql-backup-1.0.0.jar" amazon -> if you want to backup the data to amazon S3

Or:
* download releases folder
* change the properties files inside the app .jar file (using a compression tool) and put your specific Amazon and Database information
* copy the .jar and lib folders to whichever place you are going to run the Tool
* run the Tool from the copied place using: 
 * java -jar "mysql-backup-1.0.0.jar" -> if you want to backup the data to the local storage
 * java -jar "mysql-backup-1.0.0.jar" amazon -> if you want to backup the data to amazon S3

## Configuring the Tool to run at scheduled Times (Windows only)

If you want to run the Tool at scheduled times (let's say for daily backups) you can create a Task in the Windows scheduler.
Here are the steps:
* Open the Windows Task Scheduler: Control Panel -> System and Security -> Administrative Tools -> Task Scheduler.
* Create a Basic Task (Under Actions):
 * Input the name.
 * Set up the trigger.
 * Set up the Action as "Start a Program":
  * Program script: Path to java.exe
  * Add arguments: -jar "Path to .jar of the tool"  (optional argument "amazon" - see above for more details)
  * Start in: Path to .jar of the tool.

## Configuring Amazon S3 to automatically delete old backups
You can configure Amazon to automatically delete old backups based on a rule you apply to the target bucket.

Here are the steps:
* Open the Amazon AWS Console and open the S3 Service.
* Click on the Bucket you use for the MySQL backups.
* Open the Properties and click on Lifecycle.
* Click on "Add Rule":
 * Choose Rule Target (whole bucket or a specific folder inside the Bucket)
 * Configure the Rule: For example, "Permanently Delete" after 10 days of creation to store the last 10 backups
 * Review and Save.

**Always remember to create a specific IAM User with only the needed permissions when consuming Amazon AWS services. Start here http://docs.aws.amazon.com/IAM/latest/UserGuide/getting-setup.html for IAM users setup.** 
