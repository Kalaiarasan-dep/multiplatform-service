package in.hashconnect.common.service;

import static in.hashconnect.common.util.StringUtil.concate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.hashconnect.common.beans.LeadVo;
import in.hashconnect.common.beans.Leads;
import in.hashconnect.common.beans.LeadsHistory;
import in.hashconnect.common.beans.LeadsStoreMapping;
import in.hashconnect.common.dao.LeadCommonDao;
import in.hashconnect.common.util.JsonUtil;
import in.hashconnect.common.util.SettingsUtil;
import in.hashconnect.common.util.StringUtil;
import in.hashconnect.common.util.TimeUtilCommon;
import in.hashconnect.google.GoogleMyBusinessAPI;
import in.hashconnect.google.vo.Notification;

public class LeadServiceImpl implements LeadService {
	private static final Logger logger = LoggerFactory.getLogger(LeadServiceImpl.class);

	@Resource
	private LeadCommonDao leadCommonDao;

	@Resource
	private GoogleMyBusinessAPI googleMyBusinessAPI;

	@Resource
	private SettingsUtil settingsUtil;

	private final Object sync = new Object();

	@Override
	public void saveOrUpdate(Leads lead) {
		logger.info("saveOrUpdate lead " + lead);

		Long leadId = leadCommonDao.getByMobile(lead.getMobile_no());
		if (leadId != null) {
			lead.setId(leadId);
			leadCommonDao.updateLeads(lead, leadId);

			// updating lead extra
			if (lead.getLeadsExtra() != null) {
				lead.getLeadsExtra().setLead_id(leadId);
				int updatedRows = leadCommonDao.updateLeadsExtra(lead.getLeadsExtra(), leadId);
				if (updatedRows == 0) {
					leadCommonDao.insertintoLeadExtraDyanamic(lead.getLeadsExtra(), lead.getParsedValues());
				}
			}
			String lsm_status_tobe_considered_for_dup_check=settingsUtil.getValue("lsm_status_tobe_considered_for_dup_check");
			for (LeadsStoreMapping lsm : lead.getLeadsStoreMapping()) {
				// checking if status is NEW, CALL_LATER, NOT INTERESTED,
				// ALREADY BOUGHT
				if(lsm_status_tobe_considered_for_dup_check==null)
					lsm_status_tobe_considered_for_dup_check="(1,2,4,5,110,111,126,231,236)";
				
				Integer storeMapId = leadCommonDao.getLeadsStoreMappingIdByLeadId(leadId, lsm.getStore_id(),
						lsm.getProd_id(), lsm_status_tobe_considered_for_dup_check);

				Integer LhistoryStatus = 125;

				if (storeMapId != null) {

					Integer statusId = leadCommonDao.getLeadsStatusIdByLeadId(storeMapId);
					
					/*
					 * if the lead already at requested for demo status if user done one more
					 * booking from book a demo . Then update the new reference id to
					 * lead_store_mapping .
					 */
					
					if (lsm.getStatus_id() == 126 && lsm.getBookaDemoRefId() != null) {

						if (statusId == 1 || statusId == 2 || statusId == 126 || statusId == 231 || statusId == 236 ) {

							leadCommonDao.updateBookADemoRef(lsm.getBookaDemoRefId(), lsm.getBookaDemoInterested(),
									leadId, lsm.getStatus_id());


						}

					}

					/*
					 * We have already normal lead with status new , but not opted book a demo
					 * interest, if customer again coming from any landing pages with interest book
					 * a demo then bellow flow
					 */

					else if (statusId == 1 || statusId == 2) {

						if (lsm.getBookaDemoInterested() != null && lsm.getBookaDemoRefId() == null) {

							leadCommonDao.updateBookADemoRef(null, lsm.getBookaDemoInterested(), leadId, 231);

							LhistoryStatus = 231;
						}
					}

					
					LeadsHistory leadsHistory = lsm.getHistoryEntry().setLeadId(leadId).setStoreMapId(storeMapId)
							.setStatusId(LhistoryStatus).setCreatedDate(lead.getCreated_date())
							.setStoreId(lsm.getStore_id());

					leadCommonDao.insertIntoLeadHistory(leadsHistory);
				} else {

			
					// new lead first store mapping then history
					insertNewLeadToStoreMapping(lead, leadId);
				}
			}
		} else {
			synchronized (sync) {
				leadId = leadCommonDao.getByMobile(lead.getMobile_no());
				if (leadId == null) {
					// creating leads as its not found in DB
					lead.setActive_status(104);
					leadId = leadCommonDao.insertIntoLeads(lead);

					// new entry for lead extra
					if (lead.getLeadsExtra() != null) {
						lead.getLeadsExtra().setLead_id(leadId);
						leadCommonDao.insertintoLeadExtraDyanamic(lead.getLeadsExtra(), lead.getParsedValues());
					}
					// new lead first store mapping then history
					insertNewLeadToStoreMapping(lead, leadId);
				}
			}
		}

		// fire fcm message.
		sendNotification(lead);
	}

