package in.hashconnect.service;

import java.util.Map;

import in.hashconnect.storage.vo.FileContent;

public interface ImageLoaderService {
	FileContent getImage(Map<String, Object> params);
}
