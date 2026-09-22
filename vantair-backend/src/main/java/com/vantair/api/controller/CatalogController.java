package com.vantair.api.controller;

import com.vantair.api.model.Category;
import com.vantair.api.model.Product;
import com.vantair.api.service.CatalogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Catalog", description = "Products and categories")
public class CatalogController {

    private final CatalogService catalog;

    public CatalogController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/products")
    public List<Product> products(@RequestParam(required = false) String category) {
        return catalog.listProducts(category);
    }

    @GetMapping("/products/{id}")
    public Product product(@PathVariable String id) {
        return catalog.getProduct(id);
    }

    @GetMapping("/categories")
    public List<Category> categories() {
        return catalog.listCategories();
    }
}
