package com.vantair.api.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    /** Frontend product code, e.g. "P001". */
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    /** Category slug, kept as a plain column to mirror the frontend's flat shape. */
    private String category;

    private String categoryLabel;

    private String tagline;

    @Column(length = 2000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "product_benefits", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "benefit", length = 500)
    private List<String> benefits = new ArrayList<>();

    @Column(length = 1000)
    private String ingredients;

    private Integer price;
    private Integer mrp;
    private Double rating;
    private Integer reviews;
    private String badge;
    private String color;
    private String emoji;
    private boolean expressEligible;

    /** Inventory count; not present in the frontend mock but needed for a real backend. */
    private Integer stock = 100;

    private boolean active = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "variant_order")
    private List<ProductVariant> variants = new ArrayList<>();

    public void addVariant(ProductVariant variant) {
        variant.setProduct(this);
        this.variants.add(variant);
    }
}
