package com.vantair.api.config;

import com.vantair.api.model.Category;
import com.vantair.api.model.Coupon;
import com.vantair.api.model.Product;
import com.vantair.api.model.ProductVariant;
import com.vantair.api.repository.CategoryRepository;
import com.vantair.api.repository.CouponRepository;
import com.vantair.api.repository.ProductRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Seeds the catalog (categories, 32 products with variants, coupons) on first
 * run. Ported from the frontend's js/data.js so the API and storefront agree.
 * Idempotent: it only writes when the tables are empty.
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(CategoryRepository categories, ProductRepository products, CouponRepository coupons) {
        return args -> {
            if (categories.count() == 0) {
                seedCategories(categories);
            }
            if (coupons.count() == 0) {
                seedCoupons(coupons);
            }
            if (products.count() == 0) {
                seedProducts(products);
            }
        };
    }

    private void seedCategories(CategoryRepository repo) {
        repo.saveAll(List.of(
                category("perfumes", "Perfumes", "🌸", "EDP, EDT, Body Mists & Roll-Ons", "#2c2c54"),
                category("toiletries", "Toiletries", "🚿", "Shampoo, Body Wash, Soap & More", "#0077b6"),
                category("skincare", "Skin Care", "✨", "Serums, Moisturisers & Sunscreen", "#1d6a4a"),
                category("cosmetics", "Cosmetics", "💄", "Lipstick, Foundation, Kajal & More", "#8e1a4a")));
    }

    private void seedCoupons(CouponRepository repo) {
        repo.saveAll(List.of(
                coupon("VANTAIR10", "percent", 10, 500, "10% off on orders above ₹500"),
                coupon("WELCOME20", "percent", 20, 999, "20% off for new customers"),
                coupon("FLAT100", "flat", 100, 799, "₹100 off on orders above ₹799"),
                coupon("FLAT200", "flat", 200, 1499, "₹200 off on orders above ₹1499"),
                coupon("BEAUTY15", "percent", 15, 699, "15% off on beauty products"),
                coupon("FREESHIP", "shipping", 0, 0, "Free shipping on any order")));
    }

    private void seedProducts(ProductRepository repo) {
        // ── Perfumes ──────────────────────────────────────────────────
        repo.save(product("P001", "perfumes", "Perfumes", "Vantair Noir EDP",
                "Dark. Mysterious. Unforgettable.",
                "A bold oriental fragrance with deep notes of oud, amber and dark musk. Perfect for evening wear and special occasions.",
                List.of("Long lasting 8–10 hours", "Eau de Parfum concentration", "Unisex fragrance"),
                "Alcohol Denat., Aqua, Fragrance, Oud Extract, Amber Oil, Musk",
                1299, 1599, 4.8, 124, "Bestseller", "#1a1a2e", "🖤", true,
                List.of(variant("30ml", 799), variant("50ml", 1299), variant("100ml", 2199))));
        repo.save(product("P002", "perfumes", "Perfumes", "Vantair Rose Bliss EDP",
                "Bloom in every step.",
                "A feminine floral bouquet of Bulgarian rose, peony and soft sandalwood. Romantic and timeless.",
                List.of("Fresh floral notes", "Lasts 6–8 hours", "Perfect for daywear"),
                "Alcohol Denat., Aqua, Fragrance, Rose Otto, Peony Extract, Sandalwood Oil",
                1199, 1499, 4.7, 98, "New", "#ffb3c6", "🌹", true,
                List.of(variant("30ml", 749), variant("50ml", 1199), variant("100ml", 1999))));
        repo.save(product("P003", "perfumes", "Perfumes", "Vantair Ocean Breeze EDT",
                "Fresh as the sea, free as the wind.",
                "A crisp aquatic fragrance with notes of sea salt, bergamot and white cedar. Invigorating and clean.",
                List.of("Light aquatic scent", "Perfect for summer", "Office & daily wear"),
                "Alcohol Denat., Aqua, Fragrance, Bergamot Oil, Sea Salt Accord, Cedar Extract",
                999, 1299, 4.5, 76, null, "#0077b6", "🌊", false,
                List.of(variant("50ml", 999), variant("100ml", 1699))));
        repo.save(product("P004", "perfumes", "Perfumes", "Vantair Oud Royale EDP",
                "The scent of royalty.",
                "A luxurious blend of premium agarwood oud, saffron and warm vanilla. Rich, opulent and commanding.",
                List.of("Premium oud concentration", "Lasts 10–12 hours", "Luxury evening scent"),
                "Alcohol Denat., Aqua, Fragrance, Agarwood Oil, Saffron Extract, Vanilla Absolute",
                1599, 1999, 4.9, 156, "Premium", "#7b2d00", "👑", true,
                List.of(variant("30ml", 999), variant("50ml", 1599), variant("100ml", 2799))));
        repo.save(product("P005", "perfumes", "Perfumes", "Vantair Fresh Citrus EDT",
                "Zesty. Bright. Energising.",
                "A vibrant burst of lemon, grapefruit and green tea. The perfect morning fragrance for active lifestyles.",
                List.of("Energising citrus burst", "Light & refreshing", "Perfect for mornings"),
                "Alcohol Denat., Aqua, Fragrance, Lemon Oil, Grapefruit Extract, Green Tea Accord",
                899, 1099, 4.4, 63, null, "#f4d03f", "🍋", false,
                List.of(variant("50ml", 899), variant("100ml", 1499))));
        repo.save(product("P006", "perfumes", "Perfumes", "Vantair Velvet Musk EDP",
                "Soft. Sensual. Sophisticated.",
                "A delicate blend of white musk, cashmere wood and powdery iris. Intimate and deeply personal.",
                List.of("Skin-close sillage", "Clean musk signature", "All-day confidence"),
                "Alcohol Denat., Aqua, Fragrance, White Musk, Cashmere Wood, Iris Extract",
                1399, 1699, 4.6, 89, "Popular", "#d4a0c0", "🤍", true,
                List.of(variant("30ml", 899), variant("50ml", 1399), variant("100ml", 2399))));
        repo.save(product("P007", "perfumes", "Perfumes", "Vantair Pink Petal Body Mist",
                "Light, lovely and playful.",
                "A light body mist with notes of peach, jasmine and soft vanilla. Ideal for everyday freshness.",
                List.of("Lightweight formula", "Full body coverage", "Refreshing all day"),
                "Aqua, Alcohol Denat., Fragrance, Peach Extract, Jasmine Oil, Vanilla",
                599, 799, 4.3, 45, null, "#ffcce7", "🌸", false,
                List.of(variant("100ml", 599), variant("200ml", 999))));
        repo.save(product("P008", "perfumes", "Perfumes", "Vantair Midnight Roll-On",
                "Pocket-sized luxury.",
                "A concentrated roll-on perfume oil with oud and black pepper. Powerful, portable and long-lasting.",
                List.of("Alcohol-free formula", "Travel friendly", "Intense concentration"),
                "Fragrance Oil, Jojoba Oil, Oud Extract, Black Pepper CO2, Vitamin E",
                499, 649, 4.5, 112, "Travel Size", "#2c2c54", "🌙", true,
                List.of(variant("8ml", 499), variant("15ml", 799))));

        // ── Toiletries ────────────────────────────────────────────────
        repo.save(product("T001", "toiletries", "Toiletries", "Vantair Hydra Shampoo",
                "Deep hydration from root to tip.",
                "A nourishing shampoo enriched with hyaluronic acid and argan oil for deeply hydrated, frizz-free hair.",
                List.of("Sulfate-free formula", "Suitable for all hair types", "Adds shine & softness"),
                "Aqua, Sodium Lauryl Sulfoacetate, Hyaluronic Acid, Argan Oil, Panthenol, Keratin",
                399, 499, 4.6, 203, "Bestseller", "#48cae4", "💧", true,
                List.of(variant("200ml", 399), variant("400ml", 699))));
        repo.save(product("T002", "toiletries", "Toiletries", "Vantair Silk Conditioner",
                "Silky smooth, every wash.",
                "A rich conditioning treatment with silk proteins and coconut milk that leaves hair incredibly soft and manageable.",
                List.of("Deep conditioning", "Reduces breakage", "Tangle-free formula"),
                "Aqua, Cetearyl Alcohol, Silk Amino Acids, Coconut Milk, Shea Butter, Vitamin E",
                349, 449, 4.5, 167, null, "#f8edeb", "🥥", true,
                List.of(variant("200ml", 349), variant("400ml", 599))));
        repo.save(product("T003", "toiletries", "Toiletries", "Vantair Charcoal Body Wash",
                "Deep cleanse. Pure skin.",
                "Activated charcoal body wash that draws out impurities while leaving skin feeling clean, refreshed and moisturised.",
                List.of("Deep pore cleansing", "Removes toxins", "Skin detox formula"),
                "Aqua, Activated Charcoal, Sodium Cocoyl Isethionate, Tea Tree Oil, Aloe Vera",
                449, 599, 4.7, 189, "Popular", "#2d2d2d", "🖤", false,
                List.of(variant("200ml", 449), variant("400ml", 799))));
        repo.save(product("T004", "toiletries", "Toiletries", "Vantair Moisturising Soap Bar",
                "Clean skin, happy skin.",
                "A gentle moisturising soap bar enriched with shea butter and glycerin. Leaves skin soft and hydrated.",
                List.of("Moisturises while cleansing", "Gentle on sensitive skin", "Long-lasting bar"),
                "Sodium Palmate, Shea Butter, Glycerin, Coconut Oil, Vitamin E, Aloe Extract",
                199, 249, 4.3, 312, null, "#ffd6a5", "🧼", false,
                List.of(variant("75g", 199), variant("150g", 349))));
        repo.save(product("T005", "toiletries", "Toiletries", "Vantair Brightening Face Wash",
                "Reveal your glow every morning.",
                "A gentle brightening face wash with vitamin C and niacinamide that cleanses, brightens and evens skin tone.",
                List.of("Brightens dull skin", "Gentle daily cleanser", "Suitable for all skin types"),
                "Aqua, Sodium Cocoyl Glutamate, Vitamin C, Niacinamide, Hyaluronic Acid, Green Tea",
                299, 399, 4.6, 245, "New", "#fff3b0", "✨", true,
                List.of(variant("100ml", 299), variant("200ml", 499))));
        repo.save(product("T006", "toiletries", "Toiletries", "Vantair Anti-Dandruff Shampoo",
                "Flake-free, confident hair.",
                "A targeted anti-dandruff formula with zinc pyrithione and tea tree oil to combat flakes and soothe the scalp.",
                List.of("Eliminates dandruff", "Soothes scalp", "Strengthens hair"),
                "Aqua, Sodium Laureth Sulfate, Zinc Pyrithione, Tea Tree Oil, Salicylic Acid, Biotin",
                429, 549, 4.5, 178, null, "#74b9ff", "💆", false,
                List.of(variant("200ml", 429), variant("400ml", 749))));
        repo.save(product("T007", "toiletries", "Toiletries", "Vantair Deep Cleanse Scrub",
                "Buff away the dull.",
                "A physical body scrub with walnut shell powder and coffee grounds that exfoliates dead skin and boosts circulation.",
                List.of("Deep exfoliation", "Improves skin texture", "Boosts circulation"),
                "Aqua, Walnut Shell Powder, Coffee Grounds, Coconut Oil, Shea Butter, Vitamin C",
                379, 499, 4.4, 134, null, "#6d4c41", "☕", false,
                List.of(variant("150g", 379), variant("300g", 649))));
        repo.save(product("T008", "toiletries", "Toiletries", "Vantair Aloe Shower Gel",
                "Soothe. Refresh. Glow.",
                "A calming aloe vera shower gel that cleanses gently while soothing irritated or sensitive skin.",
                List.of("Soothes sensitive skin", "Cooling formula", "Dermatologically tested"),
                "Aqua, Aloe Barbadensis Leaf Juice, Sodium Cocoyl Isethionate, Cucumber Extract, Vitamin B5",
                329, 429, 4.4, 156, null, "#b7e4c7", "🌿", false,
                List.of(variant("200ml", 329), variant("400ml", 549))));

        // ── Skin Care ─────────────────────────────────────────────────
        repo.save(product("S001", "skincare", "Skin Care", "Vantair Glow Moisturiser",
                "Wake up to glowing skin.",
                "A lightweight daily moisturiser with hyaluronic acid and vitamin C that hydrates and brightens for a natural glow.",
                List.of("24hr hydration", "Brightens skin tone", "Non-greasy formula"),
                "Aqua, Hyaluronic Acid, Vitamin C, Niacinamide, Glycerin, Jojoba Oil, SPF15",
                599, 799, 4.8, 287, "Bestseller", "#ffe8d6", "🌟", true,
                List.of(variant("50ml", 599), variant("100ml", 999))));
        repo.save(product("S002", "skincare", "Skin Care", "Vantair Vitamin C Serum",
                "Brighten. Firm. Protect.",
                "A potent 15% vitamin C serum with ferulic acid and vitamin E that fades dark spots, firms skin and provides antioxidant protection.",
                List.of("Fades dark spots", "Firms & tightens", "Antioxidant protection"),
                "Aqua, L-Ascorbic Acid 15%, Ferulic Acid, Vitamin E, Hyaluronic Acid, Niacinamide",
                799, 1099, 4.9, 342, "Premium", "#f6c90e", "🍊", true,
                List.of(variant("30ml", 799), variant("50ml", 1199))));
        repo.save(product("S003", "skincare", "Skin Care", "Vantair SPF50 Sunscreen",
                "Shield your glow.",
                "A lightweight, non-sticky SPF50 PA+++ sunscreen that protects against UVA and UVB rays while keeping skin matte.",
                List.of("Broad spectrum SPF50", "Non-sticky, matte finish", "Suitable under makeup"),
                "Aqua, Zinc Oxide, Titanium Dioxide, Niacinamide, Hyaluronic Acid, Aloe Vera",
                499, 699, 4.7, 198, "Popular", "#fff9c4", "☀️", true,
                List.of(variant("50g", 499), variant("100g", 849))));
        repo.save(product("S004", "skincare", "Skin Care", "Vantair Hydra Face Mask",
                "Spa treatment at home.",
                "A deeply hydrating sheet mask infused with hyaluronic acid, collagen and aloe vera for plump, dewy skin.",
                List.of("Intense hydration boost", "Plumps & smooths", "Visible results in 20min"),
                "Aqua, Hyaluronic Acid, Hydrolyzed Collagen, Aloe Vera, Niacinamide, Centella Asiatica",
                349, 449, 4.6, 167, null, "#d0f4de", "🌊", false,
                List.of(variant("Single", 349), variant("Pack of 5", 1499))));
        repo.save(product("S005", "skincare", "Skin Care", "Vantair Rose Toner",
                "Refresh, balance, bloom.",
                "A balancing facial toner with Bulgarian rose water and witch hazel that tightens pores and refreshes skin.",
                List.of("Tightens pores", "Balances skin pH", "Refreshes instantly"),
                "Rosa Damascena Flower Water, Witch Hazel, Niacinamide, Hyaluronic Acid, Glycerin",
                449, 599, 4.5, 143, null, "#ffb3c6", "🌹", false,
                List.of(variant("100ml", 449), variant("200ml", 749))));
        repo.save(product("S006", "skincare", "Skin Care", "Vantair Night Repair Cream",
                "Repair while you rest.",
                "An intensive overnight repair cream with retinol, peptides and shea butter that renews skin while you sleep.",
                List.of("Overnight cell renewal", "Reduces fine lines", "Deep nourishment"),
                "Aqua, Retinol 0.3%, Peptide Complex, Shea Butter, Ceramides, Squalane, Vitamin E",
                699, 999, 4.8, 234, "Premium", "#2c2c54", "🌙", true,
                List.of(variant("50ml", 699), variant("100ml", 1199))));
        repo.save(product("S007", "skincare", "Skin Care", "Vantair Under Eye Gel",
                "Bye bye dark circles.",
                "A cooling under-eye gel with caffeine, vitamin K and peptides that reduces puffiness and dark circles overnight.",
                List.of("Reduces dark circles", "Depuffs instantly", "Cooling gel formula"),
                "Aqua, Caffeine 5%, Vitamin K, Peptide Complex, Hyaluronic Acid, Cucumber Extract",
                549, 749, 4.6, 189, "New", "#74b9ff", "👁️", true,
                List.of(variant("15ml", 549), variant("30ml", 899))));
        repo.save(product("S008", "skincare", "Skin Care", "Vantair Retinol Serum",
                "The gold standard of anti-ageing.",
                "A clinically formulated 0.5% retinol serum with bakuchiol and peptides for visible wrinkle reduction and skin renewal.",
                List.of("Reduces wrinkles", "Improves skin texture", "Boosts collagen"),
                "Aqua, Retinol 0.5%, Bakuchiol, Peptide Complex, Hyaluronic Acid, Niacinamide",
                899, 1299, 4.9, 276, "Premium", "#f8c471", "⭐", true,
                List.of(variant("30ml", 899), variant("50ml", 1399))));

        // ── Cosmetics ─────────────────────────────────────────────────
        repo.save(product("C001", "cosmetics", "Cosmetics", "Vantair Velvet Lipstick",
                "Colour that commands.",
                "A highly pigmented matte lipstick with a velvet finish. Moisturising formula that lasts all day without drying lips.",
                List.of("8hr wear formula", "Non-drying matte", "Highly pigmented"),
                "Ricinus Communis Oil, Candelilla Wax, Vitamin E, Shea Butter, Iron Oxides",
                499, 649, 4.7, 312, "Bestseller", "#c0392b", "💄", true,
                List.of(variant("Ruby Red", 499), variant("Nude Beige", 499), variant("Berry Wine", 499),
                        variant("Coral Bliss", 499), variant("Deep Plum", 499))));
        repo.save(product("C002", "cosmetics", "Cosmetics", "Vantair HD Foundation",
                "Flawless skin. All day.",
                "A buildable, full-coverage HD foundation with SPF20 that blurs imperfections and stays fresh for 16 hours.",
                List.of("16hr wear", "Full coverage", "SPF20 protection"),
                "Aqua, Cyclopentasiloxane, Titanium Dioxide, Niacinamide, Hyaluronic Acid, Iron Oxides",
                799, 1099, 4.6, 234, "Popular", "#d4a574", "🪞", true,
                List.of(variant("N10 Ivory", 799), variant("N20 Porcelain", 799), variant("W30 Beige", 799),
                        variant("W40 Sand", 799), variant("C50 Caramel", 799), variant("C60 Mocha", 799))));
        repo.save(product("C003", "cosmetics", "Cosmetics", "Vantair Kajal Intense",
                "Define. Intensify. Captivate.",
                "A deeply pigmented, waterproof kajal pencil that glides on smoothly and stays smudge-proof all day.",
                List.of("Waterproof formula", "Smudge-proof", "Intense black pigment"),
                "Cyclopentasiloxane, Carbon Black, Beeswax, Ozokerite, Carnauba Wax, Vitamin E",
                299, 399, 4.8, 445, "Bestseller", "#1a1a1a", "✏️", true,
                List.of(variant("Jet Black", 299), variant("Brown", 299), variant("Navy Blue", 299))));
        repo.save(product("C004", "cosmetics", "Cosmetics", "Vantair Liquid Eyeliner",
                "Sharp lines. Bold eyes.",
                "A precision liquid eyeliner with a fine felt tip that delivers sharp, defined lines. Waterproof and long-lasting.",
                List.of("Precise felt tip", "Waterproof formula", "Dries in seconds"),
                "Aqua, Acrylates Copolymer, Carbon Black, Styrene/Acrylates Copolymer, Glycerin",
                349, 449, 4.7, 267, null, "#2d3436", "🖊️", false,
                List.of(variant("Jet Black", 349), variant("Brown", 349))));
        repo.save(product("C005", "cosmetics", "Cosmetics", "Vantair Compact Powder",
                "Set. Blur. Perfect.",
                "A silky setting powder that controls shine, blurs pores and sets makeup for a flawless, long-lasting finish.",
                List.of("Controls shine", "Blurs pores", "Sets makeup all day"),
                "Talc, Mica, Silica, Nylon-12, Zinc Stearate, Titanium Dioxide, Iron Oxides",
                449, 599, 4.5, 178, null, "#f5cba7", "🪄", false,
                List.of(variant("Translucent", 449), variant("Light Beige", 449), variant("Medium Tan", 449),
                        variant("Deep Brown", 449))));
        repo.save(product("C006", "cosmetics", "Cosmetics", "Vantair Blush Palette",
                "Flush of confidence.",
                "A 4-shade blush palette with buildable, natural-looking shades from soft pink to warm coral.",
                List.of("4 complementary shades", "Buildable coverage", "Natural satin finish"),
                "Talc, Mica, Iron Oxides, Magnesium Stearate, Silica, Titanium Dioxide, Carmine",
                699, 899, 4.6, 145, "New", "#ffb3ba", "🌸", false,
                List.of(variant("Rosewood Edit", 699), variant("Sunset Edit", 699))));
        repo.save(product("C007", "cosmetics", "Cosmetics", "Vantair Eyeshadow Palette",
                "12 shades. Infinite looks.",
                "A versatile 12-pan eyeshadow palette with matte, shimmer and glitter finishes from neutral to bold.",
                List.of("12 curated shades", "Matte + shimmer + glitter", "Long-wearing formula"),
                "Talc, Mica, Iron Oxides, Silica, Carmine, Ultramarines, Chromium Oxide, Bismuth Oxychloride",
                899, 1299, 4.8, 234, "Premium", "#a29bfe", "🎨", true,
                List.of(variant("Nude Neutrals", 899), variant("Smoky Drama", 899), variant("Colourful Pop", 899))));
        repo.save(product("C008", "cosmetics", "Cosmetics", "Vantair Brow Definer",
                "Frame your face.",
                "A micro-precision brow pencil with a spoolie brush that creates natural, hair-like brow strokes.",
                List.of("Micro-precision tip", "Natural hair-like strokes", "Built-in spoolie"),
                "Cyclopentasiloxane, Synthetic Wax, Iron Oxides, Vitamin E, Carnauba Wax",
                399, 499, 4.5, 167, null, "#795548", "🖌️", false,
                List.of(variant("Soft Black", 399), variant("Dark Brown", 399), variant("Medium Brown", 399),
                        variant("Taupe", 399))));
    }

    // ── builders ────────────────────────────────────────────────────────
    private Category category(String id, String label, String emoji, String desc, String color) {
        Category c = new Category();
        c.setId(id);
        c.setLabel(label);
        c.setEmoji(emoji);
        c.setDescription(desc);
        c.setColor(color);
        return c;
    }

    private Coupon coupon(String code, String type, int value, int minOrder, String desc) {
        Coupon c = new Coupon();
        c.setCode(code);
        c.setType(type);
        c.setValue(value);
        c.setMinOrder(minOrder);
        c.setDescription(desc);
        c.setActive(true);
        return c;
    }

    private ProductVariant variant(String label, int price) {
        ProductVariant v = new ProductVariant();
        v.setLabel(label);
        v.setPrice(price);
        return v;
    }

    private Product product(String id, String category, String categoryLabel, String name, String tagline,
                            String description, List<String> benefits, String ingredients, int price, int mrp,
                            double rating, int reviews, String badge, String color, String emoji,
                            boolean expressEligible, List<ProductVariant> variants) {
        Product p = new Product();
        p.setId(id);
        p.setCategory(category);
        p.setCategoryLabel(categoryLabel);
        p.setName(name);
        p.setTagline(tagline);
        p.setDescription(description);
        p.setBenefits(benefits);
        p.setIngredients(ingredients);
        p.setPrice(price);
        p.setMrp(mrp);
        p.setRating(rating);
        p.setReviews(reviews);
        p.setBadge(badge);
        p.setColor(color);
        p.setEmoji(emoji);
        p.setExpressEligible(expressEligible);
        variants.forEach(p::addVariant);
        return p;
    }
}
