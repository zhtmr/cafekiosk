package sample.cafekiosk.spring.api.service.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sample.cafekiosk.spring.domain.product.ProductRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductNumberFactoryTest {

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private ProductNumberFactory productNumberFactory;

  @DisplayName("최근 상품 번호가 없을 경우 '001'을 반환해야 한다.")
  @Test
  void createNextProductNumber() {
    // given
    given(productRepository.findLatestProductNumber()).willReturn(null);

    // when
    String nextProductNumber = productNumberFactory.createNextProductNumber();

    // then
    assertThat(nextProductNumber).isEqualTo("001");
  }

  @DisplayName("최근 상품 번호가 있을 경우 숫자를 1 증가시켜 반환해야 한다.")
  @Test
  void createNextProductNumber2() {
    // given
    given(productRepository.findLatestProductNumber()).willReturn("003");

    // when
    String nextProductNumber = productNumberFactory.createNextProductNumber();

    // then
    assertThat(nextProductNumber).isEqualTo("004");
  }

  @DisplayName("최근 상품 번호가 앞에 0이 포함된 경우에도 포맷을 유지하며 반환해야 한다.")
  @Test
  void createNextProductNumber3() {
    // given
    given(productRepository.findLatestProductNumber()).willReturn("009");

    // when
    String nextProductNumber = productNumberFactory.createNextProductNumber();

    // then
    assertThat(nextProductNumber).isEqualTo("010");
  }

}
