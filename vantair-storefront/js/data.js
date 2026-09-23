// ============================================================
// VANTAIR — SHARED DATA FILE
// Single source of truth for all pages
// ============================================================

const VANTAIR = {

  // ── BRAND ────────────────────────────────────────────────
  brand: {
    name: 'VANTAIR',
    tagline: 'Premium Products. One Powerful Brand.',
    domain: 'www.vantair.in',
    email: 'vantair@zohomail.in',
    phones: ['+91 90888 41800', '+91 82402 07252'],
    whatsapp: '919088841800',
    address: 'Kolkata, West Bengal, India — 700001',
    social: {
      linkedin:  'https://linkedin.com/company/vantair',
      facebook:  'https://facebook.com/vantair',
      instagram: 'https://instagram.com/vantair',
      threads:   'https://threads.net/@vantair',
      x:         'https://x.com/vantair'
    },
    announcement: '🎉 Free Shipping on orders above ₹999 | Use code VANTAIR10 for 10% off your first order!'
  },

  // ── DELIVERY ─────────────────────────────────────────────
  delivery: {
    freeAbove: 999,
    standardCharge: 49,
    codCharge: 40,
    expressCharge: 149,
    expressRegions: ['700001','700002','700003','700004','700005','700006','700007','700008','700009','700010',
                     '700011','700012','700013','700014','700015','700016','700017','700018','700019','700020',
                     '711101','711102','711103','711104','711105','741101','741102','741103','743101','743102'],
    partners: ['BlueDart', 'Shiprocket'],
    standardDays: { min: 3, max: 5 },
    expressOptions: ['Same Day', 'Next Day'],
    // Region-based delivery days from Kolkata
    regionDays: {
      'WB': 1, 'OR': 2, 'JH': 2, 'BR': 2, 'AS': 3,
      'UP': 3, 'DL': 3, 'MH': 4, 'KA': 4, 'TN': 5,
      'AP': 4, 'TS': 4, 'KL': 5, 'GJ': 4, 'RJ': 4,
      'MP': 3, 'CG': 3, 'HP': 4, 'UK': 3, 'PB': 4,
      'HR': 3, 'GA': 5, 'MN': 5, 'ML': 4, 'MZ': 5,
      'NL': 5, 'TR': 3, 'SK': 3, 'AR': 5, 'DEFAULT': 5
    }
  },

  // ── COUPONS ───────────────────────────────────────────────
  coupons: [
    { code: 'VANTAIR10', type: 'percent', value: 10, minOrder: 500,  desc: '10% off on orders above ₹500' },
    { code: 'WELCOME20', type: 'percent', value: 20, minOrder: 999,  desc: '20% off for new customers' },
    { code: 'FLAT100',   type: 'flat',    value: 100, minOrder: 799, desc: '₹100 off on orders above ₹799' },
    { code: 'FLAT200',   type: 'flat',    value: 200, minOrder: 1499,desc: '₹200 off on orders above ₹1499' },
    { code: 'BEAUTY15',  type: 'percent', value: 15, minOrder: 699,  desc: '15% off on beauty products' },
    { code: 'FREESHIP',  type: 'shipping',value: 0,  minOrder: 0,    desc: 'Free shipping on any order' }
  ],

  // ── CURRENCIES ────────────────────────────────────────────
  currencies: {
    INR: { symbol: '₹',  rate: 1,       name: 'Indian Rupee',       flag: '🇮🇳' },
    USD: { symbol: '$',  rate: 0.012,   name: 'US Dollar',          flag: '🇺🇸' },
    EUR: { symbol: '€',  rate: 0.011,   name: 'Euro',               flag: '🇪🇺' },
    GBP: { symbol: '£',  rate: 0.0095,  name: 'British Pound',      flag: '🇬🇧' },
    AED: { symbol: 'د.إ',rate: 0.044,   name: 'UAE Dirham',         flag: '🇦🇪' },
    JPY: { symbol: '¥',  rate: 1.82,    name: 'Japanese Yen',       flag: '🇯🇵' },
    SGD: { symbol: 'S$', rate: 0.016,   name: 'Singapore Dollar',   flag: '🇸🇬' },
    CAD: { symbol: 'C$', rate: 0.016,   name: 'Canadian Dollar',    flag: '🇨🇦' },
    AUD: { symbol: 'A$', rate: 0.018,   name: 'Australian Dollar',  flag: '🇦🇺' },
    CNY: { symbol: '¥',  rate: 0.087,   name: 'Chinese Yuan',       flag: '🇨🇳' }
  },

  // ── LANGUAGES ─────────────────────────────────────────────
  languages: {
    en:  { name: 'English',    native: 'English',    flag: '🇬🇧' },
    hi:  { name: 'Hindi',      native: 'हिन्दी',       flag: '🇮🇳' },
    bn:  { name: 'Bengali',    native: 'বাংলা',        flag: '🇮🇳' },
    ta:  { name: 'Tamil',      native: 'தமிழ்',        flag: '🇮🇳' },
    te:  { name: 'Telugu',     native: 'తెలుగు',       flag: '🇮🇳' },
    mr:  { name: 'Marathi',    native: 'मराठी',        flag: '🇮🇳' },
    gu:  { name: 'Gujarati',   native: 'ગુજરાતી',      flag: '🇮🇳' },
    kn:  { name: 'Kannada',    native: 'ಕನ್ನಡ',        flag: '🇮🇳' },
    ml:  { name: 'Malayalam',  native: 'മലയാളം',       flag: '🇮🇳' },
    pa:  { name: 'Punjabi',    native: 'ਪੰਜਾਬੀ',       flag: '🇮🇳' },
    or:  { name: 'Odia',       native: 'ଓଡ଼ିଆ',        flag: '🇮🇳' },
    ur:  { name: 'Urdu',       native: 'اردو',         flag: '🇮🇳' },
    as:  { name: 'Assamese',   native: 'অসমীয়া',      flag: '🇮🇳' },
    es:  { name: 'Spanish',    native: 'Español',     flag: '🇪🇸' },
    fr:  { name: 'French',     native: 'Français',    flag: '🇫🇷' },
    ar:  { name: 'Arabic',     native: 'العربية',      flag: '🇸🇦' },
    pt:  { name: 'Portuguese', native: 'Português',   flag: '🇵🇹' },
    ru:  { name: 'Russian',    native: 'Русский',     flag: '🇷🇺' },
    de:  { name: 'German',     native: 'Deutsch',     flag: '🇩🇪' },
    ja:  { name: 'Japanese',   native: '日本語',        flag: '🇯🇵' },
    ko:  { name: 'Korean',     native: '한국어',        flag: '🇰🇷' },
    zh:  { name: 'Chinese',    native: '中文',          flag: '🇨🇳' },
    sa:  { name: 'Sanskrit',   native: 'संस्कृतम्',     flag: '🇮🇳' }
  },

  // ── PRODUCTS ──────────────────────────────────────────────
  products: [

    // 🌸 PERFUMES
    {
      id: 'P001', category: 'perfumes', categoryLabel: 'Perfumes',
      name: 'Vantair Noir EDP',
      tagline: 'Dark. Mysterious. Unforgettable.',
      description: 'A bold oriental fragrance with deep notes of oud, amber and dark musk. Perfect for evening wear and special occasions.',
      benefits: ['Long lasting 8–10 hours', 'Eau de Parfum concentration', 'Unisex fragrance'],
      ingredients: 'Alcohol Denat., Aqua, Fragrance, Oud Extract, Amber Oil, Musk',
      price: 1299, mrp: 1599, rating: 4.8, reviews: 124,
      badge: 'Bestseller',
      variants: [
        { label: '30ml',  price: 799  },
        { label: '50ml',  price: 1299 },
        { label: '100ml', price: 2199 }
      ],
      color: '#1a1a2e', emoji: '🖤',
      expressEligible: true
    },
    {
      id: 'P002', category: 'perfumes', categoryLabel: 'Perfumes',
      name: 'Vantair Rose Bliss EDP',
      tagline: 'Bloom in every step.',
      description: 'A feminine floral bouquet of Bulgarian rose, peony and soft sandalwood. Romantic and timeless.',
      benefits: ['Fresh floral notes', 'Lasts 6–8 hours', 'Perfect for daywear'],
      ingredients: 'Alcohol Denat., Aqua, Fragrance, Rose Otto, Peony Extract, Sandalwood Oil',
      price: 1199, mrp: 1499, rating: 4.7, reviews: 98,
      badge: 'New',
      variants: [
        { label: '30ml',  price: 749  },
        { label: '50ml',  price: 1199 },
        { label: '100ml', price: 1999 }
      ],
      color: '#ffb3c6', emoji: '🌹',
      expressEligible: true
    },
    {
      id: 'P003', category: 'perfumes', categoryLabel: 'Perfumes',
      name: 'Vantair Ocean Breeze EDT',
      tagline: 'Fresh as the sea, free as the wind.',
      description: 'A crisp aquatic fragrance with notes of sea salt, bergamot and white cedar. Invigorating and clean.',
      benefits: ['Light aquatic scent', 'Perfect for summer', 'Office & daily wear'],
      ingredients: 'Alcohol Denat., Aqua, Fragrance, Bergamot Oil, Sea Salt Accord, Cedar Extract',
      price: 999, mrp: 1299, rating: 4.5, reviews: 76,
      badge: null,
      variants: [
        { label: '50ml',  price: 999  },
        { label: '100ml', price: 1699 }
      ],
      color: '#0077b6', emoji: '🌊',
      expressEligible: false
    },
    {
      id: 'P004', category: 'perfumes', categoryLabel: 'Perfumes',
      name: 'Vantair Oud Royale EDP',
      tagline: 'The scent of royalty.',
      description: 'A luxurious blend of premium agarwood oud, saffron and warm vanilla. Rich, opulent and commanding.',
      benefits: ['Premium oud concentration', 'Lasts 10–12 hours', 'Luxury evening scent'],
      ingredients: 'Alcohol Denat., Aqua, Fragrance, Agarwood Oil, Saffron Extract, Vanilla Absolute',
      price: 1599, mrp: 1999, rating: 4.9, reviews: 156,
      badge: 'Premium',
      variants: [
        { label: '30ml',  price: 999  },
        { label: '50ml',  price: 1599 },
        { label: '100ml', price: 2799 }
      ],
      color: '#7b2d00', emoji: '👑',
      expressEligible: true
    },
    {
      id: 'P005', category: 'perfumes', categoryLabel: 'Perfumes',
      name: 'Vantair Fresh Citrus EDT',
      tagline: 'Zesty. Bright. Energising.',
      description: 'A vibrant burst of lemon, grapefruit and green tea. The perfect morning fragrance for active lifestyles.',
      benefits: ['Energising citrus burst', 'Light & refreshing', 'Perfect for mornings'],
      ingredients: 'Alcohol Denat., Aqua, Fragrance, Lemon Oil, Grapefruit Extract, Green Tea Accord',
      price: 899, mrp: 1099, rating: 4.4, reviews: 63,
      badge: null,
      variants: [
        { label: '50ml',  price: 899  },
        { label: '100ml', price: 1499 }
      ],
      color: '#f4d03f', emoji: '🍋',
      expressEligible: false
    },
    {
      id: 'P006', category: 'perfumes', categoryLabel: 'Perfumes',
      name: 'Vantair Velvet Musk EDP',
      tagline: 'Soft. Sensual. Sophisticated.',
      description: 'A delicate blend of white musk, cashmere wood and powdery iris. Intimate and deeply personal.',
      benefits: ['Skin-close sillage', 'Clean musk signature', 'All-day confidence'],
      ingredients: 'Alcohol Denat., Aqua, Fragrance, White Musk, Cashmere Wood, Iris Extract',
      price: 1399, mrp: 1699, rating: 4.6, reviews: 89,
      badge: 'Popular',
      variants: [
        { label: '30ml',  price: 899  },
        { label: '50ml',  price: 1399 },
        { label: '100ml', price: 2399 }
      ],
      color: '#d4a0c0', emoji: '🤍',
      expressEligible: true
    },
    {
      id: 'P007', category: 'perfumes', categoryLabel: 'Perfumes',
      name: 'Vantair Pink Petal Body Mist',
      tagline: 'Light, lovely and playful.',
      description: 'A light body mist with notes of peach, jasmine and soft vanilla. Ideal for everyday freshness.',
      benefits: ['Lightweight formula', 'Full body coverage', 'Refreshing all day'],
      ingredients: 'Aqua, Alcohol Denat., Fragrance, Peach Extract, Jasmine Oil, Vanilla',
      price: 599, mrp: 799, rating: 4.3, reviews: 45,
      badge: null,
      variants: [
        { label: '100ml', price: 599  },
        { label: '200ml', price: 999  }
      ],
      color: '#ffcce7', emoji: '🌸',
      expressEligible: false
    },
    {
      id: 'P008', category: 'perfumes', categoryLabel: 'Perfumes',
      name: 'Vantair Midnight Roll-On',
      tagline: 'Pocket-sized luxury.',
      description: 'A concentrated roll-on perfume oil with oud and black pepper. Powerful, portable and long-lasting.',
      benefits: ['Alcohol-free formula', 'Travel friendly', 'Intense concentration'],
      ingredients: 'Fragrance Oil, Jojoba Oil, Oud Extract, Black Pepper CO2, Vitamin E',
      price: 499, mrp: 649, rating: 4.5, reviews: 112,
      badge: 'Travel Size',
      variants: [
        { label: '8ml',  price: 499 },
        { label: '15ml', price: 799 }
      ],
      color: '#2c2c54', emoji: '🌙',
      expressEligible: true
    },

    // 🚿 TOILETRIES
    {
      id: 'T001', category: 'toiletries', categoryLabel: 'Toiletries',
      name: 'Vantair Hydra Shampoo',
      tagline: 'Deep hydration from root to tip.',
      description: 'A nourishing shampoo enriched with hyaluronic acid and argan oil for deeply hydrated, frizz-free hair.',
      benefits: ['Sulfate-free formula', 'Suitable for all hair types', 'Adds shine & softness'],
      ingredients: 'Aqua, Sodium Lauryl Sulfoacetate, Hyaluronic Acid, Argan Oil, Panthenol, Keratin',
      price: 399, mrp: 499, rating: 4.6, reviews: 203,
      badge: 'Bestseller',
      variants: [
        { label: '200ml', price: 399 },
        { label: '400ml', price: 699 }
      ],
      color: '#48cae4', emoji: '💧',
      expressEligible: true
    },
    {
      id: 'T002', category: 'toiletries', categoryLabel: 'Toiletries',
      name: 'Vantair Silk Conditioner',
      tagline: 'Silky smooth, every wash.',
      description: 'A rich conditioning treatment with silk proteins and coconut milk that leaves hair incredibly soft and manageable.',
      benefits: ['Deep conditioning', 'Reduces breakage', 'Tangle-free formula'],
      ingredients: 'Aqua, Cetearyl Alcohol, Silk Amino Acids, Coconut Milk, Shea Butter, Vitamin E',
      price: 349, mrp: 449, rating: 4.5, reviews: 167,
      badge: null,
      variants: [
        { label: '200ml', price: 349 },
        { label: '400ml', price: 599 }
      ],
      color: '#f8edeb', emoji: '🥥',
      expressEligible: true
    },
    {
      id: 'T003', category: 'toiletries', categoryLabel: 'Toiletries',
      name: 'Vantair Charcoal Body Wash',
      tagline: 'Deep cleanse. Pure skin.',
      description: 'Activated charcoal body wash that draws out impurities while leaving skin feeling clean, refreshed and moisturised.',
      benefits: ['Deep pore cleansing', 'Removes toxins', 'Skin detox formula'],
      ingredients: 'Aqua, Activated Charcoal, Sodium Cocoyl Isethionate, Tea Tree Oil, Aloe Vera',
      price: 449, mrp: 599, rating: 4.7, reviews: 189,
      badge: 'Popular',
      variants: [
        { label: '200ml', price: 449 },
        { label: '400ml', price: 799 }
      ],
      color: '#2d2d2d', emoji: '🖤',
      expressEligible: false
    },
    {
      id: 'T004', category: 'toiletries', categoryLabel: 'Toiletries',
      name: 'Vantair Moisturising Soap Bar',
      tagline: 'Clean skin, happy skin.',
      description: 'A gentle moisturising soap bar enriched with shea butter and glycerin. Leaves skin soft and hydrated.',
      benefits: ['Moisturises while cleansing', 'Gentle on sensitive skin', 'Long-lasting bar'],
      ingredients: 'Sodium Palmate, Shea Butter, Glycerin, Coconut Oil, Vitamin E, Aloe Extract',
      price: 199, mrp: 249, rating: 4.3, reviews: 312,
      badge: null,
      variants: [
        { label: '75g',  price: 199 },
        { label: '150g', price: 349 }
      ],
      color: '#ffd6a5', emoji: '🧼',
      expressEligible: false
    },
    {
      id: 'T005', category: 'toiletries', categoryLabel: 'Toiletries',
      name: 'Vantair Brightening Face Wash',
      tagline: 'Reveal your glow every morning.',
      description: 'A gentle brightening face wash with vitamin C and niacinamide that cleanses, brightens and evens skin tone.',
      benefits: ['Brightens dull skin', 'Gentle daily cleanser', 'Suitable for all skin types'],
      ingredients: 'Aqua, Sodium Cocoyl Glutamate, Vitamin C, Niacinamide, Hyaluronic Acid, Green Tea',
      price: 299, mrp: 399, rating: 4.6, reviews: 245,
      badge: 'New',
      variants: [
        { label: '100ml', price: 299 },
        { label: '200ml', price: 499 }
      ],
      color: '#fff3b0', emoji: '✨',
      expressEligible: true
    },
    {
      id: 'T006', category: 'toiletries', categoryLabel: 'Toiletries',
      name: 'Vantair Anti-Dandruff Shampoo',
      tagline: 'Flake-free, confident hair.',
      description: 'A targeted anti-dandruff formula with zinc pyrithione and tea tree oil to combat flakes and soothe the scalp.',
      benefits: ['Eliminates dandruff', 'Soothes scalp', 'Strengthens hair'],
      ingredients: 'Aqua, Sodium Laureth Sulfate, Zinc Pyrithione, Tea Tree Oil, Salicylic Acid, Biotin',
      price: 429, mrp: 549, rating: 4.5, reviews: 178,
      badge: null,
      variants: [
        { label: '200ml', price: 429 },
        { label: '400ml', price: 749 }
      ],
      color: '#74b9ff', emoji: '💆',
      expressEligible: false
    },
    {
      id: 'T007', category: 'toiletries', categoryLabel: 'Toiletries',
      name: 'Vantair Deep Cleanse Scrub',
      tagline: 'Buff away the dull.',
      description: 'A physical body scrub with walnut shell powder and coffee grounds that exfoliates dead skin and boosts circulation.',
      benefits: ['Deep exfoliation', 'Improves skin texture', 'Boosts circulation'],
      ingredients: 'Aqua, Walnut Shell Powder, Coffee Grounds, Coconut Oil, Shea Butter, Vitamin C',
      price: 379, mrp: 499, rating: 4.4, reviews: 134,
      badge: null,
      variants: [
        { label: '150g', price: 379 },
        { label: '300g', price: 649 }
      ],
      color: '#6d4c41', emoji: '☕',
      expressEligible: false
    },
    {
      id: 'T008', category: 'toiletries', categoryLabel: 'Toiletries',
      name: 'Vantair Aloe Shower Gel',
      tagline: 'Soothe. Refresh. Glow.',
      description: 'A calming aloe vera shower gel that cleanses gently while soothing irritated or sensitive skin.',
      benefits: ['Soothes sensitive skin', 'Cooling formula', 'Dermatologically tested'],
      ingredients: 'Aqua, Aloe Barbadensis Leaf Juice, Sodium Cocoyl Isethionate, Cucumber Extract, Vitamin B5',
      price: 329, mrp: 429, rating: 4.4, reviews: 156,
      badge: null,
      variants: [
        { label: '200ml', price: 329 },
        { label: '400ml', price: 549 }
      ],
      color: '#b7e4c7', emoji: '🌿',
      expressEligible: false
    },

    // ✨ SKIN CARE
    {
      id: 'S001', category: 'skincare', categoryLabel: 'Skin Care',
      name: 'Vantair Glow Moisturiser',
      tagline: 'Wake up to glowing skin.',
      description: 'A lightweight daily moisturiser with hyaluronic acid and vitamin C that hydrates and brightens for a natural glow.',
      benefits: ['24hr hydration', 'Brightens skin tone', 'Non-greasy formula'],
      ingredients: 'Aqua, Hyaluronic Acid, Vitamin C, Niacinamide, Glycerin, Jojoba Oil, SPF15',
      price: 599, mrp: 799, rating: 4.8, reviews: 287,
      badge: 'Bestseller',
      variants: [
        { label: '50ml',  price: 599 },
        { label: '100ml', price: 999 }
      ],
      color: '#ffe8d6', emoji: '🌟',
      expressEligible: true
    },
    {
      id: 'S002', category: 'skincare', categoryLabel: 'Skin Care',
      name: 'Vantair Vitamin C Serum',
      tagline: 'Brighten. Firm. Protect.',
      description: 'A potent 15% vitamin C serum with ferulic acid and vitamin E that fades dark spots, firms skin and provides antioxidant protection.',
      benefits: ['Fades dark spots', 'Firms & tightens', 'Antioxidant protection'],
      ingredients: 'Aqua, L-Ascorbic Acid 15%, Ferulic Acid, Vitamin E, Hyaluronic Acid, Niacinamide',
      price: 799, mrp: 1099, rating: 4.9, reviews: 342,
      badge: 'Premium',
      variants: [
        { label: '30ml', price: 799  },
        { label: '50ml', price: 1199 }
      ],
      color: '#f6c90e', emoji: '🍊',
      expressEligible: true
    },
    {
      id: 'S003', category: 'skincare', categoryLabel: 'Skin Care',
      name: 'Vantair SPF50 Sunscreen',
      tagline: 'Shield your glow.',
      description: 'A lightweight, non-sticky SPF50 PA+++ sunscreen that protects against UVA and UVB rays while keeping skin matte.',
      benefits: ['Broad spectrum SPF50', 'Non-sticky, matte finish', 'Suitable under makeup'],
      ingredients: 'Aqua, Zinc Oxide, Titanium Dioxide, Niacinamide, Hyaluronic Acid, Aloe Vera',
      price: 499, mrp: 699, rating: 4.7, reviews: 198,
      badge: 'Popular',
      variants: [
        { label: '50g',  price: 499 },
        { label: '100g', price: 849 }
      ],
      color: '#fff9c4', emoji: '☀️',
      expressEligible: true
    },
    {
      id: 'S004', category: 'skincare', categoryLabel: 'Skin Care',
      name: 'Vantair Hydra Face Mask',
      tagline: 'Spa treatment at home.',
      description: 'A deeply hydrating sheet mask infused with hyaluronic acid, collagen and aloe vera for plump, dewy skin.',
      benefits: ['Intense hydration boost', 'Plumps & smooths', 'Visible results in 20min'],
      ingredients: 'Aqua, Hyaluronic Acid, Hydrolyzed Collagen, Aloe Vera, Niacinamide, Centella Asiatica',
      price: 349, mrp: 449, rating: 4.6, reviews: 167,
      badge: null,
      variants: [
        { label: 'Single', price: 349 },
        { label: 'Pack of 5', price: 1499 }
      ],
      color: '#d0f4de', emoji: '🌊',
      expressEligible: false
    },
    {
      id: 'S005', category: 'skincare', categoryLabel: 'Skin Care',
      name: 'Vantair Rose Toner',
      tagline: 'Refresh, balance, bloom.',
      description: 'A balancing facial toner with Bulgarian rose water and witch hazel that tightens pores and refreshes skin.',
      benefits: ['Tightens pores', 'Balances skin pH', 'Refreshes instantly'],
      ingredients: 'Rosa Damascena Flower Water, Witch Hazel, Niacinamide, Hyaluronic Acid, Glycerin',
      price: 449, mrp: 599, rating: 4.5, reviews: 143,
      badge: null,
      variants: [
        { label: '100ml', price: 449 },
        { label: '200ml', price: 749 }
      ],
      color: '#ffb3c6', emoji: '🌹',
      expressEligible: false
    },
    {
      id: 'S006', category: 'skincare', categoryLabel: 'Skin Care',
      name: 'Vantair Night Repair Cream',
      tagline: 'Repair while you rest.',
      description: 'An intensive overnight repair cream with retinol, peptides and shea butter that renews skin while you sleep.',
      benefits: ['Overnight cell renewal', 'Reduces fine lines', 'Deep nourishment'],
      ingredients: 'Aqua, Retinol 0.3%, Peptide Complex, Shea Butter, Ceramides, Squalane, Vitamin E',
      price: 699, mrp: 999, rating: 4.8, reviews: 234,
      badge: 'Premium',
      variants: [
        { label: '50ml', price: 699  },
        { label: '100ml',price: 1199 }
      ],
      color: '#2c2c54', emoji: '🌙',
      expressEligible: true
    },
    {
      id: 'S007', category: 'skincare', categoryLabel: 'Skin Care',
      name: 'Vantair Under Eye Gel',
      tagline: 'Bye bye dark circles.',
      description: 'A cooling under-eye gel with caffeine, vitamin K and peptides that reduces puffiness and dark circles overnight.',
      benefits: ['Reduces dark circles', 'Depuffs instantly', 'Cooling gel formula'],
      ingredients: 'Aqua, Caffeine 5%, Vitamin K, Peptide Complex, Hyaluronic Acid, Cucumber Extract',
      price: 549, mrp: 749, rating: 4.6, reviews: 189,
      badge: 'New',
      variants: [
        { label: '15ml', price: 549 },
        { label: '30ml', price: 899 }
      ],
      color: '#74b9ff', emoji: '👁️',
      expressEligible: true
    },
    {
      id: 'S008', category: 'skincare', categoryLabel: 'Skin Care',
      name: 'Vantair Retinol Serum',
      tagline: 'The gold standard of anti-ageing.',
      description: 'A clinically formulated 0.5% retinol serum with bakuchiol and peptides for visible wrinkle reduction and skin renewal.',
      benefits: ['Reduces wrinkles', 'Improves skin texture', 'Boosts collagen'],
      ingredients: 'Aqua, Retinol 0.5%, Bakuchiol, Peptide Complex, Hyaluronic Acid, Niacinamide',
      price: 899, mrp: 1299, rating: 4.9, reviews: 276,
      badge: 'Premium',
      variants: [
        { label: '30ml', price: 899  },
        { label: '50ml', price: 1399 }
      ],
      color: '#f8c471', emoji: '⭐',
      expressEligible: true
    },

    // 💄 COSMETICS
    {
      id: 'C001', category: 'cosmetics', categoryLabel: 'Cosmetics',
      name: 'Vantair Velvet Lipstick',
      tagline: 'Colour that commands.',
      description: 'A highly pigmented matte lipstick with a velvet finish. Moisturising formula that lasts all day without drying lips.',
      benefits: ['8hr wear formula', 'Non-drying matte', 'Highly pigmented'],
      ingredients: 'Ricinus Communis Oil, Candelilla Wax, Vitamin E, Shea Butter, Iron Oxides',
      price: 499, mrp: 649, rating: 4.7, reviews: 312,
      badge: 'Bestseller',
      variants: [
        { label: 'Ruby Red',    price: 499 },
        { label: 'Nude Beige',  price: 499 },
        { label: 'Berry Wine',  price: 499 },
        { label: 'Coral Bliss', price: 499 },
        { label: 'Deep Plum',   price: 499 }
      ],
      color: '#c0392b', emoji: '💄',
      expressEligible: true
    },
    {
      id: 'C002', category: 'cosmetics', categoryLabel: 'Cosmetics',
      name: 'Vantair HD Foundation',
      tagline: 'Flawless skin. All day.',
      description: 'A buildable, full-coverage HD foundation with SPF20 that blurs imperfections and stays fresh for 16 hours.',
      benefits: ['16hr wear', 'Full coverage', 'SPF20 protection'],
      ingredients: 'Aqua, Cyclopentasiloxane, Titanium Dioxide, Niacinamide, Hyaluronic Acid, Iron Oxides',
      price: 799, mrp: 1099, rating: 4.6, reviews: 234,
      badge: 'Popular',
      variants: [
        { label: 'N10 Ivory',    price: 799 },
        { label: 'N20 Porcelain',price: 799 },
        { label: 'W30 Beige',    price: 799 },
        { label: 'W40 Sand',     price: 799 },
        { label: 'C50 Caramel',  price: 799 },
        { label: 'C60 Mocha',    price: 799 }
      ],
      color: '#d4a574', emoji: '🪞',
      expressEligible: true
    },
    {
      id: 'C003', category: 'cosmetics', categoryLabel: 'Cosmetics',
      name: 'Vantair Kajal Intense',
      tagline: 'Define. Intensify. Captivate.',
      description: 'A deeply pigmented, waterproof kajal pencil that glides on smoothly and stays smudge-proof all day.',
      benefits: ['Waterproof formula', 'Smudge-proof', 'Intense black pigment'],
      ingredients: 'Cyclopentasiloxane, Carbon Black, Beeswax, Ozokerite, Carnauba Wax, Vitamin E',
      price: 299, mrp: 399, rating: 4.8, reviews: 445,
      badge: 'Bestseller',
      variants: [
        { label: 'Jet Black',   price: 299 },
        { label: 'Brown',       price: 299 },
        { label: 'Navy Blue',   price: 299 }
      ],
      color: '#1a1a1a', emoji: '✏️',
      expressEligible: true
    },
    {
      id: 'C004', category: 'cosmetics', categoryLabel: 'Cosmetics',
      name: 'Vantair Liquid Eyeliner',
      tagline: 'Sharp lines. Bold eyes.',
      description: 'A precision liquid eyeliner with a fine felt tip that delivers sharp, defined lines. Waterproof and long-lasting.',
      benefits: ['Precise felt tip', 'Waterproof formula', 'Dries in seconds'],
      ingredients: 'Aqua, Acrylates Copolymer, Carbon Black, Styrene/Acrylates Copolymer, Glycerin',
      price: 349, mrp: 449, rating: 4.7, reviews: 267,
      badge: null,
      variants: [
        { label: 'Jet Black', price: 349 },
        { label: 'Brown',     price: 349 }
      ],
      color: '#2d3436', emoji: '🖊️',
      expressEligible: false
    },
    {
      id: 'C005', category: 'cosmetics', categoryLabel: 'Cosmetics',
      name: 'Vantair Compact Powder',
      tagline: 'Set. Blur. Perfect.',
      description: 'A silky setting powder that controls shine, blurs pores and sets makeup for a flawless, long-lasting finish.',
      benefits: ['Controls shine', 'Blurs pores', 'Sets makeup all day'],
      ingredients: 'Talc, Mica, Silica, Nylon-12, Zinc Stearate, Titanium Dioxide, Iron Oxides',
      price: 449, mrp: 599, rating: 4.5, reviews: 178,
      badge: null,
      variants: [
        { label: 'Translucent', price: 449 },
        { label: 'Light Beige', price: 449 },
        { label: 'Medium Tan',  price: 449 },
        { label: 'Deep Brown',  price: 449 }
      ],
      color: '#f5cba7', emoji: '🪄',
      expressEligible: false
    },
    {
      id: 'C006', category: 'cosmetics', categoryLabel: 'Cosmetics',
      name: 'Vantair Blush Palette',
      tagline: 'Flush of confidence.',
      description: 'A 4-shade blush palette with buildable, natural-looking shades from soft pink to warm coral.',
      benefits: ['4 complementary shades', 'Buildable coverage', 'Natural satin finish'],
      ingredients: 'Talc, Mica, Iron Oxides, Magnesium Stearate, Silica, Titanium Dioxide, Carmine',
      price: 699, mrp: 899, rating: 4.6, reviews: 145,
      badge: 'New',
      variants: [
        { label: 'Rosewood Edit', price: 699 },
        { label: 'Sunset Edit',   price: 699 }
      ],
      color: '#ffb3ba', emoji: '🌸',
      expressEligible: false
    },
    {
      id: 'C007', category: 'cosmetics', categoryLabel: 'Cosmetics',
      name: 'Vantair Eyeshadow Palette',
      tagline: '12 shades. Infinite looks.',
      description: 'A versatile 12-pan eyeshadow palette with matte, shimmer and glitter finishes from neutral to bold.',
      benefits: ['12 curated shades', 'Matte + shimmer + glitter', 'Long-wearing formula'],
      ingredients: 'Talc, Mica, Iron Oxides, Silica, Carmine, Ultramarines, Chromium Oxide, Bismuth Oxychloride',
      price: 899, mrp: 1299, rating: 4.8, reviews: 234,
      badge: 'Premium',
      variants: [
        { label: 'Nude Neutrals',  price: 899 },
        { label: 'Smoky Drama',    price: 899 },
        { label: 'Colourful Pop',  price: 899 }
      ],
      color: '#a29bfe', emoji: '🎨',
      expressEligible: true
    },
    {
      id: 'C008', category: 'cosmetics', categoryLabel: 'Cosmetics',
      name: 'Vantair Brow Definer',
      tagline: 'Frame your face.',
      description: 'A micro-precision brow pencil with a spoolie brush that creates natural, hair-like brow strokes.',
      benefits: ['Micro-precision tip', 'Natural hair-like strokes', 'Built-in spoolie'],
      ingredients: 'Cyclopentasiloxane, Synthetic Wax, Iron Oxides, Vitamin E, Carnauba Wax',
      price: 399, mrp: 499, rating: 4.5, reviews: 167,
      badge: null,
      variants: [
        { label: 'Soft Black', price: 399 },
        { label: 'Dark Brown', price: 399 },
        { label: 'Medium Brown',price:399 },
        { label: 'Taupe',      price: 399 }
      ],
      color: '#795548', emoji: '🖌️',
      expressEligible: false
    }
  ],

  // ── CATEGORIES ────────────────────────────────────────────
  categories: [
    { id: 'perfumes',   label: 'Perfumes',   emoji: '🌸', desc: 'EDP, EDT, Body Mists & Roll-Ons',       color: '#2c2c54' },
    { id: 'toiletries', label: 'Toiletries', emoji: '🚿', desc: 'Shampoo, Body Wash, Soap & More',        color: '#0077b6' },
    { id: 'skincare',   label: 'Skin Care',  emoji: '✨', desc: 'Serums, Moisturisers & Sunscreen',       color: '#1d6a4a' },
    { id: 'cosmetics',  label: 'Cosmetics',  emoji: '💄', desc: 'Lipstick, Foundation, Kajal & More',     color: '#8e1a4a' }
  ],

  // ── FAQ ───────────────────────────────────────────────────
  faqs: [
    { q: 'What is open box delivery?', a: 'Vantair offers open box delivery — you can inspect your product before the delivery agent leaves. If the product is damaged or defective at the time of delivery, you can return it instantly on the spot.' },
    { q: 'How does the Delivery OTP work?', a: 'After placing an order, a unique 6-digit OTP is generated for your delivery. The delivery agent will ask you for this OTP to confirm your identity. This ensures only you receive your order.' },
    { q: 'What payment methods do you accept?', a: 'We accept UPI (all major UPI apps) and Cash on Delivery (COD). A ₹40 convenience fee applies to COD orders.' },
    { q: 'How long does delivery take?', a: 'Standard delivery takes 3–5 business days depending on your location. Express delivery (Same Day or Next Day) is available for select pin codes near Kolkata for an additional ₹149.' },
    { q: 'What is your refund policy?', a: 'Refunds are processed within 7 business days to your bank account or as a coupon code in your dashboard. Coupon refunds can be used on your next purchase.' },
    { q: 'Can I return a product after accepting delivery?', a: 'We follow an open box delivery policy — returns are only accepted at the time of delivery if the product is defective. Once accepted, the product cannot be returned.' },
    { q: 'Is free shipping available?', a: 'Yes! Orders above ₹999 qualify for free standard shipping. For orders below ₹999, a flat delivery charge of ₹49 applies.' },
    { q: 'How do I track my order?', a: 'Track your order in real-time from your account dashboard. You will see live status updates, estimated delivery time, and delay notifications if any.' },
    { q: 'Do you deliver outside India?', a: 'Currently, Vantair delivers across all major pin codes in India. International shipping is coming soon.' },
    { q: 'How do I become a B2B partner?', a: 'Visit our Partner With Us page and fill in your details. Our business team will get in touch within 2 business days.' }
  ],

  // ── TESTIMONIALS ──────────────────────────────────────────
  testimonials: [
    { name: 'Priya Sharma',    city: 'Mumbai',    rating: 5, text: 'The Vitamin C Serum is absolutely amazing! My skin has never looked brighter. Vantair products are genuinely premium quality.', product: 'Vantair Vitamin C Serum', avatar: 'PS' },
    { name: 'Rahul Das',       city: 'Kolkata',   rating: 5, text: 'Ordered the Noir EDP and it lasts all day. The open box delivery was a great touch — I could check before accepting!', product: 'Vantair Noir EDP', avatar: 'RD' },
    { name: 'Anjali Mehta',    city: 'Delhi',     rating: 5, text: 'The Velvet Lipstick shades are gorgeous and the formula is so comfortable. Finally a brand that delivers on promises.', product: 'Vantair Velvet Lipstick', avatar: 'AM' },
    { name: 'Karthik Nair',    city: 'Bangalore', rating: 4, text: 'Charcoal body wash is my new favourite. Feels so clean after every shower. Will definitely order again!', product: 'Vantair Charcoal Body Wash', avatar: 'KN' },
    { name: 'Sneha Agarwal',   city: 'Pune',      rating: 5, text: 'The delivery OTP system is brilliant — so secure. And the Retinol Serum has made a visible difference in just 2 weeks!', product: 'Vantair Retinol Serum', avatar: 'SA' },
    { name: 'Mohammed Ismail', city: 'Hyderabad', rating: 5, text: 'Ordered 3 products in one go. Packaging is premium, delivery was fast and everything was exactly as described.', product: 'Multiple Products', avatar: 'MI' }
  ]
};

