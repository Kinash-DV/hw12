package dv.kinash.hw12.service;

import dv.kinash.hw12.exceptions.ItemPriceNotFoundException;
import dv.kinash.hw12.repository.ShopPriceRepository;
import dv.kinash.hw12.repository.entity.ShopPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShopService {
    ShopPriceRepository repository;
    @Autowired
    public ShopService(ShopPriceRepository repository){
        this.repository = repository;
    }

    public BigDecimal calculateBasketAmount(String basketItems){
        Map<String, Integer> itemsQuantity = new HashMap<>();
        basketItems.chars().forEach(ch -> {
            String item = String.valueOf((char) ch);
            itemsQuantity.put(item, itemsQuantity.getOrDefault(item, 0) + 1);
        });
        return itemsQuantity.entrySet().stream()
                .map(keyValue -> {
                    try {
                        return calculateItemAmount(keyValue.getKey(), keyValue.getValue());
                    } catch (ItemPriceNotFoundException e){
                        throw new ItemPriceNotFoundException(keyValue.getKey() + " not found!");
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateItemAmount(String item, Integer quantity){
        ShopPrice price = repository.findById(item).orElseThrow(ItemPriceNotFoundException::new);
        final Integer promoQuantity = price.getPromoQuantity();
        if (promoQuantity > 0 && promoQuantity <= quantity) {
            final Integer promoParts = quantity / promoQuantity;
            final Integer notPromoQuantity = quantity % promoQuantity;
            return price.getPrice().multiply(BigDecimal.valueOf(notPromoQuantity))
                    .add(price.getPromoPrice().multiply(BigDecimal.valueOf(promoParts)));
        } else
            return price.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public void init(){
        if (repository.count() > 0){
            repository.deleteAll();
        }
        List<ShopPrice> priceList = new ArrayList<>();
        priceList.add(new ShopPrice("A", new BigDecimal("1.25"), 3, new BigDecimal("3.00")));
        priceList.add(new ShopPrice("B", new BigDecimal("4.25"), 0, new BigDecimal("0.00")));
        priceList.add(new ShopPrice("C", new BigDecimal("1.00"), 6, new BigDecimal("5.00")));
        priceList.add(new ShopPrice("D", new BigDecimal("0.75"), 0, new BigDecimal("0.00")));
        repository.saveAll(priceList);
    }
}
