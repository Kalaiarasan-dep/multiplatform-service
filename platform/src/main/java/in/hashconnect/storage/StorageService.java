package in.hashconnect.storage;

import in.hashconnect.storage.vo.FileContent;

public interface StorageService {

	void put(FileContent fileContent);

	FileContent get(FileContent fileContent);

	boolean exist(FileContent fileContent);

	boolean delete(FileContent fileContent);
}
