package sample.cafekiosk.spring.api.service.order;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sample.cafekiosk.spring.IntegrationServiceTest;
import sample.cafekiosk.spring.client.mail.MailSendClient;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistory;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.order.OrderStatus;
import sample.cafekiosk.spring.domain.orderproduct.OrderProductRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

@IntegrationServiceTest
class OrderStatisticsServiceTest {

  @Autowired
  private OrderStatisticsService orderStatisticsService;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private OrderProductRepository orderProductRepository;

  @Autowired
  private MailSendHistoryRepository mailSendHistoryRepository;

  @MockitoBean
  protected MailSendClient mailSendClient;


  @AfterEach
  void tearDown() {
    orderProductRepository.deleteAllInBatch();
    orderRepository.deleteAllInBatch();
    productRepository.deleteAllInBatch();
    mailSendHistoryRepository.deleteAllInBatch();
  }

  @DisplayName("결제완료 주문들을 조회하여 매출 통계 메일을 전송한다.!!")
  @Test
  void sendOrderStatisticsMail() {
    // given
    LocalDateTime now = LocalDateTime.of(2025, 4, 26, 0, 0);

    Product product1 = createProduct(HANDMADE, "001", 1000);
    Product product2 = createProduct(HANDMADE, "002", 2000);
    Product product3 = createProduct(HANDMADE, "003", 3000);
    productRepository.saveAll(List.of(product1, product2, product3));

    List<Product> products = List.of(product1, product2, product3);
    Order order1 = createPaymentCompletedOrder(products, LocalDateTime.of(2025, 4, 25, 23, 59, 59));
    Order order2 = createPaymentCompletedOrder(products, now);
    Order order3 = createPaymentCompletedOrder(products, LocalDateTime.of(2025, 4, 26, 23, 59, 59));
    Order order4 = createPaymentCompletedOrder(products, LocalDateTime.of(2025, 4, 27, 0, 0));

    // stubbing
    BDDMockito.given(mailSendClient.sendMail(anyString(), anyString(), anyString(), anyString()))
        .willReturn(true);
    //    when(mailSendClient.sendMail(any(String.class), any(String.class), any(String.class), any(String.class)))
    //        .thenReturn(true);

    // when
    boolean result = orderStatisticsService.sendOrderStatisticsMail(LocalDate.of(2025, 4, 26), "test@test.com");

    // then
    assertThat(result).isTrue();

    List<MailSendHistory> histories = mailSendHistoryRepository.findAll();
    assertThat(histories).hasSize(1)
        .extracting("content")
        .contains("총 매출 합계는 12000원입니다.");
  }

  private Order createPaymentCompletedOrder(List<Product> products, LocalDateTime now) {
    Order order = Order.builder()
        .products(products)
        .orderStatus(OrderStatus.COMPLETED)
        .registeredDateTime(now)
        .build();
    return orderRepository.save(order);
  }

  private Product createProduct(ProductType type, String productNumber, int price) {
    return Product
        .builder()
        .type(type)
        .productNumber(productNumber)
        .price(price)
        .build();
  }



}
