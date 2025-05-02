package sample.cafekiosk.spring.api.service.product;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sample.cafekiosk.spring.IntegrationServiceTest;
import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

@IntegrationServiceTest
class ProductServiceTest {

  @Autowired
  private ProductService productService;

  @Autowired
  private ProductRepository productRepository;

  @AfterEach
  void tearDown() {
    productRepository.deleteAllInBatch();
  }

  @DisplayName("신규 상품을 등록한다. 상품번호는 가장 최근 상품의 상품번호에서 1 증가한 값이다.")
  @Test
  void createProduct() {
    // given
    Product product = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
    productRepository.save(product);

    ProductCreateServiceRequest request = ProductCreateServiceRequest.builder()
        .type(HANDMADE)
        .sellingStatus(SELLING)
        .name("카푸치노")
        .price(5000)
        .build();

    // when
    ProductResponse productResponse = productService.createProduct(request);

    // then
    assertThat(productResponse)
        .extracting("productNumber", "type", "sellingStatus", "name", "price")
        .contains("002", HANDMADE, SELLING, "카푸치노", 5000);

    List<Product> products = productRepository.findAll();
    assertThat(products).hasSize(2)
        .extracting("productNumber", "type", "sellingStatus", "name", "price")
        .containsExactlyInAnyOrder(
            tuple("001", HANDMADE, SELLING, "아메리카노", 4000),
            tuple("002", HANDMADE, SELLING, "카푸치노", 5000)

        );
  }

  @DisplayName("상품이 하나도 없는 경우 상품을 등록하면 상품번호는 001 이다.")
  @Test
  void createProductWhenProductIsEmpty() {
    // given
    ProductCreateServiceRequest request = ProductCreateServiceRequest.builder()
        .type(HANDMADE)
        .sellingStatus(SELLING)
        .name("카푸치노")
        .price(5000)
        .build();

    // when
    ProductResponse productResponse = productService.createProduct(request);

    // then
    assertThat(productResponse)
        .extracting("productNumber", "type", "sellingStatus", "name", "price")
        .contains("001", HANDMADE, SELLING, "카푸치노", 5000);

    List<Product> products = productRepository.findAll();
    assertThat(products).hasSize(1)
        .extracting("productNumber", "type", "sellingStatus", "name", "price")
        .contains(
            tuple("001", HANDMADE, SELLING, "카푸치노", 5000)
        );

  }

  @DisplayName("판매중 상품과 판매보류 상품만 표시된다.")
  @Test
  void getSellingProducts() {
    // given
    Product product1 = createProduct("001", HANDMADE, SELLING, "아메리카노", 1000);
    Product product2 = createProduct("002", HANDMADE, HOLD, "카푸치노", 2000);
    Product product3 = createProduct("003", HANDMADE, STOP_SELLING, "라떼", 2000);
    productRepository.saveAll(List.of(product1, product2, product3));

    // when
    List<ProductResponse> result = productService.getSellingProducts();

    // then
    assertThat(result).hasSize(2)
        .extracting("productNumber", "type", "sellingStatus", "name", "price")
        .containsExactlyInAnyOrder(
            tuple("001", HANDMADE, SELLING, "아메리카노", 1000),
            tuple("002", HANDMADE, HOLD, "카푸치노", 2000)
        );

  }

  private Product createProduct(String productNumber, ProductType type, ProductSellingStatus sellingStatus, String name,
      int price) {
    return Product
        .builder()
        .productNumber(productNumber)
        .type(type)
        .sellingStatus(sellingStatus)
        .name(name)
        .price(price)
        .build();
  }
}
