package com.vantair.api.service;

import com.vantair.api.config.ApiException;
import com.vantair.api.dto.Dtos.AddressRequest;
import com.vantair.api.dto.Dtos.ChangePasswordRequest;
import com.vantair.api.dto.Dtos.SignInRequest;
import com.vantair.api.dto.Dtos.SignUpRequest;
import com.vantair.api.dto.Dtos.UpdateProfileRequest;
import com.vantair.api.model.Address;
import com.vantair.api.model.User;
import com.vantair.api.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final int MAX_ADDRESSES = 3;

    private final UserRepository users;
    private final PasswordHasher hasher;

    public UserService(UserRepository users, PasswordHasher hasher) {
        this.users = users;
        this.hasher = hasher;
    }

    @Transactional
    public User signUp(SignUpRequest req) {
        if (users.existsByEmailIgnoreCase(req.email())) {
            throw ApiException.conflict("Email already registered. Please sign in.");
        }
        User user = new User();
        user.setName(req.name());
        user.setEmail(req.email());
        user.setPhone(req.phone());
        user.setPhoto(req.photo());
        user.setNewsletter(req.newsletter());
        user.setPasswordHash(hasher.hash(req.password()));

        if (req.address() != null) {
            Address addr = toAddress(req.address());
            addr.setDefault(true);
            user.addAddress(addr);
        }
        return users.save(user);
    }

    @Transactional(readOnly = true)
    public User signIn(SignInRequest req) {
        User user = users.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> ApiException.unauthorized("No account found with this email."));
        if (!hasher.matches(req.password(), user.getPasswordHash())) {
            throw ApiException.unauthorized("Incorrect password.");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User get(Long id) {
        return users.findById(id)
                .orElseThrow(() -> ApiException.notFound("User not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<User> listAll() {
        return users.findAll();
    }

    @Transactional
    public User updateProfile(Long id, UpdateProfileRequest req) {
        User user = get(id);
        if (req.name() != null) {
            user.setName(req.name());
        }
        if (req.phone() != null) {
            user.setPhone(req.phone());
        }
        if (req.photo() != null) {
            user.setPhoto(req.photo());
        }
        if (req.newsletter() != null) {
            user.setNewsletter(req.newsletter());
        }
        return users.save(user);
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordRequest req) {
        User user = get(id);
        if (!hasher.matches(req.currentPassword(), user.getPasswordHash())) {
            throw ApiException.badRequest("Current password is incorrect.");
        }
        user.setPasswordHash(hasher.hash(req.newPassword()));
        users.save(user);
    }

    // ── Addresses ───────────────────────────────────────────────────────
    @Transactional
    public User addAddress(Long userId, AddressRequest req) {
        User user = get(userId);
        if (user.getAddresses().size() >= MAX_ADDRESSES) {
            throw ApiException.badRequest("Maximum " + MAX_ADDRESSES + " addresses allowed.");
        }
        Address addr = toAddress(req);
        if (user.getAddresses().isEmpty()) {
            addr.setDefault(true);
        }
        user.addAddress(addr);
        return users.save(user);
    }

    @Transactional
    public User removeAddress(Long userId, Long addressId) {
        User user = get(userId);
        boolean removingDefault = user.getAddresses().stream()
                .anyMatch(a -> a.getId().equals(addressId) && a.isDefault());
        user.getAddresses().removeIf(a -> a.getId().equals(addressId));
        // Promote a new default if we removed the default one.
        if (removingDefault && !user.getAddresses().isEmpty()) {
            user.getAddresses().get(0).setDefault(true);
        }
        return users.save(user);
    }

    @Transactional
    public User setDefaultAddress(Long userId, Long addressId) {
        User user = get(userId);
        boolean found = false;
        for (Address a : user.getAddresses()) {
            boolean isMatch = a.getId().equals(addressId);
            a.setDefault(isMatch);
            found = found || isMatch;
        }
        if (!found) {
            throw ApiException.notFound("Address not found: " + addressId);
        }
        return users.save(user);
    }

    // ── Wishlist ────────────────────────────────────────────────────────
    @Transactional
    public List<String> toggleWishlist(Long userId, String productId) {
        User user = get(userId);
        if (user.getWishlist().contains(productId)) {
            user.getWishlist().remove(productId);
        } else {
            user.getWishlist().add(productId);
        }
        users.save(user);
        return user.getWishlist();
    }

    private Address toAddress(AddressRequest req) {
        Address addr = new Address();
        addr.setLabel(req.label());
        addr.setName(req.name());
        addr.setPhone(req.phone());
        addr.setLine1(req.line1());
        addr.setCity(req.city());
        addr.setState(req.state());
        addr.setPincode(req.pincode());
        return addr;
    }
}
