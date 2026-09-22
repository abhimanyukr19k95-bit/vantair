# VANTAIR WEBSITE - COMPLETE SETUP & INTEGRATION GUIDE

## PROJECT STRUCTURE

```
vantair-website/
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   │   └── api.js
│   │   ├── App.jsx
│   │   └── index.js
│   ├── package.json
│   └── Dockerfile
│
├── backend/
│   ├── models/
│   │   ├── Contact.js
│   │   ├── Partnership.js
│   │   ├── Newsletter.js
│   │   ├── Service.js
│   │   └── Partner.js
│   ├── routes/
│   │   ├── contact.js
│   │   ├── partnership.js
│   │   ├── newsletter.js
│   │   ├── services.js
│   │   └── partners.js
│   ├── middleware/
│   │   ├── auth.js
│   │   ├── validation.js
│   │   └── errorHandler.js
│   ├── config/
│   │   ├── database.js
│   │   ├── email.js
│   │   └── logger.js
│   ├── scripts/
│   │   └── seed.js
│   ├── server.js
│   ├── package.json
│   ├── Dockerfile
│   └── .env.example
│
├── docker-compose.yml
├── docker-compose.prod.yml
├── nginx.conf
├── .gitignore
├── README.md
└── DEPLOYMENT_GUIDE.md
```

---

## STEP-BY-STEP SETUP GUIDE

### STEP 1: Project Initialization

```bash
# Create project directory
mkdir vantair-website
cd vantair-website

# Initialize git
git init
echo "node_modules/" > .gitignore
echo ".env" >> .gitignore
echo ".env.local" >> .gitignore
echo ".DS_Store" >> .gitignore

# Create directories
mkdir frontend backend scripts
```

### STEP 2: Frontend Setup

```bash
cd frontend

# Create React app
npx create-react-app .

# Install additional dependencies
npm install lucide-react axios

# Create .env.local
echo "REACT_APP_API_URL=http://localhost:5000/api" > .env.local

# Create source structure
mkdir src/components src/pages src/services src/hooks

# Add the main app component (vantair-website.jsx)
# Copy the content from our React app to src/App.jsx

cd ..
```

### STEP 3: Backend Setup

```bash
cd backend

# Initialize Node project
npm init -y

# Install dependencies
npm install \
  express \
  cors \
  dotenv \
  mongoose \
  nodemailer \
  helmet \
  express-rate-limit \
  bcryptjs \
  jsonwebtoken \
  validator \
  axios

# Install dev dependencies
npm install --save-dev \
  nodemon \
  jest \
  eslint

# Create directories
mkdir models routes middleware config scripts

# Create .env.example
cat > .env.example << 'EOF'
NODE_ENV=development
PORT=5000
FRONTEND_URL=http://localhost:3000

MONGODB_URI=mongodb://localhost:27017/vantair

EMAIL_SERVICE=gmail
EMAIL_USER=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
ADMIN_EMAIL=admin@vantair.com

JWT_SECRET=your-super-secret-key
JWT_EXPIRE=7d
EOF

# Copy server.js
# Copy backend-server.js content to server.js

cd ..
```

### STEP 4: Create Required Files

**backend/models/Contact.js**
```javascript
const mongoose = require('mongoose');

const ContactSchema = new mongoose.Schema({
  name: { type: String, required: true },
  email: { type: String, required: true },
  company: String,
  message: { type: String, required: true },
  type: { 
    type: String, 
    enum: ['general', 'partnership', 'support'], 
    default: 'general' 
  },
  createdAt: { type: Date, default: Date.now },
  status: { 
    type: String, 
    enum: ['new', 'read', 'responded'], 
    default: 'new' 
  }
});

module.exports = mongoose.model('Contact', ContactSchema);
```

**backend/models/Partnership.js**
```javascript
const mongoose = require('mongoose');

const PartnershipSchema = new mongoose.Schema({
  companyName: { type: String, required: true },
  contactName: { type: String, required: true },
  email: { type: String, required: true },
  phone: { type: String, required: true },
  website: String,
  industry: String,
  partnershipType: {
    type: String,
    enum: ['distribution', 'affiliate', 'sub-partner', 'multi-service'],
    required: true
  },
  serviceInterests: [String],
  annualRevenue: String,
  geographicCoverage: String,
  additionalInfo: String,
  createdAt: { type: Date, default: Date.now },
  status: {
    type: String,
    enum: ['pending', 'under-review', 'approved', 'rejected'],
    default: 'pending'
  }
});

module.exports = mongoose.model('Partnership', PartnershipSchema);
```