// ── HELPER FUNCTIONS ──────────────────────────────────────────

function getCart() {
  return JSON.parse(localStorage.getItem('vantair_cart') || '[]');
}
function saveCart(cart) {
  localStorage.setItem('vantair_cart', JSON.stringify(cart));
}
function getWishlist() {
  return JSON.parse(localStorage.getItem('vantair_wishlist') || '[]');
}
function saveWishlist(wl) {
  localStorage.setItem('vantair_wishlist', JSON.stringify(wl));
}
function getCurrentUser() {
  // Backed by the API session cache (see js/api.js). Returns the cached
  // signed-in user synchronously so navbar/auth-guards keep working.
  return (typeof getSessionUser === 'function') ? getSessionUser() : null;
}
function getOrders() {
  const user = getCurrentUser();
  if (!user) return [];
  return JSON.parse(localStorage.getItem('vantair_orders_' + user.id) || '[]');
}
function saveOrders(orders) {
  const user = getCurrentUser();
  if (!user) return;
  localStorage.setItem('vantair_orders_' + user.id, JSON.stringify(orders));
}
function getCurrency() {
  return localStorage.getItem('vantair_currency') || 'INR';
}
function getLanguage() {
  return localStorage.getItem('vantair_language') || 'en';
}
function formatPrice(inrPrice) {
  const code = getCurrency();
  const cur = VANTAIR.currencies[code];
  const converted = (inrPrice * cur.rate).toFixed(code === 'JPY' ? 0 : 2);
  return cur.symbol + (code === 'INR' ? Math.round(inrPrice) : converted);
}
function getProductById(id) {
  return VANTAIR.products.find(p => p.id === id);
}
function getProductsByCategory(cat) {
  return VANTAIR.products.filter(p => p.category === cat);
}
function generateOTP() {
  return Math.floor(100000 + Math.random() * 900000).toString();
}
function generateOrderId() {
  return 'VNT' + Date.now().toString().slice(-8) + Math.floor(Math.random()*100);
}
function getCartTotal() {
  const cart = getCart();
  return cart.reduce((sum, item) => {
    const product = getProductById(item.productId);
    if (!product) return sum;
    const variant = product.variants[item.variantIndex || 0];
    return sum + (variant ? variant.price : product.price) * item.qty;
  }, 0);
}
function getCartCount() {
  return getCart().reduce((sum, item) => sum + item.qty, 0);
}
function addToCart(productId, variantIndex, qty) {
  const cart = getCart();
  const existing = cart.find(i => i.productId === productId && i.variantIndex === variantIndex);
  if (existing) {
    existing.qty += qty;
  } else {
    cart.push({ productId, variantIndex: variantIndex || 0, qty: qty || 1 });
  }
  saveCart(cart);
  updateCartBadge();
}
function updateCartBadge() {
  const badge = document.getElementById('cart-badge');
  if (badge) {
    const count = getCartCount();
    badge.textContent = count;
    badge.style.display = count > 0 ? 'flex' : 'none';
  }
}
function toggleWishlist(productId) {
  const wl = getWishlist();
  const idx = wl.indexOf(productId);
  if (idx > -1) { wl.splice(idx, 1); }
  else { wl.push(productId); }
  saveWishlist(wl);
  // If signed in, mirror the change to the server (fire-and-forget).
  const user = getCurrentUser();
  if (user && typeof api !== 'undefined') {
    api.toggleWishlist(user.id, productId).catch(() => {});
  }
  return idx === -1;
}
function isInWishlist(productId) {
  return getWishlist().includes(productId);
}
function detectCurrency() {
  const saved = localStorage.getItem('vantair_currency');
  if (saved) return saved;
  // Auto detect by timezone
  const tz = Intl.DateTimeFormat().resolvedOptions().timeZone;
  if (tz.includes('Kolkata') || tz.includes('Calcutta')) return 'INR';
  if (tz.includes('Dubai') || tz.includes('Abu_Dhabi')) return 'AED';
  if (tz.includes('Tokyo')) return 'JPY';
  if (tz.includes('Singapore')) return 'SGD';
  if (tz.includes('London')) return 'GBP';
  if (tz.includes('Paris') || tz.includes('Berlin')) return 'EUR';
  if (tz.includes('Shanghai') || tz.includes('Beijing')) return 'CNY';
  return 'USD';
}
function isExpressAvailable(pincode) {
  return VANTAIR.delivery.expressRegions.includes(pincode);
}
function getDeliveryDate(pincode, express, expressType) {
  const today = new Date();
  if (express && expressType === 'Same Day') {
    return today.toLocaleDateString('en-IN', { weekday:'long', day:'numeric', month:'long' });
  }
  if (express && expressType === 'Next Day') {
    today.setDate(today.getDate() + 1);
    return today.toLocaleDateString('en-IN', { weekday:'long', day:'numeric', month:'long' });
  }
  // Standard — based on pincode prefix (state)
  const prefix = pincode ? pincode.substring(0,3) : '700';
  const stateMap = {
    '700':'WB','711':'WB','712':'WB','713':'WB','721':'WB','722':'WB','723':'WB','731':'WB','732':'WB','741':'WB','742':'WB','743':'WB','751':'OR','752':'OR','753':'OR','800':'BR','801':'BR','811':'JH','812':'JH','110':'DL','400':'MH','500':'TS','600':'TN','560':'KA','682':'KL','380':'GJ','302':'RJ','226':'UP','462':'MP','492':'CG'
  };
  const state = stateMap[prefix] || 'DEFAULT';
  const days = VANTAIR.delivery.regionDays[state] || 5;
  today.setDate(today.getDate() + days);
  return today.toLocaleDateString('en-IN', { weekday:'long', day:'numeric', month:'long' });
}
function showToast(msg, type='success') {
  let toast = document.getElementById('vt-toast');
  if (!toast) {
    toast = document.createElement('div');
    toast.id = 'vt-toast';
    toast.style.cssText = 'position:fixed;bottom:30px;left:50%;transform:translateX(-50%) translateY(100px);background:#1d1d1f;color:#fff;padding:14px 28px;border-radius:980px;font-size:0.88rem;font-weight:500;box-shadow:0 8px 32px rgba(0,0,0,0.2);transition:transform 0.4s cubic-bezier(0.4,0,0.2,1),opacity 0.4s;opacity:0;z-index:9999;white-space:nowrap;font-family:DM Sans,sans-serif;';
    document.body.appendChild(toast);
  }
  if (type === 'error') toast.style.background = '#ff3b30';
  else if (type === 'success') toast.style.background = '#34c759';
  else toast.style.background = '#1d1d1f';
  toast.textContent = msg;
  toast.style.transform = 'translateX(-50%) translateY(0)';
  toast.style.opacity = '1';
  setTimeout(() => {
    toast.style.transform = 'translateX(-50%) translateY(100px)';
    toast.style.opacity = '0';
  }, 3000);
}

