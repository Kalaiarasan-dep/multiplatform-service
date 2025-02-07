package in.hashconnect.dao;

import static in.hashconnect.util.StringUtil.isValid;
import static org.apache.commons.collections4.MapUtils.getString;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import in.hashconnect.excel.reader.Row;
import in.hashconnect.util.JsonUtil;

@Repository
public class BulkImportDaoImpl extends BaseDao implements BulkImportDao {

	@Autowired
	GenericDao genericDao;
	private static final Logger logger = LoggerFactory.getLogger(GenericDao.class);

	@Override
	public Integer saveToDB(String fileName, List<Row> rows, String userId, String type, String programId,
			String batchId) {
		String jsonData = JsonUtil.toString(rows);
		String query = "insert into bulk_upload_file(file_name,json,created_dt,user_id,bulk_upload_type_id,upload_batch_id,program_id)values(?,?,now(),?,1,?,?)";

		KeyHolder kh = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(query, new String[] { "id" });
				pst.setString(1, fileName);
				pst.setString(2, jsonData);
				pst.setString(3, userId);
				pst.setString(4, batchId);
				pst.setString(5, programId);
				return pst;
			}
		}, kh);

		return kh.getKey().intValue();
	}

	@Override
	public List<Map<String, Object>> getAllColumns(String programId) {
		return jdbcTemplate.queryForList("select * from bulk_upload_mapping where program_id=?", programId);
	}

	@Override
	public void mapFields(Integer refId, List<Map<String, Object>> mappedFields) {
		jdbcTemplate.update("update bulk_upload_file set mapped_fields=?,modified_dt=now() where id=?",
				JsonUtil.toString(mappedFields), refId);

	}

	@Override
	public Map<String, Object> getDataToValidateByRefId(Integer refId) {
		try {
			return jdbcTemplate.queryForMap("select * from bulk_upload_file where id=?", refId);
		} catch (EmptyResultDataAccessException e) {
		}
		return null;
	}

	@Override
	public boolean isCompanyExist(String company) {
		return jdbcTemplate.queryForObject("select count(*) from company where name=?", Integer.class, company) > 0;
	}

	@Override
	public void updateRowsByRefId(Integer refId, List<Row> rows) {
		jdbcTemplate.update("update bulk_upload_file set json=? where id=?", JsonUtil.toString(rows), refId);
	}

	@Override
	public boolean importToBulkUpload(Integer refId, List<Map<String, Object>> mappedFields, List<Object[]> rows) {
		StringBuilder placeHolders = new StringBuilder();

		String dbColumns = mappedFields.stream().map(m -> {
			if (!isValid(getString(m, "mappedColumn")))
				return null;
			placeHolders.append("?,");
			return getString(m, "column");
		}).filter(v -> isValid(v)).collect(Collectors.joining(","));

		placeHolders.replace(placeHolders.lastIndexOf(","), placeHolders.length(), "");

		String query = concate("insert into `tmp_order`(bulk_upload_file_id,partner_id,status_id,program_id,",
				dbColumns, ") ", "values(?,?,?,?,", placeHolders, ")");

		List<List<Object[]>> splitRows = partitionList(rows, 100);

		splitRows.parallelStream().forEach(new LoadTmpOrderConsumer(refId, jdbcTemplate, query));

		if (jdbcTemplate.queryForObject("select count(*) from order_upload_failure where file_id=?", Integer.class,
				refId) == 0) {
			// all are loaded now copy to order table
			jdbcTemplate.update(concate("insert into `order` (created_dt,modified_dt,bulk_upload_file_id,batch_code,",
					"status_id,error,program_name,partner_name,order_id,order_date,claimed_amount,",
					"approved_amount,order_status,program_id,partner_id,invoice_request_id,contract_id,merchant_id ",
					") select created_dt,modified_dt,bulk_upload_file_id,batch_code,",
					"status_id,error,program_name,partner_name,order_id,order_date,claimed_amount,",
					"approved_amount,order_status,program_id,partner_id,invoice_request_id,contract_id,merchant_id ",
					" from tmp_order where bulk_upload_file_id=?"), refId);
			// everything is fine
			return true;
		}

		// failed to save to order table
		return false;
	}

	private static <T> List<List<T>> partitionList(List<T> list, int size) {
		int numOfPartitions = (int) Math.ceil((double) list.size() / size);
		return IntStream.range(0, numOfPartitions)
				.mapToObj(i -> list.subList(i * size, Math.min((i + 1) * size, list.size())))
				.collect(Collectors.toList());
	}

	@Override
	public Map<String, Integer> findPartnerDetailsByProgramAndMorCId(String program, List<String> merchantIds) {
		Map<String, Object> params = new HashMap<>(1);
		params.put("ids", merchantIds);

		Map<String, Integer> out = new HashMap<>();

		String query = !"1".equals(program) ? "select contract_id,id from partner where contract_id in (:ids)"
				: "select merchant_id,id from partner where merchant_id in (:ids)";

		namedParameterJdbcTemplate.query(query, params, new RowMapper<Object>() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				out.put(rs.getString(1), rs.getInt(2));
				return null;
			}
		});

		return out;
	}

	@Override
	public Map<String, Object> getFileDetails(Integer refId) {
		String query = "select f.id,u.email,f.file_name,f.created_dt from bulk_upload_file f join `user` u on u.id=f.user_id where f.id=?";
		try {
			return jdbcTemplate.queryForMap(query, refId);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}