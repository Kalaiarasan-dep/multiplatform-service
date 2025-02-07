package in.hashconnect.service;

import javax.mail.Message;
import java.util.Map;

public interface EmailModeService {
    Map<String, String> send(Message message) throws Exception;
}
