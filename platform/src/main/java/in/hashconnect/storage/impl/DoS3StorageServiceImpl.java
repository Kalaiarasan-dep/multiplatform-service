package in.hashconnect.storage.impl;

import in.hashconnect.storage.StorageService;
import in.hashconnect.storage.vo.FileContent;

public class DoS3StorageServiceImpl implements StorageService {

	@Override
	public void put(FileContent fileContent) {
		throw new UnsupportedOperationException();

	}

	@Override
	public FileContent get(FileContent fileContent) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean exist(FileContent fileContent) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean delete(FileContent fileContent) { throw new UnsupportedOperationException(); }

}
