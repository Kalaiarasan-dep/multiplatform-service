package in.hashconnect.notification.service.impl;

import java.util.Collections;
import java.util.Map;

import javax.mail.Message;
import javax.mail.Transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import in.hashconnect.notification.properties.EmailGatewayProperties;

public class SMTPEmailModeServiceImpl implements EmailModeService {
	private final Logger logger = LoggerFactory.getLogger(SMTPEmailModeServiceImpl.class);

	@Autowired
	private EmailGatewayProperties gatewayProperties;

	@Override
	public Map<String, String> send(Message message) throws Exception {
		logger.debug("email - SMTP");

		Transport transport = message.getSession().getTransport();

		transport.connect(gatewayProperties.getHost(), gatewayProperties.getUn(), gatewayProperties.getPw());

		transport.sendMessage(message, message.getAllRecipients());

		logger.debug("email - sent");

		return Collections.emptyMap();
	}

}
