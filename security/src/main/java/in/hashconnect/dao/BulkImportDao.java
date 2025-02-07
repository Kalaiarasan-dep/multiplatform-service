package in.hashconnect.dao;

import java.util.List;
import java.util.Map;

import in.hashconnect.excel.reader.Row;

public interface BulkImportDao {

	Integer saveToDB(String fileName, List<Row> rows, String userId, String type, String programId, String batchId);

	List<Map<String, Object>> getAllColumns(String programId);

	void mapFields(Integer refId, List<Map<String, Object>> mappedFields);

	Map<String, Object> getDataToValidateByRefId(Integer refId);

	boolean isCompanyExist(String company);

	void updateRowsByRefId(Integer refId, List<Row> rows);

	boolean importToBulkUpload(Integer refId, List<Map<String, Object>> mappedFields, List<Object[]> rowsToSave);

	Map<String, Integer> findPartnerDetailsByProgramAndMorCId(String programId, List<String> merchantId);

	Map<String, Object> getFileDetails(Integer id);
}
