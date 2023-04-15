package dv.kinash.hw12.service;

import dv.kinash.hw12.repository.ShopPriceRepository;
import dv.kinash.hw12.repository.entity.ShopPrice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

class ShopService_init_Test {

    private static ShopPriceRepository repository;
    private ShopService shopService;

    @BeforeAll
    static void setRepository(){
        repository = Mockito.mock(ShopPriceRepository.class);
    }

    @BeforeEach
    void setShopService() {
        shopService = new ShopService(repository);
    }

    @Test
    void checkCountInBase() {
        shopService.init();
        Mockito.verify(repository, Mockito.atLeastOnce()).count();
    }
    @Test
    void checkDeletingAllData() {
        Mockito.when(repository.count()).thenReturn(10L);
        shopService.init();
        Mockito.verify(repository).deleteAll();
    }
    @Test
    void checkNotDeletingEmptyData() {
        Mockito.when(repository.count()).thenReturn(0L);
        shopService.init();
        Mockito.verify(repository, Mockito.never()).deleteAll();
    }
    @Test
    void checkSavingData() {
        shopService.init();

        ArgumentCaptor<List<ShopPrice>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(repository).saveAll(argumentCaptor.capture());
        Assertions.assertFalse(argumentCaptor.getValue().isEmpty());
    }

}