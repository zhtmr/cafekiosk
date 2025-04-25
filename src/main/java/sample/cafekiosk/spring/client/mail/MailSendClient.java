package sample.cafekiosk.spring.client.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MailSendClient {

  public boolean sendMail(String fromEmail, String toEmail, String subject, String contents) {
    // 메일 전송
    log.info("메일 전송");
    throw new IllegalArgumentException("메일 전송");  // 구현하지 않고 테스트 용으로만 사용 (mocking)
  }
}
