package com.vantair.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.vantair.api.TestFixtures;
import com.vantair.api.model.ContactMessage;
import com.vantair.api.model.Coupon;
import com.vantair.api.model.NewsletterSubscriber;
import com.vantair.api.model.Order;
import com.vantair.api.model.OrderStatus;
import com.vantair.api.model.PageView;
import com.vantair.api.model.PartnerEnquiry;
import com.vantair.api.model.Product;
import com.vantair.api.model.RefundCoupon;
import com.vantair.api.model.User;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/** Exercises the custom finder methods against H2 (configured in test application.yml). */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class RepositoryTest {

    @Autowired ProductRepository products;
    @Autowired CouponRepository coupons;
    @Autowired AdRepository adsRepo;
    @Autowired UserRepository users;
    @Autowired OrderRepository orders;
    @Autowired RefundCouponRepository refunds;
    @Autowired PageViewRepository pageViews;
    @Autowired ContactMessageRepository contacts;
    @Autowired PartnerEnquiryRepository partners;
    @Autowired NewsletterSubscriberRepository newsletter;

    @Test
    void product_activeAndCategoryFinders() {
        Product active = TestFixtures.product("P001", "perfumes", 999);
        Product hidden = TestFixtures.product("P002", "perfumes", 500);
        hidden.setActive(false);
        Product other = TestFixtures.product("S001", "skincare", 700);
        products.saveAll(java.util.List.of(active, hidden, other));

        assertThat(products.findByActiveTrue()).extracting(Product::getId).containsExactlyInAnyOrder("P001", "S001");
        assertThat(products.findByCategoryAndActiveTrue("perfumes")).extracting(Product::getId).containsExactly("P001");
    }

    @Test
    void coupon_activeAndCodeFinders() {
        Coupon active = TestFixtures.coupon("VANTAIR10", "percent", 10, 500);
        Coupon inactive = TestFixtures.coupon("OLD", "flat", 50, 0);
        inactive.setActive(false);
        coupons.saveAll(java.util.List.of(active, inactive));

        assertThat(coupons.findByActiveTrue()).extracting(Coupon::getCode).containsExactly("VANTAIR10");
        assertThat(coupons.findByCodeIgnoreCaseAndActiveTrue("vantair10")).isPresent();
        assertThat(coupons.findByCodeIgnoreCaseAndActiveTrue("OLD")).isEmpty();
    }

    @Test
    void user_emailFinders() {
        User u = TestFixtures.user(null, "a@b.com");
        users.save(u);
        assertThat(users.findByEmailIgnoreCase("A@B.COM")).isPresent();
        assertThat(users.existsByEmailIgnoreCase("a@b.com")).isTrue();
        assertThat(users.existsByEmailIgnoreCase("none@b.com")).isFalse();
    }

    @Test
    void order_userAndStatusFinders() {
        User u = users.save(TestFixtures.user(null, "a@b.com"));
        Order o1 = TestFixtures.order("VNT1", u.getId(), 100, OrderStatus.Confirmed);
        Order o2 = TestFixtures.order("VNT2", u.getId(), 200, OrderStatus.Shipped);
        orders.saveAll(java.util.List.of(o1, o2));

        assertThat(orders.findByUserIdOrderByPlacedAtDesc(u.getId())).hasSize(2);
        assertThat(orders.findAllByOrderByPlacedAtDesc()).hasSize(2);
        assertThat(orders.findByStatusOrderByPlacedAtDesc(OrderStatus.Shipped)).extracting(Order::getId)
                .containsExactly("VNT2");
    }

    @Test
    void refundCoupon_finders() {
        User u = users.save(TestFixtures.user(null, "a@b.com"));
        RefundCoupon rc = TestFixtures.refundCoupon(null, "REFAAAAAA", 500, u);
        refunds.save(rc);

        assertThat(refunds.findByCodeIgnoreCase("refaaaaaa")).isPresent();
        assertThat(refunds.existsByCodeIgnoreCase("REFAAAAAA")).isTrue();
        assertThat(refunds.findByUser_IdOrderByIssuedAtDesc(u.getId())).hasSize(1);
        assertThat(refunds.findAllByOrderByIssuedAtDesc()).hasSize(1);
    }

    @Test
    void pageView_timeFinders() {
        PageView recent = new PageView();
        recent.setPath("/a");
        recent.setViewedAt(Instant.now());
        PageView old = new PageView();
        old.setPath("/b");
        old.setViewedAt(Instant.now().minus(10, ChronoUnit.DAYS));
        pageViews.saveAll(java.util.List.of(recent, old));

        Instant cutoff = Instant.now().minus(1, ChronoUnit.DAYS);
        assertThat(pageViews.findByViewedAtAfter(cutoff)).extracting(PageView::getPath).containsExactly("/a");
        assertThat(pageViews.countByViewedAtAfter(cutoff)).isEqualTo(1);
    }

    @Test
    void ad_slotAndActiveFinders() {
        com.vantair.api.model.Ad homeOn = new com.vantair.api.model.Ad();
        homeOn.setTitle("Home"); homeOn.setSlot("home"); homeOn.setActive(true);
        com.vantair.api.model.Ad shopOff = new com.vantair.api.model.Ad();
        shopOff.setTitle("Shop"); shopOff.setSlot("shop"); shopOff.setActive(false);
        adsRepo.saveAll(java.util.List.of(homeOn, shopOff));

        assertThat(adsRepo.findByActiveTrue()).hasSize(1);
        assertThat(adsRepo.findBySlotAndActiveTrue("home")).hasSize(1);
        assertThat(adsRepo.findBySlotAndActiveTrue("shop")).isEmpty();
        assertThat(adsRepo.findAllByOrderByCreatedAtDesc()).hasSize(2);
    }

    @Test
    void engagement_orderedFinders() {
        ContactMessage cm = new ContactMessage();
        cm.setName("R");
        contacts.save(cm);
        PartnerEnquiry pe = new PartnerEnquiry();
        pe.setName("A");
        partners.save(pe);
        NewsletterSubscriber ns = new NewsletterSubscriber();
        ns.setEmail("a@b.com");
        newsletter.save(ns);

        assertThat(contacts.findAllByOrderByCreatedAtDesc()).hasSize(1);
        assertThat(partners.findAllByOrderByCreatedAtDesc()).hasSize(1);
        assertThat(newsletter.findAll()).hasSize(1);
    }
}
