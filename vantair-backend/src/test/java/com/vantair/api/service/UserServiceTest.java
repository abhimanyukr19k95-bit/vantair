package com.vantair.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.vantair.api.TestFixtures;
import com.vantair.api.config.ApiException;
import com.vantair.api.dto.Dtos.AddressRequest;
import com.vantair.api.dto.Dtos.ChangePasswordRequest;
import com.vantair.api.dto.Dtos.SignInRequest;
import com.vantair.api.dto.Dtos.SignUpRequest;
import com.vantair.api.dto.Dtos.UpdateProfileRequest;
import com.vantair.api.model.Address;
import com.vantair.api.model.User;
import com.vantair.api.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository users;
    @Mock PasswordHasher hasher;
    @InjectMocks UserService service;

    private AddressRequest addrReq() {
        return new AddressRequest("Home", "Tester", "+91 90000 00000", "1 Test Rd", "Kolkata", "WB", "700001");
    }

    @BeforeEach
    void echoSave() {
        lenient().when(users.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
    }

    // ── signUp ────────────────────────────────────────────────
    @Test
    void signUp_newEmail_savesHashedWithDefaultAddress() {
        when(users.existsByEmailIgnoreCase("a@b.com")).thenReturn(false);
        when(hasher.hash("pw")).thenReturn("HASHED");
        SignUpRequest req = new SignUpRequest("Tester", "a@b.com", "+91", "pw", null, true, addrReq());

        User saved = service.signUp(req);

        assertThat(saved.getPasswordHash()).isEqualTo("HASHED");
        assertThat(saved.isNewsletter()).isTrue();
        assertThat(saved.getAddresses()).hasSize(1);
        assertThat(saved.getAddresses().get(0).isDefault()).isTrue();
    }

    @Test
    void signUp_noAddress_ok() {
        when(users.existsByEmailIgnoreCase("a@b.com")).thenReturn(false);
        when(hasher.hash(any())).thenReturn("H");
        SignUpRequest req = new SignUpRequest("T", "a@b.com", null, "pw", null, false, null);

        assertThat(service.signUp(req).getAddresses()).isEmpty();
    }

    @Test
    void signUp_duplicate_throwsConflict() {
        when(users.existsByEmailIgnoreCase("a@b.com")).thenReturn(true);
        SignUpRequest req = new SignUpRequest("T", "a@b.com", null, "pw", null, false, null);

        assertThatThrownBy(() -> service.signUp(req))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    // ── signIn ────────────────────────────────────────────────
    @Test
    void signIn_ok() {
        User u = TestFixtures.user(1L, "a@b.com");
        when(users.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.of(u));
        when(hasher.matches("pw", "hash")).thenReturn(true);

        assertThat(service.signIn(new SignInRequest("a@b.com", "pw"))).isSameAs(u);
    }

    @Test
    void signIn_unknownEmail_unauthorized() {
        when(users.findByEmailIgnoreCase("x@b.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.signIn(new SignInRequest("x@b.com", "pw")))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void signIn_wrongPassword_unauthorized() {
        User u = TestFixtures.user(1L, "a@b.com");
        when(users.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.of(u));
        when(hasher.matches("bad", "hash")).thenReturn(false);

        assertThatThrownBy(() -> service.signIn(new SignInRequest("a@b.com", "bad")))
                .isInstanceOf(ApiException.class);
    }

    // ── get / listAll ─────────────────────────────────────────
    @Test
    void get_missing_notFound() {
        when(users.findById(9L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(9L)).isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void listAll_delegates() {
        when(users.findAll()).thenReturn(java.util.List.of(TestFixtures.user(1L, "a@b.com")));
        assertThat(service.listAll()).hasSize(1);
    }

    // ── updateProfile ─────────────────────────────────────────
    @Test
    void updateProfile_onlyNonNullFieldsApplied() {
        User u = TestFixtures.user(1L, "a@b.com");
        u.setName("Old");
        u.setNewsletter(false);
        when(users.findById(1L)).thenReturn(Optional.of(u));

        User out = service.updateProfile(1L, new UpdateProfileRequest("New", "+91 1", "pic", true));

        assertThat(out.getName()).isEqualTo("New");
        assertThat(out.getPhone()).isEqualTo("+91 1");
        assertThat(out.getPhoto()).isEqualTo("pic");
        assertThat(out.isNewsletter()).isTrue();
    }

    @Test
    void updateProfile_allNull_isNoOp() {
        User u = TestFixtures.user(1L, "a@b.com");
        u.setName("Keep");
        when(users.findById(1L)).thenReturn(Optional.of(u));

        User out = service.updateProfile(1L, new UpdateProfileRequest(null, null, null, null));
        assertThat(out.getName()).isEqualTo("Keep");
    }

    // ── changePassword ────────────────────────────────────────
    @Test
    void changePassword_ok() {
        User u = TestFixtures.user(1L, "a@b.com");
        when(users.findById(1L)).thenReturn(Optional.of(u));
        when(hasher.matches("cur", "hash")).thenReturn(true);
        when(hasher.hash("new")).thenReturn("NEWHASH");

        service.changePassword(1L, new ChangePasswordRequest("cur", "new"));
        assertThat(u.getPasswordHash()).isEqualTo("NEWHASH");
    }

    @Test
    void changePassword_wrongCurrent_badRequest() {
        User u = TestFixtures.user(1L, "a@b.com");
        when(users.findById(1L)).thenReturn(Optional.of(u));
        when(hasher.matches("bad", "hash")).thenReturn(false);

        assertThatThrownBy(() -> service.changePassword(1L, new ChangePasswordRequest("bad", "new")))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ── addresses ─────────────────────────────────────────────
    @Test
    void addAddress_firstBecomesDefault() {
        User u = TestFixtures.user(1L, "a@b.com");
        when(users.findById(1L)).thenReturn(Optional.of(u));

        User out = service.addAddress(1L, addrReq());
        assertThat(out.getAddresses()).hasSize(1);
        assertThat(out.getAddresses().get(0).isDefault()).isTrue();
    }

    @Test
    void addAddress_secondNotDefault() {
        User u = TestFixtures.user(1L, "a@b.com");
        Address first = TestFixtures.address(1L, "700001");
        first.setDefault(true);
        u.addAddress(first);
        when(users.findById(1L)).thenReturn(Optional.of(u));

        User out = service.addAddress(1L, addrReq());
        assertThat(out.getAddresses().get(1).isDefault()).isFalse();
    }

    @Test
    void addAddress_overLimit_badRequest() {
        User u = TestFixtures.user(1L, "a@b.com");
        for (long i = 1; i <= 3; i++) {
            u.addAddress(TestFixtures.address(i, "700001"));
        }
        when(users.findById(1L)).thenReturn(Optional.of(u));

        assertThatThrownBy(() -> service.addAddress(1L, addrReq()))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void removeAddress_promotesNewDefaultWhenRemovingDefault() {
        User u = TestFixtures.user(1L, "a@b.com");
        Address a1 = TestFixtures.address(1L, "700001"); a1.setDefault(true);
        Address a2 = TestFixtures.address(2L, "700002");
        u.addAddress(a1); u.addAddress(a2);
        when(users.findById(1L)).thenReturn(Optional.of(u));

        User out = service.removeAddress(1L, 1L);
        assertThat(out.getAddresses()).hasSize(1);
        assertThat(out.getAddresses().get(0).isDefault()).isTrue();
    }

    @Test
    void removeAddress_nonDefault_noPromotion() {
        User u = TestFixtures.user(1L, "a@b.com");
        Address a1 = TestFixtures.address(1L, "700001"); a1.setDefault(true);
        Address a2 = TestFixtures.address(2L, "700002");
        u.addAddress(a1); u.addAddress(a2);
        when(users.findById(1L)).thenReturn(Optional.of(u));

        User out = service.removeAddress(1L, 2L);
        assertThat(out.getAddresses()).hasSize(1);
        assertThat(out.getAddresses().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void setDefaultAddress_movesDefaultFlag() {
        User u = TestFixtures.user(1L, "a@b.com");
        Address a1 = TestFixtures.address(1L, "700001"); a1.setDefault(true);
        Address a2 = TestFixtures.address(2L, "700002");
        u.addAddress(a1); u.addAddress(a2);
        when(users.findById(1L)).thenReturn(Optional.of(u));

        User out = service.setDefaultAddress(1L, 2L);
        assertThat(out.getAddresses().get(0).isDefault()).isFalse();
        assertThat(out.getAddresses().get(1).isDefault()).isTrue();
    }

    @Test
    void setDefaultAddress_unknown_notFound() {
        User u = TestFixtures.user(1L, "a@b.com");
        u.addAddress(TestFixtures.address(1L, "700001"));
        when(users.findById(1L)).thenReturn(Optional.of(u));

        assertThatThrownBy(() -> service.setDefaultAddress(1L, 99L))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ── wishlist ──────────────────────────────────────────────
    @Test
    void toggleWishlist_addsThenRemoves() {
        User u = TestFixtures.user(1L, "a@b.com");
        when(users.findById(1L)).thenReturn(Optional.of(u));

        assertThat(service.toggleWishlist(1L, "P001")).containsExactly("P001");
        assertThat(service.toggleWishlist(1L, "P001")).isEmpty();
    }
}
