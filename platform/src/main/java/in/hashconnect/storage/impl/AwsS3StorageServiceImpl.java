package in.hashconnect.storage.impl;

import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.model.*;
import in.hashconnect.storage.StorageService;
import in.hashconnect.storage.vo.FileContent;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class AwsS3StorageServiceImpl extends AbstractStorageService implements StorageService {

	@Autowired
	private AmazonS3 s3Client;

	private static String CONTENT_ENCODING = "gzip";

	private final Logger logger = LoggerFactory.getLogger(AwsS3StorageServiceImpl.class);

	@Override
	public void put(FileContent fileContent) {
		byte[] bytes = fileContent.getData();
		if (bytes == null && fileContent.getInStream() != null) {
			try {
				bytes = IOUtils.toByteArray(fileContent.getInStream());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		ObjectMetadata metaData = new ObjectMetadata();
		metaData.setContentLength(bytes.length);
		metaData.setContentType(fileContent.getContentType());

		PutObjectRequest putobj = new PutObjectRequest(fileContent.getBucket(), formatFileName(fileContent),
				new ByteArrayInputStream(bytes), metaData);
		s3Client.putObject(putobj);
	}

	@Override
	public FileContent get(FileContent fileContent) {
		String fileName = this.formatFileName(fileContent);
		if (!this.exist(fileContent)) {
			return null;
		}

		GetObjectRequest request = new GetObjectRequest(fileContent.getBucket(), fileName);
		Long rangeStart = fileContent.getRangeStart();
		Long rangeEnd = fileContent.getRangeEnd();
		if (rangeStart != null && rangeEnd != null) {
			request.setRange(rangeStart, rangeEnd);
		}
		boolean allFine = false;
		try {
			S3Object object = this.s3Client.getObject(request);

			S3ObjectInputStream in = object.getObjectContent();
			ObjectMetadata metaData = object.getObjectMetadata();
			fileContent.setData(IOUtils.toByteArray(this.getInputStream(metaData.getContentEncoding(), in)));
			fileContent.setContentType(metaData == null ? null : metaData.getContentType());
			allFine = true;
		} catch (Exception e) {
			logger.error("failed to getObject", e);
		} finally {
			logger.debug("downloaded file " + fileName + ", allFine: " + allFine);
		}

		return fileContent;

	}

	@Override
	public boolean exist(FileContent fileContent) {
		String fileName = formatFileName(fileContent);
		try {
			s3Client.getObjectMetadata(fileContent.getBucket(), fileName);
			return true;
		} catch (AmazonS3Exception e) {
			logger.error("failed to read object. bucket: {}, file: {}", fileContent.getBucket(), fileName, e);
		}
		return false;
	}

	private InputStream getInputStream(String encoding, InputStream in) throws Exception {
		return (InputStream) (CONTENT_ENCODING.equals(encoding) ? new GZIPInputStream(in) : in);
	}

	public boolean delete(FileContent fileContent) {
		try {
			this.s3Client
					.deleteObject(new DeleteObjectRequest(fileContent.getBucket(), this.formatFileName(fileContent)));
			return true;
		} catch (Exception var3) {
			throw new RuntimeException("failed to delete from bucket", var3);
		}
	}
}
