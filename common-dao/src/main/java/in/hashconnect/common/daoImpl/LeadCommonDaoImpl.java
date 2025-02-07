package in.hashconnect.common.daoImpl;

import static in.hashconnect.common.util.StringUtil.concate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Hex;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import in.hashconnect.common.beans.LeadVo;
import in.hashconnect.common.beans.Leads;
import in.hashconnect.common.beans.LeadsExtra;
import in.hashconnect.common.beans.LeadsHistory;
import in.hashconnect.common.beans.LeadsStoreMapping;
import in.hashconnect.common.dao.LeadCommonDao;
import in.hashconnect.common.util.SettingsUtil;
import in.hashconnect.common.util.StringUtil;

public class LeadCommonDaoImpl extends JdbcDaoSupport implements LeadCommonDao {
	
	@Resource
	private SettingsUtil settingsUtil;
	

	public Integer checkMobileExistInLeads(String mobile_no) {
		Long leadId = leadAlreadyExistByMobile(mobile_no);
		if (leadId != null)
			return leadId.intValue();
		return null;
	}

	public Long getByMobile(String mobile) {
		return leadAlreadyExistByMobile(mobile);
	}

	public Long leadAlreadyExistByMobile(String mobileNo) {
		String selectQuery = "select id from leads where mobile_no=? and active_status=104 order by id desc limit 1";
		try {
			return getJdbcTemplate().queryForObject(selectQuery,
					new Object[]{mobileNo}, Long.class);
		} catch (EmptyResultDataAccessException e) {
			try {
				mobileNo = convertToHexString(mobileNo);
				return getJdbcTemplate().queryForObject(selectQuery,
						new Object[]{mobileNo}, Long.class);
			} catch (Exception e1) {
			}
		}
		return null;
	}

	public void insertIntoLeadSourceMapping(int leadId, int source_id) {
		insertIntoLeadSourceMapping((long) leadId, source_id);
	}

	public void insertIntoLeadSourceMapping(long leadId, int source_id) {
		String insertMapQuery = "insert into lead_source_mapping (lead_id,source_id,created_date) values (?,?,now())";
		getJdbcTemplate().update(insertMapQuery, leadId, source_id);
	}
	

	@Override
	public void leadHistoryChecks(Long leadId, Long storeId) {
		String query = "update lead_history set lead_status = 0 WHERE lead_status = 1 AND lead_id = ? AND  store_id = ?";
		getJdbcTemplate().update(query, leadId, storeId);
	}

