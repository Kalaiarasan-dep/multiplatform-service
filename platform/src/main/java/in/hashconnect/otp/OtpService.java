package in.hashconnect.otp;

import in.hashconnect.otp.vo.Request;
import in.hashconnect.otp.vo.Response;

public interface OtpService {

	Response generate(Request request);
	
	Response get(Request request);

	Response validate(Request request);

}
