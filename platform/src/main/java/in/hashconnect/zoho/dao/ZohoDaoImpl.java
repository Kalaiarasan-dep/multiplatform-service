package in.hashconnect.zoho.dao;

import static org.apache.commons.collections4.MapUtils.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.StringUtil;
import in.hashconnect.zoho.vo.CreateCustomerRequest;
import in.hashconnect.zoho.vo.CreateInvoiceRequest;
import in.hashconnect.zoho.vo.CreatePaymentRequest;
import in.hashconnect.zoho.vo.RefundPaymentRequest;

@SuppressWarnings("unchecked")
public class ZohoDaoImpl extends JdbcDaoSupport implements ZohoDao {
	@Override
	public Map<String, Object> getZohoApiDetails(String string) {
		String query = "select url,client_id,client_secret,refresh_token,organization_id,access_token from zoho_apis where api=?";
		return getJdbcTemplate().queryForMap(query, string);
	}

	@Override
	public void saveZohoItems(final List<Map<String, java.lang.Object>> items) {
		String query = "insert into zoho_items(item_id,sku,name,item_name,status,description,rate,product_type,stock_on_hand,created_ts,hc_product_type,hsn_or_sac,details) values(?,?,?,?,?,?,?,?,?,now(),?,?,?)";
		getJdbcTemplate().batchUpdate(query, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Map<String, Object> item = items.get(i);
				int idx = 1;

				ps.setString(idx++, getString(item, "item_id"));
				ps.setString(idx++, getString(item, "sku"));
				ps.setString(idx++, getString(item, "name"));
				ps.setString(idx++, getString(item, "item_name"));
				ps.setString(idx++, getString(item, "status"));
				ps.setString(idx++, getString(item, "description"));
				ps.setString(idx++, getString(item, "rate"));
				ps.setString(idx++, getString(item, "product_type"));
				ps.setString(idx++, getString(item, "stock_on_hand"));
				ps.setString(idx++, findCustomFieldValue(item, "cf_hc_product_id"));
				ps.setString(idx++, getString(item, "hsn_or_sac"));
				ps.setString(idx++, JsonUtil.toString(item));
			}

			@Override
			public int getBatchSize() {
				return items.size();
			}
		});
	}

	@Override
	public void updateZohoItems(final List<Map<String, java.lang.Object>> items) {
		String query = "update zoho_items set sku=?,name=?,item_name=?,status=?,description=?,rate=?,product_type=?,stock_on_hand=?,hc_product_type=?,hsn_or_sac=?,details=? where item_id=?";
		getJdbcTemplate().batchUpdate(query, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Map<String, Object> item = items.get(i);
				int idx = 1;
				ps.setString(idx++, getString(item, "sku"));
				ps.setString(idx++, getString(item, "name"));
				ps.setString(idx++, getString(item, "item_name"));
				ps.setString(idx++, getString(item, "status"));
				ps.setString(idx++, getString(item, "description"));
				ps.setString(idx++, getString(item, "rate"));
				ps.setString(idx++, getString(item, "product_type"));
				ps.setString(idx++, getString(item, "stock_on_hand"));
				ps.setString(idx++, findCustomFieldValue(item, "cf_hc_product_id"));
				ps.setString(idx++, getString(item, "hsn_or_sac"));
				ps.setString(idx++, JsonUtil.toString(item));
				ps.setString(idx++, getString(item, "item_id"));
			}

			@Override
			public int getBatchSize() {
				return items.size();
			}
		});
	}

	private String findCustomFieldValue(Map<String, Object> item, String key) {
		String value = getString(item, key);
		if (!StringUtil.isValid(value)) {
			Map<String, Object> customFields = (Map<String, Object>) getMap(item, "custom_field_hash");
			if (customFields != null && !customFields.isEmpty()) {
				value = getString(customFields, key);
			}
		}
		return value;

	}

	@Override
	public void updateZohoToken(String accessToken) {
		String query = "update zoho_apis set access_token=?,access_token_crt_ts=now()";
		getJdbcTemplate().update(query, accessToken);
	}

	public void createCustomer(CreateCustomerRequest request, Map<String, Object> response) {
		if (request.getCustomerId() == null) {
			String query = "insert into zoho_customer(reg_id,adv_req_id,request,created_ts)values(?,?,?,now())";
			KeyHolder keyHolder = new GeneratedKeyHolder();
			getJdbcTemplate().update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					int index = 1;
					PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
					ps.setLong(index++, request.getRegId());
					ps.setLong(index++, request.getAdvReqId());
					ps.setString(index++, JsonUtil.toString(request));
					return ps;
				}
			}, keyHolder);

			request.setCustomerId(keyHolder.getKey().longValue());
			return;
		}

		String query = "update zoho_customer set customer_id=?,name=?,gst=?,contact_id=?,email=?,mobile=?,place_of_contact=?,response=? where id=?";
		getJdbcTemplate().update(query, getString(response, "contact_id"), getString(response, "contact_name"),
				getString(response, "gst_no"), getString(response, "primary_contact_id"), getString(response, "email"),
				getString(response, "mobile"), getString(response, "place_of_contact"), JsonUtil.toString(response),
				request.getCustomerId());

	}

	@Override
	public void createInvoice(CreateInvoiceRequest request, Map<String, Object> response) {
		if (request.getInvoiceId() == null) {
			String query = "insert into zoho_invoice(adv_req_id,customer_id,request,reg_offer_id,pay_id,created_ts)values(?,?,?,?,?,now())";

			KeyHolder keyHolder = new GeneratedKeyHolder();
			getJdbcTemplate().update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					int index = 1;
					PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
					ps.setLong(index++, request.getAdvReqId());
					ps.setLong(index++, request.getDbCustomerId());
					ps.setString(index++, JsonUtil.toString(request));
					ps.setLong(index++, request.getRegOfferId());
					ps.setLong(index++, request.getPayId());
					return ps;
				}
			}, keyHolder);

			request.setInvoiceId(keyHolder.getKey().longValue());
			return;
		}
		String json = JsonUtil.toString(response);
		String payMapStatus = json != null ? "PENDING" : null;
		String approved = "approve".equals(request.getNext_action()) ? "Y" : null;
		String query = "update zoho_invoice set invoice_id=?,response=?,pay_map_status=?,approved=? where id=?";
		getJdbcTemplate().update(query, getString(response, "invoice_id"), json, payMapStatus, approved,
				request.getInvoiceId());

	}

	@Override
	public void createPayment(CreatePaymentRequest request, Map<String, Object> response) {
		if (request.getPayId() == null) {
			String query = "insert into zoho_payment(adv_req_id,customer_id,request,created_ts)values(?,?,?,now())";

			KeyHolder keyHolder = new GeneratedKeyHolder();
			getJdbcTemplate().update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					int index = 1;
					PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
					ps.setLong(index++, request.getAdvReqId());
					ps.setString(index++, request.getCustomer_id());
					ps.setString(index++, JsonUtil.toString(request));
					return ps;
				}
			}, keyHolder);

			request.setPayId(keyHolder.getKey().longValue());
			return;
		}

		String query = "update zoho_payment set payment_id=?, response=? where id=?";
		getJdbcTemplate().update(query, getString(response, "payment_id"), JsonUtil.toString(response),
				request.getPayId());
	}

	@Override
	public void updatePayment(CreatePaymentRequest request, Map<String, Object> response) {
		if (request.getUpdatePaymentId() == null) {
			String query = "insert into zoho_update_payment(pay_id,request,invoice_id,created_ts)values(?,?,?,now())";
			KeyHolder keyHolder = new GeneratedKeyHolder();
			getJdbcTemplate().update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					int index = 1;
					PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
					ps.setLong(index++, request.getPayId());
					ps.setString(index++, JsonUtil.toString(request));
					ps.setLong(index++, request.getInvoiceId());
					return ps;
				}
			}, keyHolder);

			request.setUpdatePaymentId(keyHolder.getKey().longValue());
			return;
		}

		String query = "update zoho_update_payment set response=? where id=?";
		getJdbcTemplate().update(query, JsonUtil.toString(response), request.getUpdatePaymentId());
	}

	@Override
	public void createRefund(RefundPaymentRequest request, Map<String, Object> response) {
		if (request.getRefundRequestId() == null) {
			String query = "insert into zoho_refund_payment(payment_id,request,created_ts)values(?,?,now())";
			KeyHolder keyHolder = new GeneratedKeyHolder();
			getJdbcTemplate().update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					int index = 1;
					PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
					ps.setString(index++, request.getPaymentId());
					ps.setString(index++, JsonUtil.toString(request));
					return ps;
				}
			}, keyHolder);

			request.setRefundRequestId(keyHolder.getKey().longValue());
			return;
		}

		String query = "update zoho_refund_payment set response=? where id=?";
		getJdbcTemplate().update(query, JsonUtil.toString(response), request.getRefundRequestId());

	}

	@Override
	public void updateCustomer(CreateCustomerRequest request, Map<String, Object> response) {
		String query = "insert into zoho_update_customer(customer_id,request,response,created_ts)values(?,?,?,now())";
		getJdbcTemplate().update(query, request.getCustomerId(), JsonUtil.toString(request),
				JsonUtil.toString(response));

		getJdbcTemplate().update("update zoho_customer set contact_id=?,place_of_contact=?,response=? where id=?",
				getString(response, "primary_contact_id"), request.getPlace_of_contact(), JsonUtil.toString(response),
				request.getCustomerId());
	}

	@Override
	public void updateInvoice(CreateInvoiceRequest request, Map<String, Object> response) {
		if (request.getUpdateInvoiceId() == null) {
			String query = "insert into zoho_update_invoice(invoice_id,request,created_ts)values(?,?,now())";
			KeyHolder keyHolder = new GeneratedKeyHolder();
			getJdbcTemplate().update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					int index = 1;
					PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
					ps.setLong(index++, request.getInvoiceId());
					ps.setString(index++, JsonUtil.toString(request));
					return ps;
				}
			}, keyHolder);

			request.setUpdateInvoiceId(keyHolder.getKey().longValue());
			return;
		}

		String query = "update zoho_update_invoice set response=? where id=?";
		getJdbcTemplate().update(query, JsonUtil.toString(response), request.getUpdateInvoiceId());

	}

	@Override
	public void createCreditNote(CreateInvoiceRequest request, Map<String, Object> response) {
		if (request.getCreditNoteId() == null) {
			// save it
			String query = "insert into zoho_credit_note(invoice_id,request,created_ts)values(?,?,now())";

			KeyHolder keyHolder = new GeneratedKeyHolder();
			getJdbcTemplate().update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					int index = 1;
					PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
					ps.setLong(index++, request.getInvoiceId());
					ps.setString(index++, JsonUtil.toString(request));
					return ps;
				}
			}, keyHolder);

			Long id = keyHolder.getKey().longValue();
			request.setCreditNoteId(id);
			return;
		}
		String query = "update zoho_credit_note set response=? where id=?";
		getJdbcTemplate().update(query, JsonUtil.toString(response), request.getCreditNoteId());

	}

	@Override
	public Long deletePaymentFromInvoice(Long invoiceDeletePaymentId, Long invoiceId, String zohoInvoiceId,
			String zohoInvoicePaymentId, Map<String, Object> responseMap) {
		if (invoiceDeletePaymentId == null) {
			String query = "insert into zoho_invoice_delete_payment(invoice_id,zoho_invoice_id,zoho_invoice_payment_id,created_ts)values(?,?,?,now())";

			KeyHolder keyHolder = new GeneratedKeyHolder();
			getJdbcTemplate().update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					int index = 1;
					PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
					ps.setLong(index++, invoiceId);
					ps.setString(index++, zohoInvoiceId);
					ps.setString(index++, zohoInvoicePaymentId);
					return ps;
				}
			}, keyHolder);

			return keyHolder.getKey().longValue();
		}
		String query = "update zoho_invoice_delete_payment set response=? where id=?";
		getJdbcTemplate().update(query, JsonUtil.toString(responseMap), invoiceDeletePaymentId);

		return invoiceDeletePaymentId;
	}

	public void updateCustomer(Long customerId, Map<String, Object> response) {
		String query = "update zoho_customer set customer_id=?,name=?,gst=?,contact_id=?,email=?,mobile=?,place_of_contact=?,response=? where id=?";
		getJdbcTemplate().update(query, getString(response, "contact_id"), getString(response, "contact_name"),
				getString(response, "gst_no"), getString(response, "primary_contact_id"), getString(response, "email"),
				getString(response, "mobile"), getString(response, "place_of_contact"), JsonUtil.toString(response),
				customerId);
	}

	@Override
	public boolean canDownloadReviews() {
		try {
			return getJdbcTemplate().queryForObject(
					"select DATE_FORMAT(created_ts, '%Y-%m-%d') < DATE_FORMAT(NOW(), '%Y-%m-%d') download from zoho_items LIMIT 1",
					Integer.class) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}

	}

	@Override
	public void truncateZohoItem() {
		getJdbcTemplate().update("truncate table zoho_items");
	}

	@Override
	public void deletePaymentFromCustomer(Long paymentId) {
		String query = "update zoho_payment set deleted='Y' where id=?";
		getJdbcTemplate().update(query, paymentId);

	}

	@Override
	public boolean invoiceRefreshRequired(String invId) {
		int count = getJdbcTemplate().queryForObject(
				"select count(*) from zoho_invoice where invoice_id=? "
						+ "and ifnull(invoice_url_last_refresh, created_ts) < date_sub(now(), interval 1 month)",
				Integer.class, invId);

		return count > 0;
	}

	@Override
	public void updateInvoiceUrl(String invId, String url) {
		getJdbcTemplate().update(
				"update zoho_invoice set invoice_url=?,invoice_url_last_refresh=now() where invoice_id=?", url, invId);
	}
}