	@Override
	public Long insertIntoLeads(final Leads leads) {
		final String query = "insert into leads (first_name,last_name,mobile_no,mobile_no2,email_id,landline_no1,address,pincode,"
				+ "facebook_id,twitter_id,circle_id,city_id,created_date,modified_date,company_name,active_status, encoded, comments, verification_id)"
				+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

		KeyHolder kh = new GeneratedKeyHolder();

		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					int index = 1;
					PreparedStatement pst = con.prepareStatement(query,
							Statement.RETURN_GENERATED_KEYS);
					if (leads.getFirst_name() == null)
						pst.setNull(index++, Types.NULL);
					else
						pst.setString(index++, leads.getFirst_name());
					if (leads.getLast_name() == null)
						pst.setNull(index++, Types.NULL);
					else
						pst.setString(index++, leads.getLast_name());
					if (leads.getMobile_no() == null)
						pst.setNull(index++, Types.NULL);
					else
						pst.setString(index++, leads.getMobile_no());
					if (leads.getMobile_no2() == null)
						pst.setNull(index++, Types.NULL);
					else
						pst.setString(index++, leads.getMobile_no2());
					if (leads.getEmail_id() == null)
						pst.setNull(index++, Types.NULL);
					else
						pst.setString(index++, leads.getEmail_id());
					if (leads.getLandline_no1() == null)
						pst.setNull(index++, Types.NULL);
					else
						pst.setString(index++, leads.getLandline_no1());
					if (leads.getAddress() == null)
						pst.setNull(index++, Types.NULL);
					else
						pst.setString(index++, leads.getAddress());
					if (leads.getPincode() == null)
						pst.setNull(index++, Types.NULL);
					else
						pst.setString(index++, leads.getPincode());
					if (leads.getFacebook_id() == null)
						pst.setNull(index++, Types.NULL);
					else
						pst.setString(index++, leads.getFacebook_id());
					if (leads.getTwitter_id() == null)
						pst.setNull(index++, Types.NULL);
					else
						pst.setString(index++, leads.getTwitter_id());
					if (leads.getCircle_id() == null)
						pst.setNull(index++, Types.NULL);
					else
						pst.setInt(index++, leads.getCircle_id());
					if (leads.getCity_id() == null)
						pst.setNull(index++, Types.NULL);
					else
						pst.setInt(index++, leads.getCity_id());
					if (leads.getCreated_date() == null)
						pst.setNull(index++, Types.NULL);
					else
						pst.setString(index++, leads.getCreated_date());
					if (leads.getModified_date() == null)
						pst.setNull(index++, Types.NULL);
					else
						pst.setString(index++, leads.getModified_date());
					if (leads.getCompany_name() == null)
						pst.setNull(index++, Types.NULL);
					else
						pst.setString(index++, leads.getCompany_name());
					if (leads.getActive_status() == null)
						pst.setNull(index++, Types.NULL);
					else
						pst.setInt(index++, leads.getActive_status());
					if (leads.getEncoded() == null) {
						leads.setEncoded("N");
						pst.setString(index++, leads.getEncoded());
					} else {
						pst.setString(index++, leads.getEncoded());
					} if (leads.getComments() == null) {
						pst.setNull(index++, Types.NULL);
					} else {
						pst.setString(index++, leads.getComments());
					} if (leads.getVerificationId() == null) {
						pst.setNull(index++, Types.NULL);
					} else {
						pst.setInt(index++, leads.getVerificationId());
					} 
					
					return pst;
				}
			}, kh);

			leads.setId(kh.getKey().longValue());

			return leads.getId();
		} catch (UncategorizedSQLException e) {
			logger.info("insertIntoLeads failed ", e);
			String mes = e.getMessage();
			if (mes.contains("Incorrect string value")) {
				leads.setFirst_name(convertToHexString(leads.getFirst_name()));
				leads.setLast_name(convertToHexString(leads.getLast_name()));
				leads.setMobile_no(convertToHexString(leads.getMobile_no()));
				leads.setEmail_id(convertToHexString(leads.getEmail_id()));
				leads.setPincode(convertToHexString(leads.getPincode()));
				leads.setEncoded("Y");

				return insertIntoLeads(leads);
			}
		}
		return null;
	}

	private String convertToHexString(String value) {
		if (value != null) {
			return Hex.encodeHexString(value.getBytes());
		}
		return null;
	}

	@Override
	public Long insertIntoLeadStoreMapping(
			final LeadsStoreMapping leadsStoreMapping) {

		String queryForCheckingCityId = " select city_id from leads where  id=? ";
		Long cityIdOld = null;
		try {
			cityIdOld = getJdbcTemplate().queryForObject(queryForCheckingCityId,
					new Object[]{leadsStoreMapping.getLead_id()}, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.info("no city Id found so updating city id first dealer ");
		}
		if (cityIdOld == null) {
			String queryForCityId = "select cityid from tbl_dealer where storeID=? ";
			Long cityId = null;
			try {
				cityId = getJdbcTemplate().queryForObject(queryForCityId,
						new Object[]{leadsStoreMapping.getStore_id()},
						Long.class);
			} catch (EmptyResultDataAccessException e) {
				logger.info("no city id for dealer also please check city id");
			}
			String updateLeadsQuery = " update leads set city_id=? where id=? ";
			getJdbcTemplate().update(updateLeadsQuery, cityId,
					leadsStoreMapping.getLead_id());
		}
		                                                   //******  add the new two column and update it .
//		final String query = "insert into lead_store_mapping (lead_id,store_id,status_id,source_id,product_id,bookademo_customer_referenceId,bookademo_interest) values (?,?,?,?,?,?,?);";
		String query ="";
		if(StringUtil.isValid(leadsStoreMapping.getCreated_date())) {
			query = "insert into lead_store_mapping (lead_id,store_id,status_id,source_id,product_id,bookademo_customer_referenceId,bookademo_interest,created_date) values (?,?,?,?,?,?,?,?);";
		}else {
			query = "insert into lead_store_mapping (lead_id,store_id,status_id,source_id,product_id,bookademo_customer_referenceId,bookademo_interest) values (?,?,?,?,?,?,?);";
		}		
		final String finalQuery=query;
		KeyHolder kh = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement pst = con.prepareStatement(finalQuery,
						new String[]{"id"});
				try {

				pst.setLong(1, leadsStoreMapping.getLead_id());
				if (leadsStoreMapping.getStore_id() == null)
					pst.setNull(2, Types.NULL);
				else
					pst.setInt(2, Integer
							.parseInt(leadsStoreMapping.getStore_id() + ""));
				if (leadsStoreMapping.getStatus_id() == null)
					pst.setNull(3, Types.NULL);
				else
					pst.setInt(3, leadsStoreMapping.getStatus_id());
				if (leadsStoreMapping.getSource_id() == null)
					pst.setNull(4, Types.NULL);
				else
					pst.setInt(4, leadsStoreMapping.getSource_id());
				if (leadsStoreMapping.getProd_id() == null)
					pst.setNull(5, Types.NULL);
				else
					pst.setInt(5, leadsStoreMapping.getProd_id());
				if (leadsStoreMapping.getBookaDemoRefId() == null)
					pst.setNull(6, Types.NULL);
				else
					pst.setLong(6, leadsStoreMapping.getBookaDemoRefId());
				if (leadsStoreMapping.getBookaDemoInterested() == null)
					pst.setNull(7, Types.NULL);
				else
					pst.setString(7, leadsStoreMapping.getBookaDemoInterested());
				if(StringUtil.isValid(leadsStoreMapping.getCreated_date())) {
					if (leadsStoreMapping.getCreated_date() == null)
						pst.setNull(8, Types.NULL);
					else {
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						java.util.Date parsedDate = dateFormat.parse(leadsStoreMapping.getCreated_date());
						Timestamp newTimestamp = new Timestamp(parsedDate.getTime());
						pst.setTimestamp(8, newTimestamp);
					}
				}
				 } catch (ParseException e) {
			            throw new SQLException("Date parsing failed createdDate", e);
			        }

				return pst;
			}
		}, kh);

		leadsStoreMapping.setId(kh.getKey().longValue());

		return leadsStoreMapping.getId();
	}

	@Override
	public Long insertIntoLeadHistory(final LeadsHistory leadsHistory) {
		// any anything older exist then update so it would be visible on top
	String update_lead_store_mapping_date=settingsUtil.getValue("update_lead_store_mapping_date");
		
		if(update_lead_store_mapping_date == null) {
			String updatecreateddatelsmquery = "update lead_store_mapping set created_date=? where id=? and status_id<>3";
			getJdbcTemplate().update(updatecreateddatelsmquery,leadsHistory.getCreatedDate(),
					leadsHistory.getStoreMapId());		
		}
		

		final String query = " insert into lead_history (lead_id,status,description,created_date,not_interested_reason,store_id,lead_store_map_id,source_id,comments) values (?,?,?,now(),?,?,?,?,?);";
		KeyHolder kh = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement pst = con.prepareStatement(query,
						new String[]{"id"});
				if (leadsHistory.getLeadId() == null)
					pst.setNull(1, Types.NULL);
				else
					pst.setInt(1,
							Integer.parseInt(leadsHistory.getLeadId() + ""));
				if (leadsHistory.getStatusId() == null)
					pst.setNull(2, Types.NULL);
				else
					pst.setInt(2, leadsHistory.getStatusId());
				if (leadsHistory.getDescription() == null)
					pst.setNull(3, Types.NULL);
				else
					pst.setString(3, leadsHistory.getDescription());
				if (leadsHistory.getNotInterestedReason() == null)
					pst.setNull(4, Types.NULL);
				else
					pst.setString(4, leadsHistory.getNotInterestedReason());
				if (leadsHistory.getStoreId() == null)
					pst.setNull(5, Types.NULL);
				else
					pst.setInt(5,
							Integer.parseInt(leadsHistory.getStoreId() + ""));
				if (leadsHistory.getStoreMapId() == null)
					pst.setNull(6, Types.NULL);
				else
					pst.setInt(6, Integer
							.parseInt(leadsHistory.getStoreMapId() + ""));
				if (leadsHistory.getSourceId() == null)
					pst.setNull(7, Types.NULL);
				else
					pst.setInt(7,
							Integer.parseInt(leadsHistory.getSourceId() + ""));
				if (leadsHistory.getComments() == null)
					pst.setNull(8, Types.NULL);
				else
					pst.setString(8, leadsHistory.getComments() + "");
				return pst;
			}
		}, kh);

		leadsHistory.setId(kh.getKey().longValue());

		return leadsHistory.getId();
	}

	@Override
	public Long insertIntoLeadExtra(final LeadsExtra leadsExtra) {
		final String query = "insert into lead_extra_details (lead_id,facebook_form_id,fb_unique_lead_id,missed_call,units,call_center_update,cid,utm_source,utm_campaign,utm_medium,utm_content,created_date,modified_date, days) values (?,?,?,?,?,?,?,?,?,?,?,now(),?,?);";
		KeyHolder kh = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement pst = con.prepareStatement(query,
						Statement.RETURN_GENERATED_KEYS);
				if (leadsExtra.getLead_id() == null)
					pst.setNull(1, Types.NULL);
				else
					pst.setLong(1, leadsExtra.getLead_id());
				if (leadsExtra.getFacebook_form_id() == null)
					pst.setNull(2, Types.NULL);
				else
					pst.setInt(2, leadsExtra.getFacebook_form_id());
				if (leadsExtra.getFb_unique_lead_id() == null)
					pst.setNull(3, Types.NULL);
				else
					pst.setString(3, leadsExtra.getFb_unique_lead_id());
				if (leadsExtra.getMissed_call() == null)
					pst.setNull(4, Types.NULL);
				else
					pst.setString(4, leadsExtra.getMissed_call());
				if (leadsExtra.getUnits() == null)
					pst.setNull(5, Types.NULL);
				else
					pst.setString(5, leadsExtra.getUnits());
				if (leadsExtra.getCall_center_update() == null)
					pst.setNull(6, Types.NULL);
				else
					pst.setInt(6, leadsExtra.getCall_center_update());
				if (leadsExtra.getCid() == null)
					pst.setNull(7, Types.NULL);
				else
					pst.setString(7, leadsExtra.getCid());
				if (leadsExtra.getUtm_source() == null)
					pst.setNull(8, Types.NULL);
				else
					pst.setString(8, leadsExtra.getUtm_source());
				if (leadsExtra.getUtm_campaign() == null)
					pst.setNull(9, Types.NULL);
				else
					pst.setString(9, leadsExtra.getUtm_campaign());
				if (leadsExtra.getUtm_medium() == null)
					pst.setNull(10, Types.NULL);
				else
					pst.setString(10, leadsExtra.getUtm_medium());
				if (leadsExtra.getUtm_content() == null)
					pst.setNull(11, Types.NULL);
				else
					pst.setString(11, leadsExtra.getUtm_content());
				if (leadsExtra.getModified_date() == null)
					pst.setNull(12, Types.NULL);
				else
					pst.setString(12, leadsExtra.getModified_date());
				if (leadsExtra.getDays() == null)
					pst.setNull(13, Types.NULL);
				else
					pst.setString(13, leadsExtra.getDays());
				return pst;
			}
		}, kh);

		final long leadHistoryId = kh.getKey().longValue();
		return leadHistoryId;
	}

	public boolean updateLeads(Leads leads, long leadId) {
		try {
			StringBuilder query = new StringBuilder("update leads set ");
			Map<String, Object> params = new HashMap<String, Object>();

			updateQuery(query, "first_name", leads.getFirst_name(), params);
			updateQuery(query, "last_name", leads.getLast_name(), params);
			updateQuery(query, "mobile_no", leads.getMobile_no(), params);
			updateQuery(query, "mobile_no2", leads.getMobile_no2(), params);
			updateQuery(query, "email_id", leads.getEmail_id(), params);
			updateQuery(query, "landline_no1", leads.getLandline_no1(), params);
			updateQuery(query, "address", leads.getAddress(), params);
			updateQuery(query, "pincode", leads.getPincode(), params);
			updateQuery(query, "facebook_id", leads.getFacebook_id(), params);
			updateQuery(query, "twitter_id", leads.getTwitter_id(), params);
			updateQuery(query, "circle_id", leads.getCircle_id(), params);
			updateQuery(query, "city_id", leads.getCity_id(), params);
			updateQuery(query, "company_name", leads.getCompany_name(), params);

			if ("Y".equals(leads.getEncoded()))
				query.append("encoded='Y',");

			query .append("modified_date=now() where id=:id");
			params.put("id", leadId);

			NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(
					getDataSource());

			namedTemplate.update(query.toString(), params);

			return true;
		} catch (UncategorizedSQLException e) {
			logger.error("update leads has failed.", e);
			if (e.getMessage().contains("Incorrect string value")) {
				logger.info("retrying to save with hex values");

				leads.setFirst_name(convertToHexString(leads.getFirst_name()));
				leads.setLast_name(convertToHexString(leads.getLast_name()));
				leads.setMobile_no(convertToHexString(leads.getMobile_no()));
				leads.setEmail_id(convertToHexString(leads.getEmail_id()));
				leads.setPincode(convertToHexString(leads.getPincode()));
				leads.setEncoded("Y");

				return updateLeads(leads, leadId);
			}
		}

		return false;
	}

	private StringBuilder updateQuery(StringBuilder query, String colName, Object value,
			Map<String, Object> params) {
		if (value != null) {
			query.append(colName).append("=:").append(colName).append(",");
			params.put(colName, value);
		}
		return query;
	}

	@Override
	public boolean updateLeads(Leads leads, int lead_id) {
		return updateLeads(leads, (long) lead_id);
	}

	@Override
	public Integer getLeadsStoreMappingIdByLeadId(Integer ifIDExist,
			Long storeId, Integer productId, String string) {
		return getLeadsStoreMappingIdByLeadId((long) ifIDExist, storeId,
				productId, string);
	}

	@Override
	public Integer getLeadsStoreMappingIdByLeadId(Long ifIDExist, Long storeId,
			Integer productId, String string) {
		String query = "select id from lead_store_mapping where lead_id="
				+ ifIDExist + " and store_id=" + storeId + " and product_id="
				+ productId + " and status_id in " + string + " limit 1 ;";
		try {
			return getJdbcTemplate().queryForObject(query, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public int updateLeadsExtra(LeadsExtra leadExtra, long lead_id) {
		StringBuilder query = new StringBuilder("update lead_extra_details set ");
		Map<String, Object> params = new HashMap<String, Object>();
		
		updateQuery(query, "lead_id", leadExtra.getLead_id(), params);
		updateQuery(query, "facebook_form_id", leadExtra.getFacebook_form_id(), params);
		updateQuery(query, "fb_unique_lead_id", leadExtra.getFb_unique_lead_id(), params);
		updateQuery(query, "missed_call", leadExtra.getMissed_call(), params);
		updateQuery(query, "units", leadExtra.getUnits(), params);
		updateQuery(query, "call_center_update", leadExtra.getCall_center_update(), params);
		updateQuery(query, "cid", leadExtra.getCid(), params);
		updateQuery(query, "utm_source", leadExtra.getUtm_source(), params);
		updateQuery(query, "utm_campaign", leadExtra.getUtm_campaign(), params);
		updateQuery(query, "utm_medium", leadExtra.getUtm_medium(), params);
		updateQuery(query, "utm_content", leadExtra.getUtm_content(), params);
		
		query.append(" modified_date=now() where lead_id=:id ");
		params.put("id", lead_id);
		
		if(StringUtil.isValid(leadExtra.getFb_unique_lead_id())) {
			query.append(" and fb_unique_lead_id=:leadgenid ");
			params.put("leadgenid", leadExtra.getFb_unique_lead_id());		
		}
		
		NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(getDataSource());
		return namedTemplate.update(query.toString(), params);
	}

	@Override
	public boolean updateLeadsExtra(LeadsExtra leadExtra, int lead_id) {
		return updateLeadsExtra(leadExtra, (long) lead_id) > 0;
	}

	@Override
	public String getPromoterMobileNumber(Long storeId) {
		String query = " select  mobile_no from user_details where user_type_id=22 and store_id=?  ";
		try {
			return getJdbcTemplate().queryForObject(query,
					new Object[]{storeId}, String.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void insertintoLeadExtraDyanamic(final LeadsExtra leadsExtra,
			final Map<String, String> parsedValues) {
		final StringBuffer insertCol = new StringBuffer();
		final StringBuffer placeholder = new StringBuffer();

		final Map<String, String> data = new HashMap<String, String>();
		data.put("lead_id", "lead_id");
		data.put("facebook_form_id", "facebook_form_id");
		data.put("fb_unique_lead_id", "fb_unique_lead_id");
		data.put("missed_call", "missed_call");
		data.put("units", "units");
		data.put("call_center_update", "call_center_update");
		data.put("cid", "cid");
		data.put("utm_source", "utm_source");
		data.put("utm_campaign", "utm_campaign");
		data.put("utm_medium", "utm_medium");
		data.put("utm_content", "utm_content");
		data.put("created_date", "created_date");
		data.put("modified_date", "modified_date");
		data.put("days", "days");

		final Map<String, String> columns = getcols();

		for (Map.Entry<String, String> entry : columns.entrySet()) {
			if (parsedValues.containsKey(entry.getKey())
					&& !data.containsKey(entry.getKey())) {
				insertCol.append(",").append(entry.getValue());
				placeholder.append(",?");
			}
		}

		final String query = "insert into lead_extra_details (lead_id,facebook_form_id,fb_unique_lead_id,missed_call,units,call_center_update,cid,utm_source,utm_campaign,utm_medium,utm_content,created_date,modified_date, days  "
				+ insertCol.toString()
				+ ") values (?,?,?,?,?,?,?,?,?,?,?,now(),?,? "
				+ placeholder.toString() + ");";
		KeyHolder kh = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement pst = con.prepareStatement(query,
						Statement.RETURN_GENERATED_KEYS);
				if (leadsExtra.getLead_id() == null)
					pst.setNull(1, Types.NULL);
				else
					pst.setLong(1, leadsExtra.getLead_id());
				if (leadsExtra.getFacebook_form_id() == null)
					pst.setNull(2, Types.NULL);
				else
					pst.setInt(2, leadsExtra.getFacebook_form_id());
				if (leadsExtra.getFb_unique_lead_id() == null)
					pst.setNull(3, Types.NULL);
				else
					pst.setString(3, leadsExtra.getFb_unique_lead_id());
				if (leadsExtra.getMissed_call() == null)
					pst.setNull(4, Types.NULL);
				else
					pst.setString(4, leadsExtra.getMissed_call());
				if (leadsExtra.getUnits() == null)
					pst.setNull(5, Types.NULL);
				else
					pst.setString(5, leadsExtra.getUnits());
				if (leadsExtra.getCall_center_update() == null)
					pst.setNull(6, Types.NULL);
				else
					pst.setInt(6, leadsExtra.getCall_center_update());
				if (leadsExtra.getCid() == null)
					pst.setNull(7, Types.NULL);
				else
					pst.setString(7, leadsExtra.getCid());
				if (leadsExtra.getUtm_source() == null)
					pst.setNull(8, Types.NULL);
				else
					pst.setString(8, leadsExtra.getUtm_source());
				if (leadsExtra.getUtm_campaign() == null)
					pst.setNull(9, Types.NULL);
				else
					pst.setString(9, leadsExtra.getUtm_campaign());
				if (leadsExtra.getUtm_medium() == null)
					pst.setNull(10, Types.NULL);
				else
					pst.setString(10, leadsExtra.getUtm_medium());
				if (leadsExtra.getUtm_content() == null)
					pst.setNull(11, Types.NULL);
				else
					pst.setString(11, leadsExtra.getUtm_content());
				if (leadsExtra.getModified_date() == null)
					pst.setNull(12, Types.NULL);
				else
					pst.setString(12, leadsExtra.getModified_date());
				if (leadsExtra.getDays() == null)
					pst.setNull(13, Types.NULL);
				else
					pst.setString(13, leadsExtra.getDays());
				Integer counter = 14;
				for (Map.Entry<String, String> entry : columns.entrySet()) {
					if (parsedValues.containsKey(entry.getKey())
							&& !data.containsKey(entry.getKey())) {
						pst.setString(counter++, parsedValues.get(entry.getKey()));
					}
				}
				return pst;
			}
		}, kh);
	}

	private Map<String, String> getcols() {
		String queryForCols = "select *from lead_extra_details limit 1";

		final Map<String, String> cols = new HashMap<>();
		getJdbcTemplate().query(queryForCols,
				new ResultSetExtractor<Integer>() {
					@Override
					public Integer extractData(ResultSet rs)
							throws SQLException, DataAccessException {

						ResultSetMetaData rsmd = rs.getMetaData();
						int columnCount = rsmd.getColumnCount();
						for (int i = 1; i <= columnCount; i++) {
							String name = rsmd.getColumnName(i);
							cols.put(name, name);
						}
						return columnCount;
					}
				});

		return cols;
	}

	@Override
	public Map<String, String> getLeadExtrasCols() {
		// TODO Auto-generated method stub
		String queryForCols = "select * from lead_extra_details led join leads l on l.id=led.lead_id join book_demo_details bdm on bdm.lead_id=l.id limit 1";

		final Map<String, String> cols = new HashMap<>();
		getJdbcTemplate().query(queryForCols,
				new ResultSetExtractor<Integer>() {

					@Override
					public Integer extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						ResultSetMetaData rsmd = rs.getMetaData();
						int columnCount = rsmd.getColumnCount();
						for (int i = 1; i <= columnCount; i++) {
							String tableName = rsmd.getTableName(i),
									colName = rsmd.getColumnName(i);

							cols.put(tableName + "." + colName, colName);
						}
						return columnCount;
					}
				});

		return cols;
	}

	@Override
	public Integer getCityId(String city) {
		String cityQuery = "select id from cities where name='" + city
				+ "' limit 1";
		Integer id = null;
		try {
			id = getJdbcTemplate().queryForObject(cityQuery, Integer.class);
			return id;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Long insertIntocxLeads(final Leads leads, final int prod_id) {

		final String query = "insert into cx_data (source_Id,customer_name,mobile_no,email,created_date,trigger_status)"
				+ " values (?,?,?,?,now(),null)";
		KeyHolder kh = new GeneratedKeyHolder();

		getJdbcTemplate().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				int index = 1;
				PreparedStatement pst = con.prepareStatement(query,
						Statement.RETURN_GENERATED_KEYS);

				pst.setInt(index++, leads.setSource_id(prod_id));
				if (leads.getFirst_name() == null)
					pst.setNull(index++, Types.NULL);
				else
					pst.setString(index++, leads.getFirst_name());
				if (leads.getMobile_no() == null)
					pst.setNull(index++, Types.NULL);
				else
					pst.setString(index++, leads.getMobile_no());
				if (leads.getEmail_id() == null)
					pst.setNull(index++, Types.NULL);
				else
					pst.setString(index++, leads.getEmail_id());
				return pst;
			}
		}, kh);

		return kh.getKey().longValue();
	}

	@Override
	public LeadVo getLeadById(Long id) {
		String query = concate(
				"select l.id  as id,lsm.id as leadStoreMapId, s.display_name as  source,sta.name as verified, ",
				"first_name as name,  l.mobile_no as mobileNo,  email_id as emailId, c.name as city,",
				"d.name as storeName,  st.name as statusName,st.id as statusId, lsm.created_date as createdDate, ccd.model as ",
				"model, p.name as productName,lsm.mod_date as modifiedDate,ur.un as userName,lsm.",
				"call_later_date_time as call_later_date_time ,lsm.demo_date_time as demo_date_time, l.pincode as pincode ",
				"from leads l ",
				"left join lead_store_mapping lsm on lsm.lead_id=l.id ",
				"left join sources s on s.id=lsm.source_id ",
				"left join tbl_dealer d on d.storeId=lsm.store_id ",
				"left join call_center_details ccd on ccd.lead_id=l.id  ",
				"left join products p on p.id=s.product_id ",
				"left join cities c on c.id=l.city_id ",
				"left join statuses st on st.id=lsm.status_id  ",
				"left join lead_history lh on lh.lead_store_map_id=lsm.id ",
				"left join users ur on ur.id=lh.user_id ",
				"left join statuses sta on sta.id=l.verification_id ",
				"where l.id=? group by lsm.id order by lsm.id desc limit 1");

		try {
			return getJdbcTemplate().queryForObject(query, new Object[]{id},
					new BeanPropertyRowMapper<LeadVo>(LeadVo.class));
		} catch (EmptyResultDataAccessException e) {
			logger.error("getLeadById failed", e);
		}

		return null;

	}

	@Override
	public Map<String, String> getFBPreMappedFields() {
		final Map<String, String> masterFormFields = new HashMap<String, String>();

		String query = "select name,col_name from fb_form_fields";
		getJdbcTemplate().query(query, new RowMapper<Object>() {

			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				String question = rs.getString(1);
				String dbColumn = rs.getString(2);
				String oldDBColumn = masterFormFields.get(question);
				dbColumn = oldDBColumn == null
						? dbColumn
						: concate(oldDBColumn, ",", dbColumn);
				masterFormFields.put(question, dbColumn);
				return null;
			}

		});
		return masterFormFields;
	}

	@Override
	public void updateBookADemoRef(Long refId, String interested, Long leadId,Integer status) {

		if (refId != null && status != null) {
			String updateLeadsQuery = " update lead_store_mapping set bookademo_interest=?,bookademo_customer_referenceId=?,call_later_date_time=?,status_id=? where lead_id=? ";
			getJdbcTemplate().update(updateLeadsQuery, interested, refId,null,status, leadId);

		} else {

			String updateLeadsQuery = " update lead_store_mapping set bookademo_interest=?,call_later_date_time=?,status_id=?  where lead_id=? ";
			getJdbcTemplate().update(updateLeadsQuery, interested,null,status,leadId);
		}

	}

	@Override
	public Integer getLeadsStatusIdByLeadId(Integer storeMapId) {

		String queryForCityId = "select status_id from lead_store_mapping where id=? ";

		try {
			return getJdbcTemplate().queryForObject(queryForCityId, new Object[] { storeMapId },
					Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.info("status id is null");
			return null;
		}
	}

}
