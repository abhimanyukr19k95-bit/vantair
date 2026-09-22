package com.vantair.api.service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Delivery charge and ETA rules, ported from the frontend's data.js so the
 * backend is the single source of truth for money and dates.
 */
@Component
public class DeliveryService {

    public static final int FREE_ABOVE = 999;
    public static final int STANDARD_CHARGE = 49;
    public static final int COD_CHARGE = 40;
    public static final int EXPRESS_CHARGE = 149;

    private static final Set<String> EXPRESS_REGIONS = Set.of(
            "700001", "700002", "700003", "700004", "700005", "700006", "700007", "700008", "700009", "700010",
            "700011", "700012", "700013", "700014", "700015", "700016", "700017", "700018", "700019", "700020",
            "711101", "711102", "711103", "711104", "711105", "741101", "741102", "741103", "743101", "743102");

    private static final Map<String, Integer> REGION_DAYS = Map.ofEntries(
            Map.entry("WB", 1), Map.entry("OR", 2), Map.entry("JH", 2), Map.entry("BR", 2), Map.entry("AS", 3),
            Map.entry("UP", 3), Map.entry("DL", 3), Map.entry("MH", 4), Map.entry("KA", 4), Map.entry("TN", 5),
            Map.entry("AP", 4), Map.entry("TS", 4), Map.entry("KL", 5), Map.entry("GJ", 4), Map.entry("RJ", 4),
            Map.entry("MP", 3), Map.entry("CG", 3), Map.entry("DEFAULT", 5));

    private static final Map<String, String> PREFIX_STATE = Map.ofEntries(
            Map.entry("700", "WB"), Map.entry("711", "WB"), Map.entry("712", "WB"), Map.entry("713", "WB"),
            Map.entry("741", "WB"), Map.entry("742", "WB"), Map.entry("743", "WB"),
            Map.entry("751", "OR"), Map.entry("800", "BR"), Map.entry("811", "JH"),
            Map.entry("110", "DL"), Map.entry("400", "MH"), Map.entry("500", "TS"), Map.entry("600", "TN"),
            Map.entry("560", "KA"), Map.entry("682", "KL"), Map.entry("380", "GJ"), Map.entry("302", "RJ"),
            Map.entry("226", "UP"), Map.entry("462", "MP"), Map.entry("492", "CG"));

    public boolean isExpressAvailable(String pincode) {
        return pincode != null && EXPRESS_REGIONS.contains(pincode);
    }

    /** Delivery charge before any free-shipping coupon, given subtotal and mode. */
    public int deliveryCharge(int subtotal, boolean express) {
        if (express) {
            return EXPRESS_CHARGE;
        }
        return subtotal >= FREE_ABOVE ? 0 : STANDARD_CHARGE;
    }

    public String expectedDelivery(String pincode, boolean express, String expressType) {
        LocalDate date = LocalDate.now();
        if (express) {
            if ("Next Day".equalsIgnoreCase(expressType)) {
                date = date.plusDays(1);
            }
            return format(date);
        }
        String prefix = (pincode != null && pincode.length() >= 3) ? pincode.substring(0, 3) : "700";
        String state = PREFIX_STATE.getOrDefault(prefix, "DEFAULT");
        int days = REGION_DAYS.getOrDefault(state, 5);
        return format(date.plusDays(days));
    }

    private String format(LocalDate date) {
        String weekday = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return weekday + ", " + date.getDayOfMonth() + " " + month;
    }
}
