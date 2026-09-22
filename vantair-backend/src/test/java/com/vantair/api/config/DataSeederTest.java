package com.vantair.api.config;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vantair.api.model.Product;
import com.vantair.api.repository.CategoryRepository;
import com.vantair.api.repository.CouponRepository;
import com.vantair.api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {

    @Mock CategoryRepository categories;
    @Mock ProductRepository products;
    @Mock CouponRepository coupons;

    private final DataSeeder seeder = new DataSeeder();

    @Test
    void seed_emptyTables_writesEverything() throws Exception {
        when(categories.count()).thenReturn(0L);
        when(coupons.count()).thenReturn(0L);
        when(products.count()).thenReturn(0L);

        CommandLineRunner runner = seeder.seed(categories, products, coupons);
        runner.run();

        verify(categories).saveAll(anyList());
        verify(coupons).saveAll(anyList());
        // 32 products are saved individually.
        verify(products, times(32)).save(org.mockito.ArgumentMatchers.any(Product.class));
    }

    @Test
    void seed_populatedTables_writesNothing() throws Exception {
        when(categories.count()).thenReturn(4L);
        when(coupons.count()).thenReturn(6L);
        when(products.count()).thenReturn(32L);

        seeder.seed(categories, products, coupons).run();

        verify(categories, never()).saveAll(anyList());
        verify(coupons, never()).saveAll(anyList());
        verify(products, never()).save(org.mockito.ArgumentMatchers.any(Product.class));
    }
}
