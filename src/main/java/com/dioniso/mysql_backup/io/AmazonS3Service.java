package com.dioniso.mysql_backup.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
* Credentials are in  AwsCredentials.properties 
*/
public class AmazonS3Service {

	private static Log log = LogFactory.getLog(AmazonS3Service.class);
	private AmazonS3 client = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());


    /**
	 * Delete a bucket - A bucket must be completely empty before it can be
	 * deleted, so remember to delete any objects from your buckets before
	 * you try to delete them.
	 */
	public  void deleteBucket( String bucketName) {

		log.debug("Deleting bucket " + bucketName + "\n");
		client.deleteBucket(bucketName);
	}
	
	/**
	 * Delete an object - Unless versioning has been turned on for your bucket,
	 * there is no way to undelete an object, so use caution when deleting objects.
	 */
	public  void deleteObjectFromBucket( String bucketName,String key) {

		log.debug("Deleting an object\n");
		client.deleteObject(bucketName, key);
	}

	/**
	 * List objects in bucket by prefix - There are many options for
	 * listing the objects in your bucket.  Keep in mind that buckets with
	 * many objects might truncate their results when listing their objects,
	 * so be sure to check if the returned object listing is truncated, and
	 * use the AmazonS3.listNextBatchOfObjects(...) operation to retrieve
	 * additional results.
	 */
	public void getObjectsInBucket(String bucketName) {
		
		log.debug("Listing objects");
		ObjectListing objectListing = client.listObjects(new ListObjectsRequest().withBucketName(bucketName).withPrefix("My"));
		for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
		   log.debug(" - " + objectSummary.getKey() + "  " +"(size = " + objectSummary.getSize() + ")");
		}
	}
	
	/**
	 * Download an object - When you download an object, you get all of
	 * the object's metadata and a stream from which to read the contents.
	 * It's important to read the contents of the stream as quickly as
	 * possibly since the data is streamed directly from Amazon S3 and your
	 * network connection will remain open until you read all the data or
	 * close the input stream.
	 *
	 * GetObjectRequest also supports several other options, including
	 * conditional downloading of objects based on modification times,
	 * ETags, and selectively downloading a range of an object.
	 */
	public  void getObjectContents(String bucketName,String key) throws IOException {

		log.debug("Downloading an object");
		S3Object object = client.getObject(new GetObjectRequest(bucketName, key));
		
		log.debug("Content-Type: "  + object.getObjectMetadata().getContentType());
		displayTextInputStream(object.getObjectContent());
	}
	

	/**
	 * Upload an object to  bucket - You can easily upload a file to
	 * S3, or upload directly an InputStream if you know the length of
	 * the data in the stream. You can also specify your own metadata
	 * when uploading to S3, which allows you set a variety of options
	 * like content-type and content-encoding, plus additional metadata
	 * specific to your applications.
	 */
	public  void addToBucket( String bucketName, String key,File file)throws IOException {

		log.debug("Uploading a new object to S3 from a file\n");
		log.debug("bucketName: " + bucketName);
		log.debug("key: " + key);
		log.debug("file.getAbsolutePath(): " + file.getAbsolutePath());
		
		PutObjectResult result =  client.putObject(new PutObjectRequest(bucketName, key, file));
		
		log.debug("result.getETag(): " + result.getETag());
		log.debug("result.getExpirationTimeRuleId(): " + result.getExpirationTimeRuleId());
		log.debug("result.getVersionId(): " + result.getVersionId());
		log.debug("result.getExpirationTime(): " + result.getExpirationTime());
		
	}
	
	/**
	 * Upload an object to  bucket - You can easily upload a file to
	 * S3, or upload directly an InputStream if you know the length of
	 * the data in the stream. You can also specify your own metadata
	 * when uploading to S3, which allows you set a variety of options
	 * like content-type and content-encoding, plus additional metadata
	 * specific to your applications.
	 */
	public  void addToBucket( String bucketName, String key,File file, String mimeType)throws IOException {

		log.debug("Uploading a new object to S3 from a file\n");
		log.debug("bucketName: " + bucketName);
		log.debug("key: " + key);
		log.debug("file.getAbsolutePath(): " + file.getAbsolutePath());
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.length());
		metadata.setContentType(mimeType);
		FileInputStream stream = new FileInputStream(file);
		try {
			PutObjectResult result =  client.putObject(new PutObjectRequest(bucketName, key, stream, metadata));
			log.debug("result.getETag(): " + result.getETag());
			log.debug("result.getExpirationTimeRuleId(): " + result.getExpirationTimeRuleId());
			log.debug("result.getVersionId(): " + result.getVersionId());
			log.debug("result.getExpirationTime(): " + result.getExpirationTime());
		} finally {
			IOUtils.closeQuietly(stream);
		}
		
	}
	
	/**
	 * Upload an object to  bucket - You can easily upload a file to
	 * S3, or upload directly an InputStream if you know the length of
	 * the data in the stream. You can also specify your own metadata
	 * when uploading to S3, which allows you set a variety of options
	 * like content-type and content-encoding, plus additional metadata
	 * specific to your applications.
	 */
	public void addToBucket(String bucketName, String keyName, InputStream inputStream, String mimeType) throws IOException {
		
		log.debug("Uploading a new object to S3 from a inputstream\n");
		log.debug("bucketName: " + bucketName);
		log.debug("key: " + keyName);
		ObjectMetadata metadata = new ObjectMetadata();
		byte[] contentBytes = IOUtils.toByteArray(inputStream);
		metadata.setContentLength(contentBytes.length);
		metadata.setContentType(mimeType);
		try {
			PutObjectResult result = client.putObject(new PutObjectRequest(bucketName, keyName, inputStream, metadata));
			log.debug("result.getETag(): " + result.getETag());
			log.debug("result.getExpirationTimeRuleId(): " + result.getExpirationTimeRuleId());
			log.debug("result.getVersionId(): " + result.getVersionId());
			log.debug("result.getExpirationTime(): " + result.getExpirationTime());
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}
	
	/**
	 * Return the buckets in your account
	 */
	public  List<Bucket>  getBucketNames() {

		log.debug("Listing buckets");
		List<Bucket> buckets = client.listBuckets();
		for (Bucket bucket : buckets) {
			log.debug(" - " + bucket.getName());
		}
	return 	buckets;
	}
	
	/**
	 * Create a new S3 bucket - Amazon S3 bucket names are globally unique,
	 * so once a bucket name has been taken by any user, you can't create
	 * another bucket with that same name.
	 *
	 * You can optionally specify a location for your bucket if you want to
	 * keep your data closer to your applications or users.
	 */
	public  void createBucket(String bucketName) {

		log.debug("Creating bucket " + bucketName + "\n");
		client.createBucket(bucketName);
	}

    /**
     * Displays the contents of the specified input stream as text.
     *
     * @param input
     *            The input stream to display as text.
     *
     * @throws IOException
     */
    private  void displayTextInputStream(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            
           log.debug("    " + line);
        }
      
    }

}
