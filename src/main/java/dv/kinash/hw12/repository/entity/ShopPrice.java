package dv.kinash.hw12.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ShopPrice {
    @Id
    private String Item;
    private BigDecimal price;
    private Integer promoQuantity;
    private BigDecimal promoPrice;
}
