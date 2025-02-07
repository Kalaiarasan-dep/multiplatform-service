package in.hashconnect.service.impl;

import in.hashconnect.service.EmailModeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import software.amazon.awssdk.services.ses.model.SendRawEmailResponse;

import javax.mail.Message;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class HTTPSEmailModeServiceImpl implements EmailModeService {
    private final Logger logger = LoggerFactory.getLogger(HTTPSEmailModeServiceImpl.class);

    @Autowired
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
