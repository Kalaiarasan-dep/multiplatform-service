package in.hashconnect.storage.impl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.springframework.util.FileCopyUtils;

import in.hashconnect.notification.exception.StorageException;
import in.hashconnect.storage.StorageService;
import in.hashconnect.storage.vo.FileContent;

public class LocalStorageServiceImpl extends AbstractStorageService implements StorageService {

	@Override
	public void put(FileContent fileContent) {
		String file = formatFileName(fileContent);

		if (fileContent.getData() != null) {
			try (FileOutputStream out = new FileOutputStream(file)) {
				FileCopyUtils.copy(fileContent.getData(), out);
			} catch (Exception e) {
				throw new StorageException(e);
			}
		}

		if (fileContent.getInStream() != null) {
			try (FileOutputStream out = new FileOutputStream(file)) {
				FileCopyUtils.copy(fileContent.getInStream(), out);
			} catch (Exception e) {
				throw new StorageException(e);
			}
		}

	}

	@Override
	public FileContent get(FileContent fileContent) {
		String file = formatFileName(fileContent);

		try (FileInputStream in = new FileInputStream(new File(file));
				ByteArrayOutputStream out = new ByteArrayOutputStream(1000)) {
			IOUtils.copy(in, out);

			fileContent.setData(out.toByteArray());
			return fileContent;
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

	@Override
	public boolean exist(FileContent fileContent) {
		String file = formatFileName(fileContent);

		return Paths.get(file).toFile().exists();
	}
	@Override
	public boolean delete(FileContent fileContent) {
		try {
			Files.delete(Paths.get(fileContent.getName()));
			return true;
		} catch (IOException var3) {
			return false;
		}
	}
}
