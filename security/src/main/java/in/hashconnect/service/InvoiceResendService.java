package in.hashconnect.service;

import java.util.List;

import in.hashconnect.api.vo.Response;

public interface InvoiceResendService {
	
	Response<List<Integer>> invResend(List<Integer> invIds);

}
