package com.vantair.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vantair.api.TestFixtures;
import com.vantair.api.config.ApiException;
import com.vantair.api.model.Category;
import com.vantair.api.model.Product;
import com.vantair.api.repository.CategoryRepository;
import com.vantair.api.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock ProductRepository products;
    @Mock CategoryRepository categories;
    @InjectMocks CatalogService service;

    @Test
    void listProducts_byCategory_usesCategoryQuery() {
        Product p = TestFixtures.product("P001", "perfumes", 999);
        when(products.findByCategoryAndActiveTrue("perfumes")).thenReturn(List.of(p));

        assertThat(service.listProducts("perfumes")).containsExactly(p);
        verify(products, never()).findByActiveTrue();
    }

    @Test
    void listProducts_blankOrNull_listsAllActive() {
        when(products.findByActiveTrue()).thenReturn(List.of());

        assertThat(service.listProducts(null)).isEmpty();
        assertThat(service.listProducts("   ")).isEmpty();
        verify(products, never()).findByCategoryAndActiveTrue(any());
    }

    @Test
    void getProduct_found() {
        Product p = TestFixtures.product("P001", "perfumes", 999);
        when(products.findById("P001")).thenReturn(Optional.of(p));

        assertThat(service.getProduct("P001")).isSameAs(p);
    }

    @Test
    void getProduct_missing_throwsNotFound() {
        when(products.findById("X")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getProduct("X"))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void listCategories_delegates() {
        Category c = new Category();
        when(categories.findAll()).thenReturn(List.of(c));
        assertThat(service.listCategories()).containsExactly(c);
    }

    @Test
    void createProduct_new_savesAndLinksVariants() {
        Product p = TestFixtures.product("P009", "perfumes", 500, TestFixtures.variant("50ml", 500));
        when(products.existsById("P009")).thenReturn(false);
        when(products.save(p)).thenReturn(p);

        Product saved = service.createProduct(p);

        assertThat(saved).isSameAs(p);
        assertThat(p.getVariants().get(0).getProduct()).isSameAs(p);
    }

    @Test
    void createProduct_nullId_autoGeneratesWithPrefix() {
        Product existing = TestFixtures.product("P008", "perfumes", 999);
        Product p = TestFixtures.product("ignored", "perfumes", 500);
        p.setId(null);
        when(products.findAll()).thenReturn(List.of(existing));
        when(products.save(p)).thenReturn(p);

        Product saved = service.createProduct(p);

        assertThat(saved.getId()).isEqualTo("P009"); // max P008 + 1
        verify(products, never()).existsById(any());
    }

    @Test
    void createProduct_blankId_autoGenerates() {
        Product p = TestFixtures.product("x", "skincare", 500);
        p.setId("  ");
        when(products.findAll()).thenReturn(List.of());
        when(products.save(p)).thenReturn(p);

        assertThat(service.createProduct(p).getId()).isEqualTo("S001");
    }

    @Test
    void generateProductId_unknownCategory_usesXPrefix() {
        when(products.findAll()).thenReturn(List.of());
        assertThat(service.generateProductId("misc")).isEqualTo("X001");
    }

    @Test
    void generateProductId_ignoresNonNumericSuffixes() {
        Product weird = TestFixtures.product("Pabc", "perfumes", 100);
        Product real = TestFixtures.product("P005", "perfumes", 100);
        when(products.findAll()).thenReturn(List.of(weird, real));
        assertThat(service.generateProductId("perfumes")).isEqualTo("P006");
    }

    @Test
    void createProduct_duplicate_throwsConflict() {
        Product p = TestFixtures.product("P001", "perfumes", 999);
        when(products.existsById("P001")).thenReturn(true);

        assertThatThrownBy(() -> service.createProduct(p))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void updateProduct_copiesFieldsAndReplacesVariants() {
        Product existing = TestFixtures.product("P001", "perfumes", 999, TestFixtures.variant("old", 1));
        Product incoming = TestFixtures.product("P001", "skincare", 1299, TestFixtures.variant("50ml", 1299));
        incoming.setName("Updated");
        incoming.setStock(42);
        incoming.setActive(false);
        when(products.findById("P001")).thenReturn(Optional.of(existing));
        when(products.save(existing)).thenReturn(existing);

        Product result = service.updateProduct("P001", incoming);

        assertThat(result.getName()).isEqualTo("Updated");
        assertThat(result.getCategory()).isEqualTo("skincare");
        assertThat(result.getStock()).isEqualTo(42);
        assertThat(result.isActive()).isFalse();
        assertThat(result.getVariants()).hasSize(1);
        assertThat(result.getVariants().get(0).getLabel()).isEqualTo("50ml");
    }

    @Test
    void updateProduct_nullStock_keepsExistingStock() {
        Product existing = TestFixtures.product("P001", "perfumes", 999);
        existing.setStock(7);
        Product incoming = TestFixtures.product("P001", "perfumes", 999);
        incoming.setStock(null);
        when(products.findById("P001")).thenReturn(Optional.of(existing));
        when(products.save(existing)).thenReturn(existing);

        assertThat(service.updateProduct("P001", incoming).getStock()).isEqualTo(7);
    }

    @Test
    void setActive_togglesAndSaves() {
        Product p = TestFixtures.product("P001", "perfumes", 999);
        when(products.findById("P001")).thenReturn(Optional.of(p));

        service.setActive("P001", false);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(products).save(captor.capture());
        assertThat(captor.getValue().isActive()).isFalse();
    }
}
