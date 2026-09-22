// ============================================
// BACKEND PACKAGE.JSON
// ============================================

{
  "name": "vantair-backend",
  "version": "1.0.0",
  "description": "VANTAIR B2B Business Partner Platform - Backend API",
  "main": "server.js",
  "type": "module",
  "scripts": {
    "start": "node server.js",
    "dev": "nodemon server.js",
    "test": "jest",
    "lint": "eslint ."
  },
  "dependencies": {
    "express": "^4.18.2",
    "cors": "^2.8.5",
    "dotenv": "^16.3.1",
    "mongoose": "^7.5.0",
    "nodemailer": "^6.9.7",
    "helmet": "^7.1.0",
    "express-rate-limit": "^7.0.0",
    "bcryptjs": "^2.4.3",
    "jsonwebtoken": "^9.1.0",
    "validator": "^13.11.0",
    "axios": "^1.5.0"
  },
  "devDependencies": {
    "nodemon": "^3.0.1",
    "jest": "^29.7.0",
    "eslint": "^8.49.0"
  },
  "engines": {
    "node": ">=16.0.0",
    "npm": ">=8.0.0"
  }
}

// ============================================
// ENVIRONMENT CONFIGURATION (.env)
// ============================================

# Server Configuration
NODE_ENV=production
PORT=5000
FRONTEND_URL=https://vantair.com

# Database Configuration
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/vantair

# Email Configuration
EMAIL_SERVICE=gmail
EMAIL_USER=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
ADMIN_EMAIL=admin@vantair.com
ADMIN_URL=https://admin.vantair.com

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key
JWT_EXPIRE=7d

# Third-party Services
STRIPE_SECRET_KEY=sk_live_xxxxx
GOOGLE_ANALYTICS_ID=G-xxxxx

# AWS Configuration (for file uploads)
AWS_ACCESS_KEY_ID=xxxxx
AWS_SECRET_ACCESS_KEY=xxxxx
AWS_REGION=us-east-1
AWS_S3_BUCKET=vantair-uploads

// ============================================
// FRONTEND PACKAGE.JSON
// ============================================

{
  "name": "vantair-frontend",
  "version": "1.0.0",
  "description": "VANTAIR B2B Business Partner Platform - Frontend",
  "private": true,
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-scripts": "5.0.1",
    "lucide-react": "^0.263.1",
    "axios": "^1.5.0"
  },
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test",
    "eject": "react-scripts eject"
  },
  "eslintConfig": {
    "extends": [
      "react-app"
    ]
  },
  "browserslist": {
    "production": [
      ">0.2%",
      "not dead",
      "not op_mini all"
    ],
    "development": [
      "last 1 chrome version",
      "last 1 firefox version",
      "last 1 safari version"
    ]
  }
}

// ============================================
// FRONTEND API SERVICE (api.js)
// ============================================

import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:5000/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Contact Services
export const contactAPI = {
  submit: (data) => api.post('/contact', data),
  getAll: (token) => api.get('/contact/admin/all', {
    headers: { Authorization: `Bearer ${token}` }
  }),
};

// Partnership Services
export const partnershipAPI = {
  apply: (data) => api.post('/partnership/apply', data),
  getAll: (token) => api.get('/partnership/admin/all', {
    headers: { Authorization: `Bearer ${token}` }
  }),
  getById: (id) => api.get(`/partnership/${id}`),
  updateStatus: (id, status, token) => api.patch(`/partnership/${id}/status`, { status }, {
    headers: { Authorization: `Bearer ${token}` }
  }),
};

// Newsletter Services
export const newsletterAPI = {
  subscribe: (email) => api.post('/newsletter/subscribe', { email }),
  unsubscribe: (email) => api.post('/newsletter/unsubscribe', { email }),
};

// Services
export const servicesAPI = {
  getAll: () => api.get('/services'),
  getById: (id) => api.get(`/services/${id}`),
};

// Partners
export const partnersAPI = {
  getAll: () => api.get('/partners'),
  getById: (id) => api.get(`/partners/${id}`),
};

// Analytics
export const analyticsAPI = {
  getStats: (token) => api.get('/analytics/stats', {
    headers: { Authorization: `Bearer ${token}` }
  }),
};

// Health Check
export const healthAPI = {
  check: () => api.get('/health'),
};

// Error handling interceptor
api.interceptors.response.use(
  response => response,
  error => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export default api;