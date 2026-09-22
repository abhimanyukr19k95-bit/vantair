package com.vantair.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Request/response payloads for the API, grouped in one file for easy scanning.
 * The {@code @Schema(example = ...)} annotations drive the pre-filled example
 * bodies shown in Swagger UI.
 */
public final class Dtos {

    private Dtos() {
    }

    // ── Auth ────────────────────────────────────────────────────────────
    public record SignUpRequest(
            @NotBlank @Schema(example = "Priya Sharma") String name,
            @Email @NotBlank @Schema(example = "priya@example.com") String email,
            @Schema(example = "+91 90888 41800") String phone,
            @NotBlank @Schema(example = "secret123") String password,
            @Schema(description = "Base64 profile photo (optional)") String photo,
            @Schema(example = "true") boolean newsletter,
            AddressRequest address) {
    }

    public record SignInRequest(
            @Email @NotBlank @Schema(example = "priya@example.com") String email,
            @NotBlank @Schema(example = "secret123") String password) {
    }

    public record ChangePasswordRequest(
            @NotBlank @Schema(example = "secret123") String currentPassword,
            @NotBlank @Schema(example = "newSecret456") String newPassword) {
    }

    public record UpdateProfileRequest(
            @Schema(example = "Priya S. Sharma") String name,
            @Schema(example = "+91 90888 41800") String phone,
            String photo,
            @Schema(example = "false") Boolean newsletter) {
    }

    // ── Address ─────────────────────────────────────────────────────────
    public record AddressRequest(
            @Schema(example = "Home") String label,
            @NotBlank @Schema(example = "Priya Sharma") String name,
            @Schema(example = "+91 90888 41800") String phone,
            @NotBlank @Schema(example = "12 MG Road, Park Street Area") String line1,
            @NotBlank @Schema(example = "Kolkata") String city,
            @Schema(example = "WB") String state,
            @NotBlank @Schema(example = "700001") String pincode) {
    }

    // ── Cart / checkout ─────────────────────────────────────────────────
    public record CartItemRequest(
            @NotBlank @Schema(example = "P001") String productId,
            @Min(0) @Schema(example = "1", description = "Index into the product's variants list") int variantIndex,
            @Min(1) @Schema(example = "2") int qty) {
    }

    public record PlaceOrderRequest(
            @NotNull @Schema(example = "1") Long userId,
            @NotNull @Schema(example = "1") Long addressId,
            @NotBlank @Schema(example = "upi", allowableValues = {"upi", "cod"}) String payment,
            @Schema(example = "VANTAIR10", description = "Optional coupon code") String couponCode,
            @Schema(example = "standard", description = "standard | express (Same Day) | express2 (Next Day)")
            String deliveryType,
            @NotNull List<CartItemRequest> items) {
    }

    public record UpdateOrderStatusRequest(
            @NotBlank
            @Schema(example = "Shipped",
                    allowableValues = {"Confirmed", "Processing", "Shipped", "OutForDelivery", "Delivered", "Cancelled"})
            String status) {
    }

    public record ValidateOtpRequest(
            @NotBlank @Schema(example = "123456", description = "6-digit delivery OTP") String otp) {
    }

    public record CouponApplyRequest(
            @NotBlank @Schema(example = "VANTAIR10") String code,
            @Min(0) @Schema(example = "1000", description = "Cart subtotal in INR") int subtotal) {
    }

    // ── Marketing / support ─────────────────────────────────────────────
    public record NewsletterRequest(
            @Email @NotBlank @Schema(example = "subscriber@example.com") String email) {
    }

    public record ContactRequest(
            @NotBlank @Schema(example = "Rahul Das") String name,
            @Email @NotBlank @Schema(example = "rahul@example.com") String email,
            @Schema(example = "+91 82402 07252") String phone,
            @Schema(example = "General Enquiry") String type,
            @NotBlank @Schema(example = "I'd like to know more about your express delivery.") String message) {
    }

    public record PartnerRequest(
            @NotBlank @Schema(example = "Anjali Mehta") String name,
            @Schema(example = "Mehta Retail Pvt Ltd") String company,
            @Email @NotBlank @Schema(example = "anjali@mehtaretail.com") String email,
            @Schema(example = "+91 90888 41800") String phone,
            @Schema(example = "Reseller") String type,
            @Schema(example = "500 units/month") String volume,
            @Schema(example = "Interested in bulk perfume orders.") String message) {
    }

    // ── Refunds ─────────────────────────────────────────────────────────
    public record IssueRefundRequest(
            @Email @NotBlank @Schema(example = "priya@example.com",
                    description = "Email of the customer to credit") String email,
            @NotNull @Min(1) @Schema(example = "500", description = "Refund value in INR") Integer amount,
            @Schema(example = "Damaged item returned at delivery") String reason) {
    }

    /** Admin-facing view of a refund coupon, flattened with the customer it belongs to. */
    public record RefundCouponResponse(
            Long id, String code, Integer value, String reason, boolean used,
            String issuedAt, String customerName, String customerEmail) {
    }

    // ── Analytics ───────────────────────────────────────────────────────
    public record TrackEventRequest(
            @NotBlank @Schema(example = "/shop.html", description = "Page path being viewed") String path,
            @Schema(example = "Shop — Vantair") String title,
            @Schema(example = "sess_ab12cd34", description = "Anonymous client session id") String sessionId,
            @Schema(example = "1", description = "Signed-in user id, if any") Long userId,
            @Schema(example = "mobile", description = "mobile | tablet | desktop") String device,
            @Schema(example = "https://google.com", description = "document.referrer") String referrer) {
    }

    // ── Ads ─────────────────────────────────────────────────────────────
    public record AdRequest(
            @NotBlank @Schema(example = "Monsoon Sale") String title,
            @Schema(example = "Flat 20% off all perfumes this week") String text,
            @Schema(example = "🌧️", description = "Emoji or image URL") String image,
            @Schema(example = "shop.html?category=perfumes") String link,
            @Schema(example = "home", description = "Placement slot: home | shop") String slot,
            @Schema(example = "true") Boolean active) {
    }

    // ── Product content generation ──────────────────────────────────────
    public record GenerateContentRequest(
            @NotBlank @Schema(example = "Vantair Citrus Bloom EDP") String name,
            @Schema(example = "perfumes") String category) {
    }

    // ── Generic responses ───────────────────────────────────────────────
    public record MessageResponse(String message) {
    }

    public record CouponResult(boolean valid, int discount, String message, String code, String type) {
    }
}
