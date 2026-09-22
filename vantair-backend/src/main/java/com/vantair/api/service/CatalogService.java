package com.vantair.api.service;

import com.vantair.api.config.ApiException;
import com.vantair.api.model.Category;
import com.vantair.api.model.Product;
import com.vantair.api.repository.CategoryRepository;
import com.vantair.api.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogService {

    private final ProductRepository products;
    private final CategoryRepository categories;

    public CatalogService(ProductRepository products, CategoryRepository categories) {
        this.products = products;
        this.categories = categories;
    }

    @Transactional(readOnly = true)
    public List<Product> listProducts(String category) {
        if (category != null && !category.isBlank()) {
            return products.findByCategoryAndActiveTrue(category);
        }
        return products.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Product getProduct(String id) {
        return products.findById(id)
                .orElseThrow(() -> ApiException.notFound("Product not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Category> listCategories() {
        return categories.findAll();
    }

    /** Category slug → product-id prefix used when auto-generating ids. */
    private static final java.util.Map<String, String> ID_PREFIX = java.util.Map.of(
            "perfumes", "P", "toiletries", "T", "skincare", "S", "cosmetics", "C");

    @Transactional
    public Product createProduct(Product product) {
        if (product.getId() == null || product.getId().isBlank()) {
            product.setId(generateProductId(product.getCategory()));
        } else if (products.existsById(product.getId())) {
            throw ApiException.conflict("Product already exists: " + product.getId());
        }
        product.getVariants().forEach(v -> v.setProduct(product));
        return products.save(product);
    }

    /** Next free id for a category, e.g. "P009" — falls back to "X" prefix for unknown categories. */
    String generateProductId(String category) {
        String prefix = ID_PREFIX.getOrDefault(category == null ? "" : category, "X");
        int max = 0;
        for (Product p : products.findAll()) {
            String id = p.getId();
            if (id != null && id.startsWith(prefix)) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(prefix.length())));
                } catch (NumberFormatException ignored) {
                    // non-numeric suffix — skip
                }
            }
        }
        return String.format("%s%03d", prefix, max + 1);
    }

    @Transactional
    public Product updateProduct(String id, Product incoming) {
        Product existing = getProduct(id);
        existing.setName(incoming.getName());
        existing.setCategory(incoming.getCategory());
        existing.setCategoryLabel(incoming.getCategoryLabel());
        existing.setTagline(incoming.getTagline());
        existing.setDescription(incoming.getDescription());
        existing.setBenefits(incoming.getBenefits());
        existing.setIngredients(incoming.getIngredients());
        existing.setPrice(incoming.getPrice());
        existing.setMrp(incoming.getMrp());
        existing.setRating(incoming.getRating());
        existing.setReviews(incoming.getReviews());
        existing.setBadge(incoming.getBadge());
        existing.setColor(incoming.getColor());
        existing.setEmoji(incoming.getEmoji());
        existing.setExpressEligible(incoming.isExpressEligible());
        if (incoming.getStock() != null) {
            existing.setStock(incoming.getStock());
        }
        existing.setActive(incoming.isActive());
        // Replace variants wholesale.
        existing.getVariants().clear();
        incoming.getVariants().forEach(existing::addVariant);
        return products.save(existing);
    }

    @Transactional
    public void setActive(String id, boolean active) {
        Product p = getProduct(id);
        p.setActive(active);
        products.save(p);
    }
}
