package com.vantair.api.controller;

import com.vantair.api.dto.Dtos.AddressRequest;
import com.vantair.api.dto.Dtos.ChangePasswordRequest;
import com.vantair.api.dto.Dtos.MessageResponse;
import com.vantair.api.dto.Dtos.UpdateProfileRequest;
import com.vantair.api.model.User;
import com.vantair.api.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Profile, addresses and wishlist")
public class UserController {

    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        return users.get(id);
    }

    @PutMapping("/{id}")
    public User updateProfile(@PathVariable Long id, @Valid @RequestBody UpdateProfileRequest req) {
        return users.updateProfile(id, req);
    }

    @PostMapping("/{id}/change-password")
    public MessageResponse changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest req) {
        users.changePassword(id, req);
        return new MessageResponse("Password updated.");
    }

    // ── Addresses ───────────────────────────────────────────────────────
    @PostMapping("/{id}/addresses")
    public User addAddress(@PathVariable Long id, @Valid @RequestBody AddressRequest req) {
        return users.addAddress(id, req);
    }

    @DeleteMapping("/{id}/addresses/{addressId}")
    public User removeAddress(@PathVariable Long id, @PathVariable Long addressId) {
        return users.removeAddress(id, addressId);
    }

    @PutMapping("/{id}/addresses/{addressId}/default")
    public User setDefaultAddress(@PathVariable Long id, @PathVariable Long addressId) {
        return users.setDefaultAddress(id, addressId);
    }

    // ── Wishlist ────────────────────────────────────────────────────────
    @PostMapping("/{id}/wishlist/{productId}")
    public List<String> toggleWishlist(@PathVariable Long id, @PathVariable String productId) {
        return users.toggleWishlist(id, productId);
    }
}