// Init currency detection
if (!localStorage.getItem('vantair_currency')) {
  localStorage.setItem('vantair_currency', detectCurrency());
}

// ── CATALOG SYNC ──────────────────────────────────────────────
// The hardcoded VANTAIR.products/categories/coupons above are kept as an
// instant-render fallback (they mirror the backend seed). On every page we
// also refresh them from the API so the storefront reflects live admin edits.
//
// VANTAIR.ready resolves once the refresh completes (or fails). Listing pages
// can either await it or listen for the 'vantair:catalog' event to re-render.

VANTAIR.catalogReady = false;

function applyCatalog(products, categories, coupons) {
  if (Array.isArray(products) && products.length) {
    VANTAIR.products = products;
  }
  if (Array.isArray(categories) && categories.length) {
    // Frontend templates read `desc`; backend serves `description`.
    VANTAIR.categories = categories.map(c => ({ ...c, desc: c.description ?? c.desc }));
  }
  if (Array.isArray(coupons) && coupons.length) {
    VANTAIR.coupons = coupons.map(c => ({ ...c, desc: c.description ?? c.desc }));
  }
  VANTAIR.catalogReady = true;
}

VANTAIR.ready = (async function loadCatalog() {
  if (typeof api === 'undefined') return; // api.js not present — stay on fallback
  try {
    const [products, categories, coupons] = await Promise.all([
      api.getProducts(),
      api.getCategories(),
      api.getCoupons(),
    ]);
    applyCatalog(products, categories, coupons);
    document.dispatchEvent(new CustomEvent('vantair:catalog'));
  } catch (e) {
    // Backend down — keep the hardcoded fallback so the site still renders.
    console.warn('[vantair] catalog refresh failed, using bundled data:', e.message);
    // Mark the refresh cycle as complete (even though it failed) and notify
    // listeners. Pages that defer decisions until the live catalog arrives
    // (e.g. product.html's 404 check) can then stop waiting on the fallback.
    VANTAIR.catalogReady = true;
    document.dispatchEvent(new CustomEvent('vantair:catalog'));
  }
})();

