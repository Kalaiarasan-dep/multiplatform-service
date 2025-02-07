package in.hashconnect.notification.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import software.amazon.awssdk.services.ses.model.SendRawEmailResponse;

public class HTTPSEmailModeServiceImpl implements EmailModeService {
	private final Logger logger = LoggerFactory.getLogger(HTTPSEmailModeServiceImpl.class);

	@Resource
	private SesClient sesClient;

	@Override
	public Map<String, String> send(Message message) throws Exception {
		logger.debug("email - HTTPS");

		Map<String, String> data = new HashMap<String, String>(2);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		message.writeTo(outputStream);

		SendRawEmailRequest rawEmailRequest = SendRawEmailRequest.builder()
				.rawMessage(RawMessage.builder().data(SdkBytes.fromByteArray(outputStream.toByteArray())).build())
				.build();

		SendRawEmailResponse response = sesClient.sendRawEmail(rawEmailRequest);

		logger.debug("email - sent with msgId {}", response.messageId());

		data.put("refId", response.messageId());

		return data;
	}
}
