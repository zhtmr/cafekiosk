package sample.cafekiosk.spring.domain.stock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@DataJpaTest
class StockRepositoryTest {

  @Autowired
  private StockRepository stockRepository;

  @DisplayName("상품번호 리스트로 재고를 조회한다.")
  @Test
  void findAllByProductNumberIn() {
    // given
    Stock stock1 = Stock.create("001", 1);
    Stock stock2 = Stock.create("002", 2);
    Stock stock3 = Stock.create("003", 3);
    stockRepository.saveAll(List.of(stock1, stock2, stock3));

    // when
    List<Stock> stocks = stockRepository.findAllByProductNumberIn(List.of("001", "002"));

    // then
    assertThat(stocks)
        .hasSize(2)
        .extracting("productNumber", "quantity")
        .containsExactlyInAnyOrder(
            tuple("001", 1),
            tuple("002", 2));
  }
  
  @DisplayName("재고의 수량이 제공된 수량보다 작은지 확인한다.")
  @Test
  void isQuantityLessThan() {
    // given
    Stock stock = Stock.create("001", 1);
    int quantity = 2;

    // when
    boolean result = stock.isQuantityLessThan(quantity);

    // then
    assertThat(result).isTrue();

  }

  @DisplayName("재고를 주어진 갯수 만큼 차감할 수 있다.")
  @Test
  void deductQuantity() {
    // given
    Stock stock = Stock.create("001", 1);
    int quantity = 1;

    // when
    stock.deductQuantity(quantity);

    // then
    assertThat(stock.getQuantity()).isZero();
  }

  @DisplayName("재고보다 많은 수량으로 차감 시도하는 경우 예외가 발생한다.")
  @Test
  void deductQuantity2() {
    // given
    Stock stock = Stock.create("001", 1);
    int quantity = 2;

    // when
    // then
    assertThatThrownBy(() -> stock.deductQuantity(quantity))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("차감할 재고 수량이 없습니다.");
  }
}
