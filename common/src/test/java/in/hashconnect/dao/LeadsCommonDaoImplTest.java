package in.hashconnect.dao;

import javax.sql.DataSource;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import in.hashconnect.common.beans.Leads;
import in.hashconnect.common.beans.LeadsExtra;
import in.hashconnect.common.daoImpl.LeadCommonDaoImpl;

@RunWith(MockitoJUnitRunner.class)
public class LeadsCommonDaoImplTest {

	LeadCommonDaoImpl dao = new LeadCommonDaoImpl();
	@Mock
	DataSource ds = null;

	@Test@Ignore
	public void testUploadLeadQuery() {
		dao.setDataSource(ds);

		Leads l = new Leads();
		l.setFirst_name("testFN");
		l.setLast_name("testLN");
		l.setEncoded("Y");
		dao.updateLeads(l, 100L);
	}

	@Test
	public void testUploadLeadExtrasQuery() {
		dao.setDataSource(ds);

		LeadsExtra l = new LeadsExtra();
		l.setLead_id(100L);
		l.setUnits("1");
		dao.updateLeadsExtra(l, 100L);
	}
}
