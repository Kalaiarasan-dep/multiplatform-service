package in.hashconnect.notification.service.impl;

import static in.hashconnect.util.StringUtil.*;
import static org.apache.commons.collections4.MapUtils.getString;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import in.hashconnect.notification.properties.EmailGatewayProperties;
import in.hashconnect.notification.service.NotificationService;
import in.hashconnect.notification.service.vo.Attachment;
import in.hashconnect.notification.service.vo.Notification;
import in.hashconnect.util.Constants;
import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.StringUtil;

public class EmailServiceImpl extends AbstractNotificationService implements NotificationService {
	private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Autowired
	private EmailGatewayProperties gatewayProperties;

	@Autowired
	private EmailModeService emailModeService;

	public void process(Notification notification) {
		new Thread(() -> asyncProcess(notification)).start();
	}

	private void asyncProcess(Notification notification) {
		String template = notification.getTemplate();
		String to = notification.getTo();
		String cc = notification.getCc();
		Map<String, Object> reqParams = notification.getParams();
		Attachment attachment = notification.getAttachment();
		// get details by key
		Map<String, Object> params = getTemplateDetailsByKey(template);
		if (params == null) {
			logger.info("invalid temlate {}", template);
			return;
		}

		String subject = notification.getSubject();
		if (!isValid(subject))
			subject = getString(params, "subject");

		String from = notification.getFrom();
		if (!isValid(from))
			from = getString(params, "efrom");

		String displayName = getString(params, "from_display_name");
		if (!StringUtil.isValid(cc))
			cc = getString(params, "cc");

		String body = getBody(template, reqParams);

		// save in db
		Long id = persistNotification(template, EMAIL, to, from, cc, subject, reqParams);

		Map<String, String> response = sendEmail(subject, body, to, from, attachment, null, cc, displayName);

		String status = getString(response, "status");
		String refId = getString(response, "refId");

		// update DB
		updateStatus(id, status, null, refId);
	}

	private Map<String, String> sendEmail(String subject, String body, String to, String from, Attachment attachment,
			String calendarInvite, String cc, String fromDisplayName) {
		if (logger.isInfoEnabled())
			logger.info("sendEmail - to: " + to + ", subject: " + subject);

		Map<String, String> response = new HashMap<String, String>(2);
		response.put("status", Constants.SUCCESS);

		Transport transport = null;

		try {
			Session session = prepareSession();

			// creates a new e-mail message
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from, fromDisplayName));

			// email to
			InternetAddress[] recipientAddress = prepareRecepientList(to);
			if (recipientAddress == null)
				throw new RuntimeException("Missing To Addresses");

			msg.setRecipients(Message.RecipientType.TO, recipientAddress);

			// cc
			recipientAddress = prepareRecepientList(cc);
			if (recipientAddress != null) {
				msg.setRecipients(Message.RecipientType.CC, recipientAddress);
			}

			msg.setSubject(subject);
			msg.setSentDate(new Date());

			Multipart mimePart = new MimeMultipart();

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(body, "text/html; charset=utf-8");
			mimePart.addBodyPart(htmlPart);

			if (attachment != null && attachment.getByteData() != null) {
				MimeBodyPart attach = new MimeBodyPart();
				DataSource dataSource = new ByteArrayDataSource(attachment.getByteData(), attachment.getContentType());
				attach.setDataHandler(new DataHandler(dataSource));
				attach.setFileName(formatFileName(attachment.getContentType(), attachment.getFileName()));
				mimePart.addBodyPart(attach);
			}

			if (calendarInvite != null) {
				MimeBodyPart calendarPart = new MimeBodyPart();
				calendarPart.addHeader("Content-Class", "urn:content-classes:calendarmessage");
				calendarPart.setHeader("Content-ID", "calendar_message");
				calendarPart.setFileName("invite.ics");
				calendarPart.setDataHandler(
						new DataHandler(new ByteArrayDataSource(calendarInvite, "text/calendar;method=PUBLISH")));
				mimePart.addBodyPart(calendarPart);
			}

			msg.setContent(mimePart);

			Map<String, String> sendResponse = emailModeService.send(msg);

			if (MapUtils.isNotEmpty(sendResponse))
				response.putAll(sendResponse);

		} catch (Exception e) {
			response.put("status", Constants.FAILED);

			logger.error("failed to send email due to " + e.getMessage(), e);
		} finally {
			if (transport != null) {
				try {
					transport.close();
				} catch (MessagingException e) {
				}
			}
		}
		return response;
	}

	private InternetAddress[] prepareRecepientList(String recipients) throws AddressException {
		if (!StringUtil.isValid(recipients))
			return null;

		String[] recipientList = recipients.split(",");
		InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
		int counter = 0;
		for (String recipient1 : recipientList) {
			recipientAddress[counter++] = new InternetAddress(recipient1.trim());
		}
		return recipientAddress;
	}

	@SuppressWarnings("unchecked")
	private Session prepareSession() {
		Properties properties = new Properties();

		Map<String, Object> params = JsonUtil.readValue(gatewayProperties.getParams(), Map.class);

		for (String item : params.keySet()) {
			properties.put(item, params.get(item));
		}

		return Session.getInstance(properties);
	}

	private String formatFileName(String contentType, String fileName) {
		try {
			return contentType.contains("zip") ? fileName + ".zip" : fileName;
		} catch (Exception e) {
		}
		return fileName;
	}

}
