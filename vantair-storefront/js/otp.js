// ============================================================
// VANTAIR — OTP MODULE (API-backed)
// The delivery OTP now lives on the server. The customer reveals
// it via POST /orders/{id}/reveal-otp (capped at 3 views by the
// backend); the delivery agent validates via
// POST /delivery/{orderId}/validate-otp.
// ============================================================

const OTP_CONFIG = {
  DISPLAY_SECONDS: 10,
  MAX_VIEWS: 3,
};

// Renders the OTP reveal box inside the account "My Orders" panel.
// `order` is the order object returned by the API.
function renderOTPBox(containerId, orderId, order) {
  const container = document.getElementById(containerId);
  if (!container) return;

  if (order.otpUsed) {
    container.innerHTML = `
      <div class="otp-box">
        <div style="font-size:2rem;margin-bottom:12px;">✅</div>
        <p style="font-weight:600;color:var(--success);">OTP Verified — Delivered</p>
        <p style="font-size:0.8rem;color:var(--ink-2);margin-top:6px;">This OTP has been used and the order has been delivered.</p>
      </div>`;
    return;
  }

  const viewsLeft = OTP_CONFIG.MAX_VIEWS - (order.otpViews || 0);

  container.innerHTML = `
    <div class="otp-box" id="otp-wrapper-${orderId}">
      <p style="font-size:0.8rem;font-weight:600;letter-spacing:0.08em;text-transform:uppercase;color:var(--ink-2);margin-bottom:4px;">Delivery OTP</p>
      <p style="font-size:0.76rem;color:var(--ink-3);">Order #${orderId}</p>

      <div id="otp-locked-${orderId}">
        <div style="margin:20px 0;">
          <div class="otp-digits">
            ${Array(6).fill(0).map(() => `<div class="otp-digit otp-hidden" style="color:transparent;background:var(--ink);border-color:var(--ink);">•</div>`).join('')}
          </div>
        </div>
        ${viewsLeft > 0 ? `
          <button class="btn btn-primary btn-sm" onclick="revealOTP('${orderId}')">
            🔓 Reveal OTP (${viewsLeft} views left)
          </button>
        ` : `
          <p style="color:var(--error);font-size:0.84rem;margin-top:12px;">⚠️ Maximum views reached. Please contact support if needed.</p>
        `}
      </div>

      <div id="otp-revealed-${orderId}" style="display:none;">
        <div style="margin:16px 0;">
          <div class="otp-digits" id="otp-digits-${orderId}">
            ${Array(6).fill(0).map((_, i) => `<div class="otp-digit" id="otp-d-${orderId}-${i}">?</div>`).join('')}
          </div>
        </div>
        <p class="otp-timer" id="otp-timer-${orderId}">Hiding in <span id="otp-countdown-${orderId}">10</span>s</p>
        <p style="font-size:0.72rem;color:var(--ink-3);margin-top:8px;">Show this OTP to your delivery agent</p>
      </div>
    </div>
  `;

  // Hide the OTP if the tab is backgrounded.
  document.addEventListener('visibilitychange', () => {
    if (document.hidden) hideOTPDisplay(orderId);
  });
}

async function revealOTP(orderId) {
  const user = getCurrentUser();
  if (!user) { showToast('Please sign in', 'error'); return; }

  try {
    const result = await api.revealOtp(orderId, user.id);
    const otp = result.otp;

    const locked = document.getElementById('otp-locked-' + orderId);
    const revealed = document.getElementById('otp-revealed-' + orderId);
    if (locked) locked.style.display = 'none';
    if (revealed) revealed.style.display = 'block';

    otp.split('').forEach((d, i) => {
      const el = document.getElementById(`otp-d-${orderId}-${i}`);
      if (el) el.textContent = d;
    });

    // Countdown then re-hide.
    let secs = OTP_CONFIG.DISPLAY_SECONDS;
    const interval = setInterval(() => {
      secs--;
      const el = document.getElementById('otp-countdown-' + orderId);
      if (el) el.textContent = secs;
      if (secs <= 0) { clearInterval(interval); hideOTPDisplay(orderId); }
    }, 1000);
  } catch (e) {
    showToast(e.message, 'error');
  }
}

function hideOTPDisplay(orderId) {
  const revealed = document.getElementById('otp-revealed-' + orderId);
  const locked = document.getElementById('otp-locked-' + orderId);
  if (revealed) revealed.style.display = 'none';
  if (locked) locked.style.display = 'block';
}

// Delivery-agent validation — confirms the OTP and marks the order delivered.
async function validateAgentOTP(orderId, inputOTP) {
  try {
    const order = await api.validateDeliveryOtp(orderId, inputOTP);
    return { valid: true, order };
  } catch (e) {
    return { valid: false, error: e.message };
  }
}
