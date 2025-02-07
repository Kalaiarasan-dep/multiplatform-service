package in.hashconnect.service;

import org.springframework.beans.factory.annotation.Autowired;

import in.hashconnect.dao.GenericDao;
import in.hashconnect.storage.StorageService;
import in.hashconnect.util.SettingsUtil;

public abstract class AbstractImageLoader {
	@Autowired
	protected GenericDao genericDao;

	@Autowired
	protected StorageService storageService;

	@Autowired
	protected SettingsUtil settingsUtil;
}