**backend/middleware/auth.js**
```javascript
const jwt = require('jsonwebtoken');

const authenticate = (req, res, next) => {
  const token = req.headers.authorization?.split(' ')[1];

  if (!token) {
    return res.status(401).json({ error: 'Unauthorized' });
  }

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.user = decoded;
    next();
  } catch (error) {
    return res.status(401).json({ error: 'Invalid token' });
  }
};

module.exports = { authenticate };
```

**backend/config/database.js**
```javascript
const mongoose = require('mongoose');

const connectDatabase = async () => {
  try {
    await mongoose.connect(
      process.env.MONGODB_URI || 'mongodb://localhost:27017/vantair',
      {
        useNewUrlParser: true,
        useUnifiedTopology: true,
      }
    );
    console.log('✓ MongoDB connected successfully');
  } catch (error) {
    console.error('✗ MongoDB connection failed:', error);
    process.exit(1);
  }
};

module.exports = connectDatabase;
```

**frontend/src/services/api.js**
```javascript
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:5000/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const contactAPI = {
  submit: (data) => api.post('/contact', data),
};

export const partnershipAPI = {
  apply: (data) => api.post('/partnership/apply', data),
};

export const newsletterAPI = {
  subscribe: (email) => api.post('/newsletter/subscribe', { email }),
};

export default api;
```

### STEP 5: Create Docker Files

**Dockerfile (backend)**
```dockerfile
FROM node:18-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install --production

COPY . .

EXPOSE 5000

CMD ["node", "server.js"]
```

**Dockerfile (frontend)**
```dockerfile
FROM node:18-alpine AS build

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**docker-compose.yml**
```yaml
version: '3.8'

services:
  mongodb:
    image: mongo:6.0
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db
    environment:
      MONGO_INITDB_DATABASE: vantair

  backend:
    build: ./backend
    ports:
      - "5000:5000"
    environment:
      NODE_ENV: development
      MONGODB_URI: mongodb://mongodb:27017/vantair
      PORT: 5000
    depends_on:
      - mongodb
    volumes:
      - ./backend:/app
      - /app/node_modules

  frontend:
    build: ./frontend
    ports:
      - "3000:80"
    environment:
      REACT_APP_API_URL: http://localhost:5000/api
    depends_on:
      - backend

volumes:
  mongodb_data:
```

**nginx.conf**
```nginx
server {
    listen 80;
    server_name _;

    root /usr/share/nginx/html;
    index index.html;

    gzip on;
    gzip_types text/plain text/css text/javascript application/json;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:5000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }
}
```

### STEP 6: Setup Environment Files

**backend/.env**
```
NODE_ENV=development
PORT=5000
FRONTEND_URL=http://localhost:3000

MONGODB_URI=mongodb://localhost:27017/vantair

EMAIL_SERVICE=gmail
EMAIL_USER=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
ADMIN_EMAIL=admin@vantair.com

JWT_SECRET=development-secret-key-change-in-production
JWT_EXPIRE=7d
```

**frontend/.env.local**
```
REACT_APP_API_URL=http://localhost:5000/api
```

### STEP 7: Update package.json Scripts

**backend/package.json**
```json
{
  "scripts": {
    "start": "node server.js",
    "dev": "nodemon server.js",
    "test": "jest",
    "seed": "node scripts/seed.js"
  }
}
```

**frontend/package.json**
```json
{
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test",
    "eject": "react-scripts eject"
  }
}
```

---

## RUNNING THE APPLICATION

### Development Mode (Local)

```bash
# Terminal 1: Start MongoDB
mongod

# Terminal 2: Start Backend
cd backend
npm run dev

# Terminal 3: Start Frontend
cd frontend
npm start
```

### Development Mode (Docker)

```bash
docker-compose up
```

Access:
- Frontend: http://localhost:3000
- Backend: http://localhost:5000
- API: http://localhost:5000/api
- MongoDB: mongodb://localhost:27017

### Production Build

```bash
# Frontend
cd frontend
npm run build

