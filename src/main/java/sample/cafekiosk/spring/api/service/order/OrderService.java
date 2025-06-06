package sample.cafekiosk.spring.api.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.service.order.request.OrderCreateServiceRequest;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.stock.Stock;
import sample.cafekiosk.spring.domain.stock.StockRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

  private final ProductRepository productRepository;
  private final OrderRepository orderRepository;
  private final StockRepository stockRepository;

  /**
   *  재고 감소 -> 동시성 고민 (여러대의 키오스크 있을경우)
   *  optimistic lock / pessimistic lock / ...
   */
  public OrderResponse createOrder(OrderCreateServiceRequest request, LocalDateTime registeredDateTime) {
    List<String> productNumbers = request.getProductNumbers();
    List<Product> products = findProductsBy(productNumbers);

    deductStockQuantities(products);

    Order order = Order.create(products, registeredDateTime);
    Order savedOrder = orderRepository.save(order);
    return OrderResponse.of(savedOrder);
  }

  private List<Product> findProductsBy(List<String> productNumbers) {
    List<Product> products = productRepository.findAllByProductNumberIn(productNumbers);
    Map<String, Product> productMap = products
        .stream()
        .collect(Collectors.toMap(Product::getProductNumber, product -> product));

    return productNumbers
        .stream()
        .map(productMap::get)
        .toList();
  }

  private void deductStockQuantities(List<Product> products) {
    List<String> stockProductNumbers = extractStockProductNumbers(products);

    Map<String, Stock> stockMap = createStockMapBy(stockProductNumbers);
    Map<String, Long> productCountingMap = createCountingMapBy(stockProductNumbers);

    // 재고 차감 시도
    for (String stockProductNumber : new HashSet<>(stockProductNumbers)) {
      Stock stock = stockMap.get(stockProductNumber);
      int quantity = productCountingMap.get(stockProductNumber).intValue();

      if (stock.isQuantityLessThan(quantity)) {
        throw new IllegalArgumentException("재고가 부족한 상품이 있습니다.");
      }
      stock.deductQuantity(quantity);
    }
  }

  private List<String> extractStockProductNumbers(List<Product> products) {
    // 재고 차감 체크가 필요한 상품들 필터링
    return products
        .stream()
        .filter(product -> ProductType.containsStockType(product.getType()))
        .map(Product::getProductNumber)
        .toList();
  }

  private Map<String, Stock> createStockMapBy(List<String> stockProductNumbers) {
    // 재고 엔티티 조회
    List<Stock> stocks = stockRepository.findAllByProductNumberIn(stockProductNumbers);
    return stocks.stream()
        .collect(Collectors.toMap(Stock::getProductNumber, stock -> stock));
  }

  private Map<String, Long> createCountingMapBy(List<String> stockProductNumbers) {
    // 상품별 카운팅
    return stockProductNumbers.stream()
        .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
  }
}
