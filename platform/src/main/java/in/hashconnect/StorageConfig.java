package in.hashconnect;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import in.hashconnect.storage.impl.GoogleCloudStorageServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import in.hashconnect.storage.StorageService;
import in.hashconnect.storage.impl.AwsS3StorageServiceImpl;
import in.hashconnect.storage.impl.DoS3StorageServiceImpl;
import in.hashconnect.storage.impl.LocalStorageServiceImpl;
import org.springframework.context.annotation.Primary;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@Configuration
@ConditionalOnProperty(value = "storage.enabled", havingValue = "true", matchIfMissing = false)
public class StorageConfig {

	@Configuration
	@ConditionalOnProperty(value = "storage.type", havingValue = "aws")
	public class AWSStorageServiceConfig {

		@Value("${aws.storage.key:}")
		public String ACCESS_KEY;
		@Value("${aws.storage.secret:}")
		private String SECRET_KEY;
		@Value("${aws.storage.region:}")
		private String REGION;

		@Bean
		public AmazonS3 s3Service() {
			return AmazonS3ClientBuilder.standard().withRegion(REGION)
					.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)))
					.build();
		}

		@Bean
		@Primary
		public StorageService storageService() {
			return new AwsS3StorageServiceImpl();
		}

	}

	@Configuration
	@ConditionalOnProperty(value = "storage.type", havingValue = "local")
	public class LocalStorageConfig {

		@Bean
		@Primary
		public StorageService storageService() {
			return new LocalStorageServiceImpl();
		}

	}

	@Configuration
	@ConditionalOnProperty(value = "storage.type", havingValue = "do")
	public class DigitalOceanConfig {

		@Bean
		@Primary
		public StorageService storageService() {
			return new DoS3StorageServiceImpl();
		}

	}


	@Configuration
	@ConditionalOnProperty(value = "gcs.store.type", havingValue = "true")
	public class  GoogleStorageConfig{
		@Value("${gcs.creds:}")
		public String CRED_JSON;
		@Bean
		public Storage storage() throws IOException {
			GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(CRED_JSON.getBytes()));
			return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
		}
		@Bean
		public  StorageService storageGCSService(){return new GoogleCloudStorageServiceImpl();};
	}
}
