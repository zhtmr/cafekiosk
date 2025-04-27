package sample.cafekiosk.spring.api.service.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.cafekiosk.spring.client.mail.MailSendClient;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistory;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;

@RequiredArgsConstructor
@Service
public class MailService {

  private final MailSendClient mailSendClient;
  private final MailSendHistoryRepository mailSendHistoryRepository;

  public boolean sendMail(String fromEmail, String toEmail, String subject, String contents) {
    boolean result = mailSendClient.sendMail(fromEmail, toEmail, subject, contents);
    if (result) {
      mailSendHistoryRepository.save(MailSendHistory.builder()
          .fromEmail(fromEmail)
          .toEmail(toEmail)
          .subject(subject)
          .content(contents)
          .build());

      // @Spy 를 쓰면 log 확인할 수 있다
      mailSendClient.a();
      mailSendClient.b();
      mailSendClient.c();

      return true;
    }
    return false;
  }
}