// ── TRAFFIC BEACON ────────────────────────────────────────────
// Fire-and-forget page-view tracking that powers the admin Traffic
// dashboard. A stable anonymous session id lives in sessionStorage
// (per browser tab session) so the backend can count active visitors
// without any personal data. The admin panel itself is excluded.

function getAnalyticsSessionId() {
  let sid = sessionStorage.getItem('vantair_sid');
  if (!sid) {
    sid = 'sess_' + Math.random().toString(36).slice(2, 10) + Date.now().toString(36).slice(-4);
    sessionStorage.setItem('vantair_sid', sid);
  }
  return sid;
}

function detectDevice() {
  const ua = navigator.userAgent;
  if (/iPad|Tablet|PlayBook|Silk/i.test(ua) || (/Android/i.test(ua) && !/Mobile/i.test(ua))) return 'tablet';
  if (/Mobi|Android|iPhone|iPod|Windows Phone/i.test(ua)) return 'mobile';
  return 'desktop';
}

function trackPageView() {
  if (typeof api === 'undefined' || !api.trackPageView) return;
  // Don't track the admin panel — it's staff, not customer traffic.
  if (/admin\.html$/i.test(location.pathname)) return;
  const user = (typeof getCurrentUser === 'function') ? getCurrentUser() : null;
  api.trackPageView({
    path: location.pathname + location.search,
    title: document.title,
    sessionId: getAnalyticsSessionId(),
    userId: user ? user.id : null,
    device: detectDevice(),
    referrer: document.referrer || '',
  }).catch(() => {}); // never let tracking surface an error to the user
}

