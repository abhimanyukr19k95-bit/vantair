package com.vantair.api;

import com.vantair.api.model.Address;
import com.vantair.api.model.Coupon;
import com.vantair.api.model.Order;
import com.vantair.api.model.OrderItem;
import com.vantair.api.model.OrderStatus;
import com.vantair.api.model.Product;
import com.vantair.api.model.ProductVariant;
import com.vantair.api.model.RefundCoupon;
import com.vantair.api.model.ShippingAddress;
import com.vantair.api.model.User;
import java.util.List;

/** Small builders so tests stay readable. */
public final class TestFixtures {

    private TestFixtures() {
    }

    public static Product product(String id, String category, int price, ProductVariant... variants) {
        Product p = new Product();
        p.setId(id);
        p.setName("Test " + id);
        p.setCategory(category);
        p.setCategoryLabel(category);
        p.setPrice(price);
        p.setMrp(price + 200);
        p.setRating(4.5);
        p.setReviews(10);
        p.setEmoji("🧪");
        p.setActive(true);
        for (ProductVariant v : variants) {
            p.addVariant(v);
        }
        return p;
    }

    public static ProductVariant variant(String label, Integer price) {
        ProductVariant v = new ProductVariant();
        v.setLabel(label);
        v.setPrice(price);
        return v;
    }

    public static User user(Long id, String email) {
        User u = new User();
        u.setId(id);
        u.setName("Tester");
        u.setEmail(email);
        u.setPasswordHash("hash");
        return u;
    }

    public static Address address(Long id, String pincode) {
        Address a = new Address();
        a.setId(id);
        a.setLabel("Home");
        a.setName("Tester");
        a.setPhone("+91 90000 00000");
        a.setLine1("1 Test Road");
        a.setCity("Kolkata");
        a.setState("WB");
        a.setPincode(pincode);
        return a;
    }

    public static Coupon coupon(String code, String type, Integer value, Integer minOrder) {
        Coupon c = new Coupon();
        c.setCode(code);
        c.setType(type);
        c.setValue(value);
        c.setMinOrder(minOrder);
        c.setDescription(code + " desc");
        c.setActive(true);
        return c;
    }

    public static RefundCoupon refundCoupon(Long id, String code, Integer value, User owner) {
        RefundCoupon rc = new RefundCoupon();
        rc.setId(id);
        rc.setCode(code);
        rc.setValue(value);
        rc.setReason("Goodwill");
        rc.setUser(owner);
        return rc;
    }

    public static Order order(String id, Long userId, int total, OrderStatus status) {
        Order o = new Order();
        o.setId(id);
        o.setUserId(userId);
        o.setTotal(total);
        o.setStatus(status);
        ShippingAddress sa = new ShippingAddress();
        sa.setName("Tester");
        sa.setPincode("700001");
        o.setAddress(sa);
        OrderItem item = new OrderItem();
        item.setProductId("P001");
        item.setQty(1);
        item.setPrice(total);
        o.addItem(item);
        return o;
    }

    public static List<OrderItem> none() {
        return List.of();
    }
}
