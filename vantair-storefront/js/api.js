// ============================================================
// VANTAIR — API CLIENT
// Thin fetch wrapper over the Spring Boot backend, plus the
// browser-side session (which user is signed in).
//
// Served same-origin behind server.js, so the base path is just
// "/api". Override with window.VANTAIR_API_BASE if needed.
// ============================================================

const API_BASE = (typeof window !== 'undefined' && window.VANTAIR_API_BASE) || '/api';

/**
 * Core request helper. Throws an Error whose .message is the backend's
 * human-readable message (so existing `catch (e) showToast(e.message)` works).
 */
async function apiRequest(method, pathName, body) {
  const opts = { method, headers: {} };
  if (body !== undefined) {
    opts.headers['Content-Type'] = 'application/json';
    opts.body = JSON.stringify(body);
  }
  let res;
  try {
    res = await fetch(API_BASE + pathName, opts);
  } catch (networkErr) {
    throw new Error('Cannot reach the server. Please try again.');
  }

  // 204 No Content
  if (res.status === 204) return null;

  const text = await res.text();
  let data = null;
  if (text) {
    try { data = JSON.parse(text); } catch { data = text; }
  }

  if (!res.ok) {
    const msg = (data && data.message) ? data.message : ('Request failed (' + res.status + ')');
    const err = new Error(msg);
    err.status = res.status;
    err.body = data;
    throw err;
  }
  return data;
}

const api = {
  // ── Catalog ──────────────────────────────────────────────
  getProducts: (category) => apiRequest('GET', '/products' + (category ? '?category=' + encodeURIComponent(category) : '')),
  getProduct: (id) => apiRequest('GET', '/products/' + encodeURIComponent(id)),
  getCategories: () => apiRequest('GET', '/categories'),

  // ── Coupons ──────────────────────────────────────────────
  getCoupons: () => apiRequest('GET', '/coupons'),
  applyCoupon: (code, subtotal) => apiRequest('POST', '/coupons/apply', { code, subtotal }),

  // ── Auth ─────────────────────────────────────────────────
  signUp: (payload) => apiRequest('POST', '/auth/signup', payload),
  signIn: (email, password) => apiRequest('POST', '/auth/signin', { email, password }),

  // ── User / profile ───────────────────────────────────────
  getUser: (id) => apiRequest('GET', '/users/' + id),
  updateProfile: (id, payload) => apiRequest('PUT', '/users/' + id, payload),
  changePassword: (id, currentPassword, newPassword) =>
    apiRequest('POST', '/users/' + id + '/change-password', { currentPassword, newPassword }),
  addAddress: (id, addr) => apiRequest('POST', '/users/' + id + '/addresses', addr),
  removeAddress: (id, addressId) => apiRequest('DELETE', '/users/' + id + '/addresses/' + addressId),
  setDefaultAddress: (id, addressId) => apiRequest('PUT', '/users/' + id + '/addresses/' + addressId + '/default'),
  toggleWishlist: (id, productId) => apiRequest('POST', '/users/' + id + '/wishlist/' + encodeURIComponent(productId)),

  // ── Orders ───────────────────────────────────────────────
  placeOrder: (payload) => apiRequest('POST', '/orders', payload),
  getOrder: (id) => apiRequest('GET', '/orders/' + encodeURIComponent(id)),
  getUserOrders: (userId) => apiRequest('GET', '/orders?userId=' + userId),
  updateOrderStatus: (id, status) => apiRequest('PUT', '/orders/' + encodeURIComponent(id) + '/status', { status }),
  revealOtp: (id, userId) => apiRequest('POST', '/orders/' + encodeURIComponent(id) + '/reveal-otp?userId=' + userId),

  // ── Delivery agent ───────────────────────────────────────
  validateDeliveryOtp: (orderId, otp) =>
    apiRequest('POST', '/delivery/' + encodeURIComponent(orderId) + '/validate-otp', { otp }),

  // ── Engagement ───────────────────────────────────────────
  subscribeNewsletter: (email) => apiRequest('POST', '/newsletter', { email }),
  sendContact: (payload) => apiRequest('POST', '/contact', payload),
  sendPartner: (payload) => apiRequest('POST', '/partner', payload),

  // ── Admin ────────────────────────────────────────────────
  adminStats: () => apiRequest('GET', '/admin/stats'),
  adminOrders: () => apiRequest('GET', '/admin/orders'),
  adminCustomers: () => apiRequest('GET', '/admin/customers'),
  adminNewsletter: () => apiRequest('GET', '/admin/newsletter'),
  adminContacts: () => apiRequest('GET', '/admin/contacts'),
  adminPartners: () => apiRequest('GET', '/admin/partners'),
  adminCreateProduct: (product) => apiRequest('POST', '/admin/products', product),
  adminUpdateProduct: (id, product) => apiRequest('PUT', '/admin/products/' + encodeURIComponent(id), product),
  adminHideProduct: (id) => apiRequest('DELETE', '/admin/products/' + encodeURIComponent(id)),

  // ── Admin: refunds ───────────────────────────────────────
  adminIssueRefund: (email, amount, reason) =>
    apiRequest('POST', '/admin/refunds', { email, amount, reason }),
  adminRefunds: () => apiRequest('GET', '/admin/refunds'),

  // ── Admin: analytics ─────────────────────────────────────
  adminAnalytics: () => apiRequest('GET', '/admin/analytics'),

  // ── Admin: product content generation ────────────────────
  adminGenerateContent: (name, category) =>
    apiRequest('POST', '/admin/products/generate-content', { name, category }),

  // ── Admin: ads ───────────────────────────────────────────
  adminAds: () => apiRequest('GET', '/admin/ads'),
  adminCreateAd: (ad) => apiRequest('POST', '/admin/ads', ad),
  adminUpdateAd: (id, ad) => apiRequest('PUT', '/admin/ads/' + id, ad),
  adminToggleAd: (id, active) => apiRequest('PUT', '/admin/ads/' + id + '/active?active=' + active),
  adminDeleteAd: (id) => apiRequest('DELETE', '/admin/ads/' + id),

  // ── Ads (public storefront) ──────────────────────────────
  getAds: (slot) => apiRequest('GET', '/ads' + (slot ? '?slot=' + encodeURIComponent(slot) : '')),

  // ── Analytics beacon (public) ────────────────────────────
  trackPageView: (event) => apiRequest('POST', '/analytics/track', event),
};

// ── SESSION ────────────────────────────────────────────────
// We cache the signed-in user object so synchronous code
// (navbar, requireAuth, getCurrentUser) keeps working unchanged.
const SESSION_KEY = 'vantair_session_user';

function setSessionUser(user) {
  if (user) localStorage.setItem(SESSION_KEY, JSON.stringify(user));
  else localStorage.removeItem(SESSION_KEY);
}

function getSessionUser() {
  try { return JSON.parse(localStorage.getItem(SESSION_KEY) || 'null'); }
  catch { return null; }
}

/** Re-fetch the signed-in user from the API and refresh the cached copy. */
async function refreshSessionUser() {
  const cached = getSessionUser();
  if (!cached || cached.id == null) return null;
  try {
    const fresh = await api.getUser(cached.id);
    setSessionUser(fresh);
    return fresh;
  } catch (e) {
    // 404 → account no longer exists; clear the stale session.
    if (e.status === 404) setSessionUser(null);
    return cached;
  }
}