if (typeof document !== 'undefined') {
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', trackPageView);
  } else {
    trackPageView();
  }
}

// ── PROMOTIONAL ADS ───────────────────────────────────────────
// Any page with a <div id="ad-slot-{slot}"> gets the active ads for that
// slot rendered into it (admin-managed via the Ads panel). Fire-and-forget;
// if the backend is down or there are no ads, the slot just stays empty.

function renderAdSlot(slotEl, ads) {
  const slot = slotEl.id.replace('ad-slot-', '');
  const mine = ads.filter(a => (a.slot || 'home') === slot);
  if (!mine.length) { slotEl.innerHTML = ''; return; }
  slotEl.innerHTML = mine.map(a => {
    const isImg = a.image && /^https?:\/\//i.test(a.image);
    const media = isImg
      ? `<img src="${a.image}" alt="" style="width:54px;height:54px;border-radius:12px;object-fit:cover;flex-shrink:0;">`
      : `<div style="font-size:2rem;flex-shrink:0;">${a.image || '📣'}</div>`;
    const inner = `
      <div style="display:flex;align-items:center;gap:16px;background:linear-gradient(120deg,#eef2ff,#fce7f3);border:1px solid rgba(99,102,241,0.18);border-radius:16px;padding:16px 20px;margin-bottom:12px;">
        ${media}
        <div style="flex:1;min-width:0;">
          <div style="font-weight:700;color:#1e1b4b;font-size:1rem;">${a.title || ''}</div>
          <div style="color:#475569;font-size:0.86rem;margin-top:2px;">${a.text || ''}</div>
        </div>
        ${a.link ? '<span style="color:#4f46e5;font-weight:600;font-size:0.85rem;white-space:nowrap;">Shop now →</span>' : ''}
      </div>`;
    return a.link
      ? `<a href="${a.link}" style="text-decoration:none;display:block;">${inner}</a>`
      : inner;
  }).join('');
}

function loadAds() {
  if (typeof api === 'undefined' || !api.getAds) return;
  const slots = document.querySelectorAll('[id^="ad-slot-"]');
  if (!slots.length) return;
  api.getAds().then(ads => {
    slots.forEach(el => renderAdSlot(el, ads || []));
  }).catch(() => {}); // ads are non-critical; never surface an error
}

if (typeof document !== 'undefined') {
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', loadAds);
  } else {
    loadAds();
  }
}
