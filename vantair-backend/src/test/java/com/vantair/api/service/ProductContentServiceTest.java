package com.vantair.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ProductContentServiceTest {

    private final ProductContentService service = new ProductContentService();

    @Test
    void generate_knownCategory_includesNameAndCategoryWords() {
        Map<String, String> c = service.generate("Vantair Citrus Bloom", "perfumes");

        assertThat(c.get("tagline")).isNotBlank();
        assertThat(c.get("description")).contains("Vantair Citrus Bloom").contains("fragrance");
        assertThat(c.get("benefits")).contains("|");
    }

    @Test
    void generate_isDeterministicForSameName() {
        assertThat(service.generate("Same Name", "skincare"))
                .isEqualTo(service.generate("Same Name", "skincare"));
    }

    @Test
    void generate_unknownCategory_usesGenericWords() {
        Map<String, String> c = service.generate("Mystery Box", "toys");
        assertThat(c.get("description")).contains("Mystery Box").contains("product");
    }

    @Test
    void generate_blankName_stillProducesCopy() {
        Map<String, String> c = service.generate("", "cosmetics");
        assertThat(c.get("tagline")).isNotBlank();
        assertThat(c.get("description")).contains("This product");
    }

    @Test
    void generate_nullNameAndCategory_safe() {
        Map<String, String> c = service.generate(null, null);
        assertThat(c.get("tagline")).isNotBlank();
        assertThat(c.get("description")).isNotBlank();
    }

    @Test
    void generate_eachKnownCategoryWorks() {
        for (String cat : new String[]{"perfumes", "toiletries", "skincare", "cosmetics"}) {
            assertThat(service.generate("X", cat).get("description")).isNotBlank();
        }
    }
}
