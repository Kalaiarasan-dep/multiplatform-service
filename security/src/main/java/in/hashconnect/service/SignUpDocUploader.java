package in.hashconnect.service;

import static in.hashconnect.util.StringUtil.concate;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.hashconnect.storage.StorageService;
import in.hashconnect.storage.vo.FileContent;
import in.hashconnect.vo.SignUpUploadVo;

@Service
public class SignUpDocUploader {
	@Autowired
	private StorageService s3Client;

	public void uploadFile(SignUpUploadVo vo, String bucket, String path) throws IOException {
		long uniqueId = System.currentTimeMillis();

		MultipartFile file = vo.getInFile();
		String ext = FilenameUtils.getExtension(file.getOriginalFilename());
		String fileName = concate("id-proof-", vo.getType(), "-", uniqueId, ".", ext);

		byte[] data = IOUtils.toByteArray(file.getInputStream());

		FileContent fc = new FileContent();
		fc.setData(data);
		fc.setFolder(path);
		fc.setName(fileName);
		fc.setBucket(bucket);
		s3Client.put(fc);

		vo.setUploadLocation(new File(path, fileName).getAbsolutePath());
	}
}
