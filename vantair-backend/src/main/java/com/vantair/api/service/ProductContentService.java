package com.vantair.api.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Generates a marketing tagline and description for a product from its name and
 * category. A self-contained, category-aware template generator — no external AI
 * service or API key required, so it works offline and instantly.
 */
@Service
public class ProductContentService {

    /** Category slug → phrasing used to colour the generated copy. */
    private static final Map<String, String[]> CATEGORY_WORDS = Map.of(
            "perfumes", new String[]{"fragrance", "scent", "long-lasting notes", "signature aroma"},
            "toiletries", new String[]{"daily care", "cleansing formula", "fresh feel", "everyday essential"},
            "skincare", new String[]{"skincare", "nourishing formula", "radiant glow", "visible results"},
            "cosmetics", new String[]{"makeup", "rich pigment", "all-day wear", "flawless finish"});

    private static final String[] TAGLINE_TEMPLATES = {
            "%s. Crafted for you.",
            "Experience %s like never before.",
            "Your everyday %s, perfected.",
            "Premium %s, the Vantair way."
    };

    public Map<String, String> generate(String name, String category) {
        String cleanName = name == null ? "" : name.trim();
        String[] words = CATEGORY_WORDS.getOrDefault(category == null ? "" : category,
                new String[]{"product", "premium quality", "lasting performance", "trusted formula"});

        // Pick a template deterministically from the name so repeat calls are stable.
        int idx = Math.floorMod(cleanName.hashCode(), TAGLINE_TEMPLATES.length);
        String subject = cleanName.isEmpty() ? "luxury" : cleanName;
        String tagline = String.format(TAGLINE_TEMPLATES[idx], capitalize(subject));

        String displayName = cleanName.isEmpty() ? "This product" : cleanName;
        String description = String.format(
                "%s is a premium %s from Vantair, designed for those who expect more. "
                        + "Enjoy its %s and %s with every use. "
                        + "Thoughtfully formulated and quality-tested, it delivers %s you can rely on, day after day.",
                displayName, words[0], words[1], words[2], words[3]);

        return Map.of("tagline", tagline, "description", description,
                "benefits", String.join("|", suggestedBenefits(words)));
    }

    private List<String> suggestedBenefits(String[] words) {
        return List.of(
                capitalize(words[1]),
                capitalize(words[2]),
                "Premium Vantair quality");
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
