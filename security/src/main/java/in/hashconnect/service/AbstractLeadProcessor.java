package in.hashconnect.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import in.hashconnect.dao.BulkImportDao;
import in.hashconnect.excel.reader.ExcelReader;
import in.hashconnect.excel.reader.Row;

public abstract class AbstractLeadProcessor {
	@Autowired
	protected BulkImportDao bulkImportDao;

	private final Logger logger = LoggerFactory.getLogger(AbstractLeadProcessor.class);

	protected Map<String, Object> parse(MultipartFile file, String userId,  String type,String programId,String batchId) {
		String fileName = file.getOriginalFilename();

		logger.debug("file received {}", fileName);

		try (InputStream in = file.getInputStream()) {

			// prepare reader object
			ExcelReader reader = new ExcelReader(fileName, in);

			// reading excel
			reader.read();

			// get all rows
			List<Row> rows = reader.getWorkBook().getSheets().get(0).getRows();

			logger.info("file {}, read with {} rows", fileName, rows.size());

			// iterate and update row index
			for (int i = 0; i < rows.size();)
				rows.get(i).setRowNumber(i++ + 1);

			Row row = rows.get(0);
			row.setHeader(true);

			// save to db
			Integer refId = bulkImportDao.saveToDB(fileName, rows, userId, type,programId,batchId);

			Map<String, Object> meta = new HashMap<>(2);
			meta.put("refId", refId);
			meta.put("rows", rows);

			return meta;
		} catch (Exception e) {
			logger.error("failed to read excel", e);
		}

		return null;
	}
}
