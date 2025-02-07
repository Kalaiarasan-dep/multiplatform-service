package in.hashconnect.service.impl;


import in.hashconnect.common.util.JsonUtil;
import in.hashconnect.config.properties.EmailGatewayProperties;
import in.hashconnect.controller.vo.Attachment;
import in.hashconnect.controller.vo.Notification;
import in.hashconnect.exceptions.CustomException;
import in.hashconnect.service.EmailModeService;
import in.hashconnect.service.NotificationService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.apache.commons.collections4.MapUtils.getString;
import static org.apache.commons.lang.StringUtils.isBlank;


public class EmailNotificationServiceImpl extends AbstractNotificationService implements NotificationService {
	private static final Logger logger = LoggerFactory.getLogger(EmailNotificationServiceImpl.class);

	private EmailGatewayProperties gatewayProperties;


	@Value("${spring.profiles.active}")
	private String profile;

	@Autowired
	private EmailModeService emailModeService;

	public void process(Notification notification) {
		String template = notification.getTemplate();
		String type = notification.getType().toString();
		String to = notification.getTo();
		String cc = notification.getCc();
		String subject = notification.getSubject();

		Map<String, Object> reqParams = notification.getParams();
		Attachment attachment = notification.getAttachment();
		// get details by key
		Map<String, Object> params = getTemplateDetailsByKey(template);
		if (params == null) {
			logger.info("invalid temlate {}", template);
			return;
		}

		if (isBlank(subject)){
			subject = getString(params, "subject");
		}
		String from = getString(params, "efrom");
		String displayName = getString(params, "from_display_name");
		if (isBlank(cc)) {
			cc = getString(params, "cc");
		}
		String body = getBody(template, reqParams);

		// save in db
		Map<String,Object> notificationParams = buildNotificationParams(MapUtils.getInteger(params,"id"), type, to, from, cc, subject);
		Long id = persistNotification(notificationParams, reqParams, notification.getClientId());
		Map<String, String> response = new HashMap<>();
		if(!profile.equals("prod") ){

			Boolean enableEmailInQA= true;
			if(Boolean.TRUE.equals(enableEmailInQA)) {
				Boolean notifyOnlyInternalUsers = true;
				if (Boolean.TRUE.equals(notifyOnlyInternalUsers)) {
					if (to.contains("@hashconnect.in")) {
						response = sendEmail(notificationParams, body, attachment, null, displayName);
					}
				} else {
					response = sendEmail(notificationParams, body, attachment, null, displayName);
				}
			}
		}else{
			response = sendEmail(notificationParams, body,  attachment, null,  displayName);
		}
		String status = getString(response, "status");
		String refId = getString(response, "refId");

		// update DB
		updateStatus(id, status, null, refId);

	}

	private Map<String, String> sendEmail(Map<String,Object> notificationParams, String body, Attachment attachment,
			String calendarInvite, String fromDisplayName) {

		String to = MapUtils.getString(notificationParams,"to");
		String from = MapUtils.getString(notificationParams,"from");
		String cc = MapUtils.getString(notificationParams,"cc");
		String subject = MapUtils.getString(notificationParams,"subject");

		if (logger.isInfoEnabled())
			logger.info("sendEmail - to: " + to + ", subject: " + subject);

		Map<String, String> response = new HashMap<String, String>(2);
		response.put("status", "SUCCESS");

		Transport transport = null;

		try {
			Session session = prepareSession();

			// creates a new e-mail message
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from, fromDisplayName));

			// email to
			InternetAddress[] recipientAddress = prepareRecepientList(to);
			if (recipientAddress == null)
				throw new CustomException("Missing To Addresses");

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

			if (attachment !=null &&  attachment.getByteData() != null) {
				MimeBodyPart attach = new MimeBodyPart();
				DataSource dataSource = new ByteArrayDataSource(new ByteArrayInputStream(attachment.getByteData()),
						attachment.getContentType());
				attach.setDataHandler(new DataHandler(dataSource));
				String fileName = isZipContent(attachment.getContentType()) ? attachment.getFileName() + ".zip"
						: attachment.getFileName();
				attach.setFileName(fileName);
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

			if (org.apache.commons.collections4.MapUtils.isNotEmpty(sendResponse))
				response.putAll(sendResponse);

		} catch (Exception e) {
			response.put("status","FAILED");
			logger.error("failed to send email due to " + e.getMessage(), e);
		} finally {
			if (transport != null) {
				try {
					transport.close();
				} catch (MessagingException e) {
					logger.error("Error while closing {}", e.getMessage());
				}
			}

			if (logger.isInfoEnabled()) {
				logger.info("Email subject: " + subject + ", to: " + to + ", status: " + getString(response,"status") + ", cc: " + cc);
			}
		}
		return response;
	}

	private InternetAddress[] prepareRecepientList(String recipients) throws AddressException {
		if (StringUtils.isBlank(recipients))
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
		params.entrySet().forEach(e-> properties.put(e.getKey(),e.getValue()));
		return Session.getInstance(properties);
	}

	private boolean isZipContent(String contentType) {
		try {
			return contentType.contains("zip");
		} catch (Exception e) {
			return false;
		}
	}

	public void setGatewayProperties(EmailGatewayProperties gatewayProperties) {
		this.gatewayProperties = gatewayProperties;
	}

}
