// ============================================================
// VANTAIR — AUTH MODULE (API-backed)
// Sign up, sign in, session, profile & address management.
// All mutating calls hit the backend via js/api.js and refresh
// the cached session user. Functions are async and return
// { success, user?, error? } to match existing call sites.
// ============================================================

async function signUp(data) {
  // Split the flat signup form into the API's nested shape.
  const payload = {
    name: data.name,
    email: data.email,
    phone: data.phone,
    password: data.password,
    photo: data.photo || null,
    newsletter: data.newsletter || false,
  };
  // Only attach an address if the optional fields were filled in.
  if (data.address && data.city && data.pincode) {
    payload.address = {
      label: 'Home',
      name: data.name,
      phone: data.phone,
      line1: data.address,
      city: data.city,
      state: data.state,
      pincode: data.pincode,
    };
  }
  try {
    const user = await api.signUp(payload);
    setSessionUser(user);
    return { success: true, user };
  } catch (e) {
    return { success: false, error: e.message };
  }
}

async function signIn(email, password) {
  try {
    const user = await api.signIn(email, password);
    setSessionUser(user);
    return { success: true, user };
  } catch (e) {
    return { success: false, error: e.message };
  }
}

function signOut() {
  setSessionUser(null);
  window.location.href = 'index.html';
}

async function updateProfile(data) {
  const user = getCurrentUser();
  if (!user) return { success: false, error: 'Not signed in.' };
  try {
    const updated = await api.updateProfile(user.id, data);
    setSessionUser(updated);
    return { success: true, user: updated };
  } catch (e) {
    return { success: false, error: e.message };
  }
}

async function changePassword(current, newPass) {
  const user = getCurrentUser();
  if (!user) return { success: false, error: 'Not signed in.' };
  try {
    await api.changePassword(user.id, current, newPass);
    return { success: true };
  } catch (e) {
    return { success: false, error: e.message };
  }
}

async function addAddress(addr) {
  const user = getCurrentUser();
  if (!user) return { success: false, error: 'Not signed in.' };
  try {
    const updated = await api.addAddress(user.id, addr);
    setSessionUser(updated);
    return { success: true, user: updated };
  } catch (e) {
    return { success: false, error: e.message };
  }
}

async function removeAddress(addrId) {
  const user = getCurrentUser();
  if (!user) return { success: false, error: 'Not signed in.' };
  try {
    const updated = await api.removeAddress(user.id, addrId);
    setSessionUser(updated);
    return { success: true, user: updated };
  } catch (e) {
    return { success: false, error: e.message };
  }
}

async function setDefaultAddress(addrId) {
  const user = getCurrentUser();
  if (!user) return { success: false, error: 'Not signed in.' };
  try {
    const updated = await api.setDefaultAddress(user.id, addrId);
    setSessionUser(updated);
    return { success: true, user: updated };
  } catch (e) {
    return { success: false, error: e.message };
  }
}

async function savePhotoToProfile(base64) {
  return updateProfile({ photo: base64 });
}

/** Synchronous auth guard — relies on the cached session. */
function requireAuth(redirect = 'signin.html') {
  if (!getCurrentUser()) {
    window.location.href = redirect + '?redirect=' + encodeURIComponent(window.location.href);
    return false;
  }
  return true;
}
