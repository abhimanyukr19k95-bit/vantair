// ============================================================
// VANTAIR — SHARED COMPONENTS
// Injects navbar, footer, announcement bar, WhatsApp button
// ============================================================

function buildAnnBar() {
  const bar = document.getElementById('ann-bar');
  if (!bar) return;
  bar.innerHTML = `
    <span>${VANTAIR.brand.announcement}</span>
    <button class="ann-close" onclick="this.parentElement.style.display='none'">✕</button>
  `;
}

function buildLCBar() {
  const bar = document.getElementById('lc-bar');
  if (!bar) return;
  const curCode = getCurrency();
  const langCode = getLanguage();

  const curOptions = Object.entries(VANTAIR.currencies).map(([code, cur]) =>
    `<option value="${code}" ${code === curCode ? 'selected' : ''}>${cur.flag} ${code}</option>`
  ).join('');

  const langOptions = Object.entries(VANTAIR.languages).map(([code, lang]) =>
    `<option value="${code}" ${code === langCode ? 'selected' : ''}>${lang.flag} ${lang.native}</option>`
  ).join('');

  bar.innerHTML = `
    <select class="lc-select" id="lang-select" onchange="setLanguage(this.value)">
      ${langOptions}
    </select>
    <select class="lc-select" id="cur-select" onchange="setCurrency(this.value)">
      ${curOptions}
    </select>
  `;
}

function buildNavbar() {
  const nav = document.getElementById('navbar');
  if (!nav) return;
  const user = getCurrentUser();
  const page = window.location.pathname.split('/').pop() || 'index.html';

  function isActive(href) {
    return page === href ? 'active' : '';
  }

  nav.innerHTML = `
    <a href="index.html" class="nav-logo">
      <img src="assets/logo.jpg" alt="Vantair Logo">
      <span class="nav-logo-text">VANTAIR</span>
    </a>
    <nav class="nav-links">
      <a href="index.html"   class="${isActive('index.html')}">Home</a>
      <a href="shop.html"    class="${isActive('shop.html')}">Shop</a>
      <a href="about.html"   class="${isActive('about.html')}">About</a>
      <a href="partner.html" class="${isActive('partner.html')}">Partner</a>
      <a href="contact.html" class="${isActive('contact.html')}">Contact</a>
    </nav>
    <div class="nav-search">
      <span class="search-icon">🔍</span>
      <input type="text" placeholder="Search products..." id="nav-search-input" onkeydown="handleNavSearch(event)">
    </div>
    <div class="nav-actions">
      <a href="wishlist.html" class="nav-icon-btn" title="Wishlist">♡</a>
      <a href="cart.html" class="nav-icon-btn" title="Cart">
        🛒
        <span id="cart-badge">${getCartCount() || ''}</span>
      </a>
      ${user
        ? `<a href="account.html" class="nav-user-btn">
            <div class="nav-user-avatar">${user.name ? user.name[0].toUpperCase() : 'U'}</div>
            <span>${user.name ? user.name.split(' ')[0] : 'Account'}</span>
          </a>`
        : `<a href="signin.html" class="btn btn-primary btn-sm">Sign In</a>`
      }
    </div>
    <button class="nav-hamburger" onclick="toggleMobileNav()" aria-label="Menu">
      <span></span><span></span><span></span>
    </button>
  `;

  // Update cart badge
  const badge = document.getElementById('cart-badge');
  if (badge) {
    const count = getCartCount();
    badge.textContent = count;
    badge.style.display = count > 0 ? 'flex' : 'none';
  }
}

function buildMobileNav() {
  const mn = document.getElementById('mobile-nav');
  if (!mn) return;
  const user = getCurrentUser();
  mn.innerHTML = `
    <button class="mob-close" onclick="toggleMobileNav()">✕</button>
    <a href="index.html"   onclick="toggleMobileNav()">Home</a>
    <a href="shop.html"    onclick="toggleMobileNav()">Shop</a>
    <a href="about.html"   onclick="toggleMobileNav()">About</a>
    <a href="partner.html" onclick="toggleMobileNav()">Partner</a>
    <a href="contact.html" onclick="toggleMobileNav()">Contact</a>
    <a href="wishlist.html" onclick="toggleMobileNav()">♡ Wishlist</a>
    <a href="cart.html"    onclick="toggleMobileNav()">🛒 Cart (${getCartCount()})</a>
    ${user
      ? `<a href="account.html" onclick="toggleMobileNav()">👤 My Account</a>`
      : `<a href="signin.html"  onclick="toggleMobileNav()">Sign In</a>`
    }
  `;
}

