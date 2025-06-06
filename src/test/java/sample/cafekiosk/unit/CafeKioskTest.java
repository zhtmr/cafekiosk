package sample.cafekiosk.unit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.cafekiosk.unit.beverage.Americano;
import sample.cafekiosk.unit.beverage.Latte;
import sample.cafekiosk.unit.order.Order;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class CafeKioskTest {

  @DisplayName("음료 1개를 추가하면 주문 목록에 담긴다.!!")
  @Test
  void add() {
    CafeKiosk cafeKiosk = new CafeKiosk();
    cafeKiosk.add(new Americano());

    assertThat(cafeKiosk.getBeverages()).hasSize(1);

    assertThat(cafeKiosk
        .getBeverages()
        .get(0)
        .getName()).isEqualTo("아메리카노");
  }

  @Test
  void addSeveralBeverages() {
    CafeKiosk cafeKiosk = new CafeKiosk();
    Americano americano = new Americano();
    cafeKiosk.add(americano, 2);

    assertThat(cafeKiosk
        .getBeverages()
        .get(0)).isEqualTo(americano);
    assertThat(cafeKiosk
        .getBeverages()
        .get(1)).isEqualTo(americano);
  }

  @Test
  void addZeroBeverages() {
    CafeKiosk cafeKiosk = new CafeKiosk();
    Americano americano = new Americano();

    assertThatThrownBy(() -> cafeKiosk.add(americano, 0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("count must be greater than 0");
  }

  @Test
  void remove() {

    CafeKiosk cafeKiosk = new CafeKiosk();
    Americano americano = new Americano();

    cafeKiosk.add(americano);
    assertThat(cafeKiosk.getBeverages()).hasSize(1);

    cafeKiosk.remove(americano);
    assertThat(cafeKiosk.getBeverages()).isEmpty();
  }

  @Test
  void clear() {

    CafeKiosk cafeKiosk = new CafeKiosk();
    Americano americano = new Americano();
    Latte latte = new Latte();

    cafeKiosk.add(americano);
    cafeKiosk.add(latte);
    assertThat(cafeKiosk.getBeverages()).hasSize(2);

    cafeKiosk.clear();
    assertThat(cafeKiosk.getBeverages()).isEmpty();
  }


  @DisplayName("주문 목록에 담긴 상품들의 총 금액을 계산할 수 있다.")
  @Test
  void calculateTotalPrice() {
    // given
    CafeKiosk cafeKiosk = new CafeKiosk();
    Americano americano = new Americano();
    Latte latte = new Latte();
    cafeKiosk.add(americano);
    cafeKiosk.add(latte);

    // when
    int totalPrice = cafeKiosk.calculateTotalPrice();

    // then
    assertThat(totalPrice).isEqualTo(8500);
  }

  @Disabled("시간이 코드에 강결합되어 있어 테스트 불가")
  @Test
  void createOrder() {

    CafeKiosk cafeKiosk = new CafeKiosk();
    Americano americano = new Americano();

    cafeKiosk.add(americano);

    Order order = cafeKiosk.createOrder();
    assertThat(order.getBeverages()).hasSize(1);
    assertThat(order
        .getBeverages()
        .get(0)
        .getName()).isEqualTo("아메리카노");
  }

  @Test
  void createOrderWithCurrentTime() {

    CafeKiosk cafeKiosk = new CafeKiosk();
    Americano americano = new Americano();

    cafeKiosk.add(americano);

    Order order = cafeKiosk.createOrder(LocalDateTime.of(2025, 3, 18, 10, 0));

    assertThat(order.getBeverages()).hasSize(1);
    assertThat(order
        .getBeverages()
        .get(0)
        .getName()).isEqualTo("아메리카노");
  }

  @Test
  void createOrderOutsideOpenTime() {

    CafeKiosk cafeKiosk = new CafeKiosk();
    Americano americano = new Americano();

    cafeKiosk.add(americano);

    assertThatThrownBy(() -> cafeKiosk.createOrder(LocalDateTime.of(2025, 3, 18, 9, 59)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("주문 시간이 아닙니다.");
  }
}
