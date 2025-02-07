package in.hashconnect.storage.impl;

import in.hashconnect.storage.vo.FileContent;

public abstract class AbstractStorageService {
	private static final String SUFFIX = "/";

	protected String formatFileName(FileContent fileContent) {
		String folder = fileContent.getFolder();
		if (folder != null && folder.startsWith("/")) {
			folder = folder.replaceFirst("/", "");
			return folder + SUFFIX + fileContent.getName();
		}

		// this may include complete path
		String fileName = fileContent.getName();
		if (fileName.contains("/"))
			fileName = fileName.replaceFirst("/", "");

		return fileName;

	}
}