# Backend (no build needed for Node)
# Just ensure all dependencies are installed
cd backend
npm install --production
```

---

## TESTING THE APPLICATION

### Frontend Testing

```bash
cd frontend
npm test

# Test with coverage
npm test -- --coverage
```

### Backend Testing

```bash
cd backend
npm test

# Example test file: backend/__tests__/contact.test.js
```

### API Testing with cURL

```bash
# Test contact form
curl -X POST http://localhost:5000/api/contact \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "company": "Acme Corp",
    "message": "Interested in partnership"
  }'

# Test partnership application
curl -X POST http://localhost:5000/api/partnership/apply \
  -H "Content-Type: application/json" \
  -d '{
    "companyName": "Tech Corp",
    "contactName": "Jane Smith",
    "email": "jane@techcorp.com",
    "phone": "+1234567890",
    "partnershipType": "distribution"
  }'
```

### Health Check

```bash
curl http://localhost:5000/api/health
```

---

## IMPLEMENTATION CHECKLIST

### Frontend Features
- [ ] Responsive design (mobile, tablet, desktop)
- [ ] Smooth scroll animations
- [ ] Contact form with validation
- [ ] Partnership application form
- [ ] Newsletter subscription
- [ ] Service showcase
- [ ] Partner logos gallery
- [ ] Call-to-action buttons
- [ ] Footer with links
- [ ] Mobile menu
- [ ] Form success notifications
- [ ] Error handling

### Backend Features
- [ ] Contact form endpoint
- [ ] Partnership application endpoint
- [ ] Newsletter subscription
- [ ] Email notifications
- [ ] Database persistence
- [ ] Admin dashboard API
- [ ] Analytics endpoints
- [ ] Input validation
- [ ] Error handling
- [ ] Rate limiting
- [ ] CORS configuration
- [ ] Security headers

### Deployment Readiness
- [ ] Environment variables configured
- [ ] Database migration scripts
- [ ] Automated backups setup
- [ ] Email service configured
- [ ] SSL certificate installed
- [ ] Domain DNS configured
- [ ] Nginx/reverse proxy configured
- [ ] Monitoring setup
- [ ] Error tracking configured
- [ ] Analytics configured
- [ ] CI/CD pipeline configured
- [ ] Load testing completed

---

## COMMON ISSUES & FIXES

### Port Already in Use
```bash
# Find process on port
lsof -i :3000
lsof -i :5000

# Kill process
kill -9 <PID>
```

### MongoDB Connection Error
```bash
# Check MongoDB is running
systemctl status mongod

# Start MongoDB
systemctl start mongod
```

### CORS Errors
```javascript
// In backend/server.js
app.use(cors({
  origin: [
    'http://localhost:3000',
    'https://vantair.com',
    'https://www.vantair.com'
  ],
  credentials: true
}));
```

### Email Not Sending
- Use Gmail App Password (not regular password)
- Check email is verified in SendGrid
- Check ADMIN_EMAIL is correct

---

## MONITORING & LOGS

```bash
# Docker logs
docker-compose logs -f backend
docker-compose logs -f frontend

# System logs
tail -f /var/log/nginx/error.log
tail -f /var/log/nginx/access.log

# Application logs
pm2 logs
```

---

## NEXT STEPS AFTER DEPLOYMENT

1. **Setup monitoring**: Sentry, New Relic, or similar
2. **Configure analytics**: Google Analytics, Mixpanel
3. **Setup backups**: Automated daily backups
4. **Configure CDN**: CloudFlare, Akamai
5. **Setup monitoring**: Uptime Robot
6. **Security audit**: OWASP Top 10
7. **Performance testing**: Load testing
8. **SEO optimization**: Google Search Console, robots.txt, sitemap.xml
9. **Setup email**: Newsletter automation
10. **User feedback**: Collect feedback and iterate

---

## SUPPORT & DOCUMENTATION

- Frontend Components: See `frontend/src/components/`
- Backend Routes: See `backend/routes/`
- API Documentation: See `DEPLOYMENT_GUIDE.md`
- Database Schema: See `backend/models/`

---

**Version**: 1.0.0
**Last Updated**: December 2024
**Status**: Production Ready