	private void sendNotification(Leads lead) {
		String appNotificationsEnabled = settingsUtil.getValue("mobile_app_notifications_enabled");
		if (appNotificationsEnabled == null || "false".equals(appNotificationsEnabled)) {
			logger.info("app notifcations are disabled");
			return;
		}
		LeadVo leadVo = leadCommonDao.getLeadById(lead.getId());

		Notification notificationVo = new Notification();
		// set proirity for instant display
		Map<String, String> andriod = new HashMap<String, String>();
		andriod.put("priority", "high");
		notificationVo.setAndroid(andriod);

		// lead data into message
		Map<String, String> leadData = new HashMap<>();
		leadData.put("id", String.valueOf(leadVo.getId()));
		leadData.put("screen_name", "lead_details");
		notificationVo.setData(leadData);

		// notification title and body
		String title = settingsUtil.getValue("partner_app_notification_title");
		String body = leadVo.getName();
		body = StringUtil.isValid(body) ? concate(body, " - ", leadVo.getMobileNo()) : leadVo.getMobileNo();
		Map<String, String> notificationData = new HashMap<>();
		notificationData.put("title", title);
		notificationData.put("body", body);
		notificationData.put("click_action", "FLUTTER_NOTIFICATION_CLICK");
		notificationVo.setNotification(notificationData);

		List<Long> storeIds = new ArrayList<>();
		for (LeadsStoreMapping lsm : lead.getLeadsStoreMapping())
			storeIds.add(lsm.getStore_id());

		notificationVo.setStoreIds(storeIds);

		googleMyBusinessAPI.postNotifications(notificationVo);
	}

	private void insertNewLeadToStoreMapping(Leads lead, Long leadId) {
		// insert into lead_store_mapping
		for (LeadsStoreMapping lsm : lead.getLeadsStoreMapping()) {

			Long storeMapId = leadCommonDao.insertIntoLeadStoreMapping(lsm.setLead_id(leadId));

			// make entry into history
			if (lsm.getHistoryEntry() != null) {
				lsm.getHistoryEntry().setLeadId(leadId).setStoreMapId(storeMapId).setStatusId(lsm.getStatus_id())
						.setStoreId(lsm.getStore_id()).setCreatedDate(lead.getCreated_date());
				leadCommonDao.insertIntoLeadHistory(lsm.getHistoryEntry());
			}
		}
	}

	@Override
	public void sendNotificationCallLater(Leads lead) {
		String appNotificationsEnabled = settingsUtil.getValue("mobile_app_notifications_enabled");
		if (appNotificationsEnabled == null || "false".equals(appNotificationsEnabled)) {
			logger.info("app notifcations are disabled");
			return;
		}

		Notification notificationVo = new Notification();
		// set proirity for instant display
		Map<String, String> andriod = new HashMap<String, String>();
		andriod.put("priority", "high");
		notificationVo.setAndroid(andriod);

		// lead data into message
		Map<String, String> leadData = new HashMap<>();
		leadData.put("notificationCount", lead.getNotificationCount());
		leadData.put("screen_name", "call_later");
		notificationVo.setData(leadData);

		// notification title and body
		String title = settingsUtil.getValue("partner_app_notification_call_later_title");
		String body = lead.getNotificationCount();
		body = StringUtil.isValid(body)
				? concate("You have a " + body, " call back to be done today between ", lead.getDateTime())
				: lead.getDateTime();
		Map<String, String> notificationData = new HashMap<>();
		notificationData.put("title", title);
		notificationData.put("body", body);
		notificationData.put("click_action", "FLUTTER_NOTIFICATION_CLICK");
		notificationVo.setNotification(notificationData);

		List<Long> storeIds = new ArrayList<>();
		storeIds.add(lead.getStoreId().longValue());

		notificationVo.setStoreIds(storeIds);

		googleMyBusinessAPI.postNotifications(notificationVo);

	}

}