function buildFooter() {
  const footer = document.getElementById('footer');
  if (!footer) return;
  footer.innerHTML = `
    <div class="footer-grid">
      <div class="footer-brand">
        <div style="display:flex;align-items:center;gap:10px;">
          <img src="assets/logo.jpg" style="width:36px;height:36px;border-radius:8px;">
          <span class="nav-logo-text" style="color:#fff;">VANTAIR</span>
        </div>
        <p>Premium beauty & lifestyle products. Crafted to our exacting standards, proudly under the Vantair brand. Kolkata, India.</p>
        <div class="footer-social">
          <a href="${VANTAIR.brand.social.linkedin}"  target="_blank" title="LinkedIn">in</a>
          <a href="${VANTAIR.brand.social.facebook}"  target="_blank" title="Facebook">f</a>
          <a href="${VANTAIR.brand.social.instagram}" target="_blank" title="Instagram">📸</a>
          <a href="${VANTAIR.brand.social.threads}"   target="_blank" title="Threads">@</a>
          <a href="${VANTAIR.brand.social.x}"         target="_blank" title="X">𝕏</a>
        </div>
      </div>
      <div class="footer-col">
        <h5>Shop</h5>
        <ul>
          <li><a href="shop.html?cat=perfumes">Perfumes</a></li>
          <li><a href="shop.html?cat=toiletries">Toiletries</a></li>
          <li><a href="shop.html?cat=skincare">Skin Care</a></li>
          <li><a href="shop.html?cat=cosmetics">Cosmetics</a></li>
          <li><a href="shop.html">All Products</a></li>
        </ul>
      </div>
      <div class="footer-col">
        <h5>Company</h5>
        <ul>
          <li><a href="about.html">About Vantair</a></li>
          <li><a href="partner.html">Partner With Us</a></li>
          <li><a href="contact.html">Contact</a></li>
          <li><a href="faq.html">FAQ</a></li>
        </ul>
      </div>
      <div class="footer-col">
        <h5>Support</h5>
        <ul>
          <li><a href="track.html">Track Order</a></li>
          <li><a href="returns.html">Returns & Refunds</a></li>
          <li><a href="faq.html">Help Centre</a></li>
          <li><a href="contact.html">Contact Support</a></li>
        </ul>
      </div>
      <div class="footer-col">
        <h5>Contact</h5>
        <ul>
          <li><a href="mailto:${VANTAIR.brand.email}">${VANTAIR.brand.email}</a></li>
          <li><a href="tel:${VANTAIR.brand.phones[0].replace(/\s/g,'')}">${VANTAIR.brand.phones[0]}</a></li>
          <li><a href="tel:${VANTAIR.brand.phones[1].replace(/\s/g,'')}">${VANTAIR.brand.phones[1]}</a></li>
          <li><span style="color:rgba(255,255,255,0.45);font-size:0.84rem;">Kolkata, West Bengal, India</span></li>
        </ul>
      </div>
    </div>
    <div class="footer-bottom">
      <p class="footer-copy">© ${new Date().getFullYear()} Vantair. All rights reserved. | ${VANTAIR.brand.domain}</p>
      <div class="footer-legal">
        <a href="privacy.html">Privacy Policy</a>
        <a href="privacy.html#terms">Terms of Service</a>
        <a href="returns.html">Returns Policy</a>
      </div>
    </div>
  `;
}

function buildWAFloat() {
  const wa = document.getElementById('wa-float');
  if (!wa) return;
  wa.href = `https://wa.me/${VANTAIR.brand.whatsapp}?text=Hi%20Vantair%2C%20I%20need%20help%20with%20my%20order.`;
  wa.title = 'Chat on WhatsApp';
  wa.innerHTML = '💬';
}

function buildPageLoader() {
  const loader = document.getElementById('page-loader');
  if (!loader) return;
  loader.innerHTML = `
    <div class="loader-logo">
      <img src="assets/logo.jpg" alt="Vantair">
      <span class="loader-logo-text">VANTAIR</span>
    </div>
    <div class="loader-bar">
      <div class="loader-bar-fill"></div>
    </div>
  `;
  setTimeout(() => loader.classList.add('hidden'), 1000);
}

function toggleMobileNav() {
  const mn = document.getElementById('mobile-nav');
  if (mn) mn.classList.toggle('open');
}

function handleNavSearch(e) {
  if (e.key === 'Enter') {
    const q = document.getElementById('nav-search-input').value.trim();
    if (q) window.location.href = `shop.html?search=${encodeURIComponent(q)}`;
  }
}

function setCurrency(code) {
  localStorage.setItem('vantair_currency', code);
  location.reload();
}

function setLanguage(code) {
  localStorage.setItem('vantair_language', code);
  // In production: trigger Google Translate or i18n library
  showToast('Language preference saved!');
}

function initScrollReveal() {
  const reveals = document.querySelectorAll('.reveal');
  const obs = new IntersectionObserver((entries) => {
    entries.forEach(e => { if (e.isIntersecting) e.target.classList.add('visible'); });
  }, { threshold: 0.1 });
  reveals.forEach(el => obs.observe(el));
}

function initComponents() {
  buildPageLoader();
  buildAnnBar();
  buildLCBar();
  buildNavbar();
  buildMobileNav();
  buildFooter();
  buildWAFloat();
  initScrollReveal();
  updateCartBadge();
}

// Auto init when DOM ready
document.addEventListener('DOMContentLoaded', initComponents);
