package dv.kinash.hw12.service;

import dv.kinash.hw12.exceptions.ItemPriceNotFoundException;
import dv.kinash.hw12.repository.ShopPriceRepository;
import dv.kinash.hw12.repository.entity.ShopPrice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

public class ShopService_calculation_Test {
    private static ShopPriceRepository repository;
    private static ShopService shopService;

    @BeforeAll
    static void setRepositoryAndShopService(){
        repository = Mockito.mock(ShopPriceRepository.class);
        Mockito.when(repository.findById(Mockito.anyString())).thenAnswer(invocation -> {
                    String item = invocation.getArgument(0);
                    switch (item) {
                        case "A" :
                            return Optional.of(new ShopPrice(
                                    "A", new BigDecimal("1.25"), 3, new BigDecimal("3.00")));
                        case "B" :
                            return Optional.of(new ShopPrice(
                                    "B", new BigDecimal("4.25"), 0, new BigDecimal("0.00")));
                        case "C" :
                            return Optional.of(new ShopPrice(
                                    "C", new BigDecimal("1.00"), 6, new BigDecimal("5.00")));
                        case "D" :
                            return Optional.of(new ShopPrice(
                                    "D", new BigDecimal("0.75"), 0, new BigDecimal("0.00")));
                        default:
                            return Optional.empty();
                    }
                });

        shopService = new ShopService(repository);
    }

    @Test
    void canCalculateAmountForKnownItems(){
        BigDecimal result = shopService.calculateBasketAmount("ABCD");
        Assertions.assertFalse(result.equals(BigDecimal.ZERO));
    }
    @Test
    void cantCalculateAmountForUnknownItems(){
        Assertions.assertThrows(
                ItemPriceNotFoundException.class,
                () -> shopService.calculateBasketAmount("E"));
    }
    @Test
    void shouldReturnZeroForEmptyString(){
        BigDecimal result = shopService.calculateBasketAmount("");
        Assertions.assertTrue(result.equals(BigDecimal.ZERO));
    }
    @Test
    void checkCalculationRulesForItemWithoutDiscount(){
        Assertions.assertEquals(new BigDecimal("4.25"), shopService.calculateBasketAmount("B"));
        Assertions.assertEquals(new BigDecimal("42.50"), shopService.calculateBasketAmount("B".repeat(10)));
    }
    @Test
    void checkCalculationRulesForItemWithDiscount(){
        Assertions.assertEquals(new BigDecimal("5.00"), shopService.calculateBasketAmount("C".repeat(5)));
        Assertions.assertEquals(new BigDecimal("5.00"), shopService.calculateBasketAmount("C".repeat(6)));
        Assertions.assertEquals(new BigDecimal("6.00"), shopService.calculateBasketAmount("C".repeat(7)));
        Assertions.assertEquals(new BigDecimal("11.00"), shopService.calculateBasketAmount("C".repeat(13)));
    }
    @Test
    void checkCalculationRulesForDifferentItems(){
        // 1.25*1 + 4.25*1 + 1*1 + 0.75*1 = 7.25
        Assertions.assertEquals(new BigDecimal("7.25"), shopService.calculateBasketAmount("ABCD"));
        // (3*3.0 + 1.25) + 4.25*10 + (1*5.0 + 4*1.0) + 0.75*10 = 69.25
        Assertions.assertEquals(new BigDecimal("69.25"), shopService.calculateBasketAmount("ABCD".repeat(10)));
        // (33*3.0 + 1.25) + 4.25*100 + (16*5.0 + 4*1.0) + 0.75*100 = 684.25
        Assertions.assertEquals(new BigDecimal("684.25"), shopService.calculateBasketAmount("ABCD".repeat(100)));
    }
}
