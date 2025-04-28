package sample.cafekiosk.spring.api.service.mail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sample.cafekiosk.spring.client.mail.MailSendClient;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistory;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

  @Mock
  private MailSendClient mailSendClient;

  @Mock
  private MailSendHistoryRepository mailSendHistoryRepository;

  @InjectMocks
  private MailService mailService;

  @DisplayName("메일 전송 테스트")
  @Test
  void sendMail() {
    // given
//    Mockito.when(mailSendClient.sendMail(anyString(), anyString(), anyString(), anyString()))
//        .thenReturn(true);

    given(mailSendClient.sendMail(anyString(), anyString(), anyString(), anyString())).willReturn(true);

    // @Spy 를 사용하려면 아래처럼 stubbing 해야 된다. 실제 객체의 일부 기능이 그대로 동작하는지 확인하고 싶을때
//    doReturn(true)
//        .when(mailSendClient)
//        .sendMail(anyString(), anyString(), anyString(), anyString());

    // null 이나 0 이 반환되어도 되는 stub 객체는 따로 stubbing 하지 않아도 mock 기본 정책에 의해 null 로 반환된다.
//    when(mailSendHistoryRepository.save(any(MailSendHistory.class))).thenReturn(null);

    // when
    boolean result = mailService.sendMail("", "", "", "");

    // then
    assertTrue(result);
    // save 라는 행위가 한번 호출되었는지 검증할 수 있다.
    verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class));

  }

}
