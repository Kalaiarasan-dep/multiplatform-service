package in.hashconnect.common.dao;

import java.util.Map;

import in.hashconnect.common.beans.LeadVo;
import in.hashconnect.common.beans.Leads;
import in.hashconnect.common.beans.LeadsExtra;
import in.hashconnect.common.beans.LeadsHistory;
import in.hashconnect.common.beans.LeadsStoreMapping;

public interface LeadCommonDao {
	public Long leadAlreadyExistByMobile(String mobileNo);

	public Integer checkMobileExistInLeads(String mobile_no);

	public void insertIntoLeadSourceMapping(int leadId, int source_id);

	public void insertIntoLeadSourceMapping(long leadId, int source_id);

	public String getPromoterMobileNumber(Long storeId);

	public Long insertIntoLeads(Leads leads);

	public Long insertIntoLeadStoreMapping(LeadsStoreMapping leadsStoreMapping);

	public Long insertIntoLeadHistory(LeadsHistory leadsHistory);

	public Long insertIntoLeadExtra(LeadsExtra leadsExtra);

	public boolean updateLeads(Leads leads, int lead_id);

	public boolean updateLeads(Leads leads, long lead_id);

	public Integer getLeadsStoreMappingIdByLeadId(Integer leadId, Long storeId,
			Integer productId, String string);

	public Integer getLeadsStoreMappingIdByLeadId(Long leadId, Long storeId,
			Integer productId, String string);

	public boolean updateLeadsExtra(LeadsExtra leadExtra, int lead_id);

	public int updateLeadsExtra(LeadsExtra leadExtra, long lead_id);

	public void insertintoLeadExtraDyanamic(LeadsExtra leadsExtra,
			Map<String, String> parsedValues);

	public Map<String, String> getLeadExtrasCols();

	public Integer getCityId(String city);

	public Long insertIntocxLeads(Leads leads, int prod_id);

	public Long getByMobile(String mobile);

	LeadVo getLeadById(Long id);

	public Map<String, String> getFBPreMappedFields();
	
	public void updateBookADemoRef(Long refId,String interested,Long leadId,Integer status);
	
	public Integer getLeadsStatusIdByLeadId(Integer storeMapId);

	void leadHistoryChecks(Long leadId, Long storeId);

}
