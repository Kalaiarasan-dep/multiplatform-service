package in.hashconnect.controller;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import in.hashconnect.admin.dao.PartnerDao;

@SpringBootTest
@AutoConfigureMockMvc
public class InvoiceValidationControllerTest {

	@Autowired
    MockMvc mockMvc;
	
	@MockBean
	private PartnerDao partnerDao;
		
	@Test
	public void testValidateInvId() throws Exception {
		when(partnerDao.isInvoiceExistInFinancialYrForPartner(ArgumentMatchers.anyString(), 
				ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong())).thenReturn(Boolean.TRUE);
		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.
				post("/inv-req/validate-invid").param("invid", "3000").param("partner_id", "12"));
		response.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
				

	}

}
