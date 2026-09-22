# VANTAIR WEBSITE - COMPLETE DEPLOYMENT GUIDE
## Production-Ready Deployment Instructions

---

## TABLE OF CONTENTS
1. [Local Development Setup](#local-development-setup)
2. [Docker Setup](#docker-setup)
3. [Deployment Platforms](#deployment-platforms)
4. [Database Setup](#database-setup)
5. [Email Configuration](#email-configuration)
6. [Security & Optimization](#security--optimization)
7. [Monitoring & Analytics](#monitoring--analytics)
8. [Production Checklist](#production-checklist)
9. [Troubleshooting](#troubleshooting)

---

## LOCAL DEVELOPMENT SETUP

### Prerequisites
- Node.js v16+ and npm v8+
- MongoDB Community Edition (or MongoDB Atlas account)
- Git
- Visual Studio Code (recommended)

### Step 1: Clone Repository
```bash
git clone https://github.com/yourusername/vantair-website.git
cd vantair-website
```

### Step 2: Backend Setup
```bash
cd backend
npm install

# Create .env file
cp .env.example .env

# Edit .env with your configuration
nano .env

# Run migrations (if applicable)
npm run migrate

# Start development server
npm run dev
```

### Step 3: Frontend Setup
```bash
cd ../frontend
npm install

# Create .env file
echo "REACT_APP_API_URL=http://localhost:5000/api" > .env.local

# Start development server
npm start
```

The application will be available at:
- Frontend: http://localhost:3000
- Backend: http://localhost:5000
- API: http://localhost:5000/api

---

## DOCKER SETUP

### Backend Dockerfile

```dockerfile
# Dockerfile for VANTAIR Backend

FROM node:18-alpine

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install --production

# Copy application code
COPY . .

# Expose port
EXPOSE 5000

# Start application
CMD ["node", "server.js"]
```

### Frontend Dockerfile

```dockerfile
# Dockerfile for VANTAIR Frontend

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

### docker-compose.yml

```yaml
version: '3.8'

services:
  mongodb:
    image: mongo:6.0
    container_name: vantair-mongodb
    environment:
      MONGO_INITDB_DATABASE: vantair
    volumes:
      - mongodb_data:/data/db
    ports:
      - "27017:27017"
    healthcheck:
      test: echo 'db.runCommand("ping").ok' | mongosh localhost:27017/test --quiet
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: vantair-backend
    environment:
      NODE_ENV: development
      MONGODB_URI: mongodb://mongodb:27017/vantair
      PORT: 5000
    ports:
      - "5000:5000"
    depends_on:
      mongodb:
        condition: service_healthy
    volumes:
      - ./backend:/app
      - /app/node_modules
    healthcheck:
      test: curl -f http://localhost:5000/api/health || exit 1
      interval: 30s
      timeout: 10s
      retries: 3

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: vantair-frontend
    environment:
      REACT_APP_API_URL: http://localhost:5000/api
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mongodb_data:

networks:
  default:
    name: vantair-network
```

### Run with Docker Compose

```bash
# Development
docker-compose up

# Production (detached)
docker-compose -f docker-compose.prod.yml up -d

# View logs
docker-compose logs -f backend

# Stop services
docker-compose down
```

---

## DEPLOYMENT PLATFORMS

### Option 1: Vercel (Frontend Only)

**Best for:** React frontend with serverless backend

1. Push code to GitHub
2. Connect GitHub repo to Vercel
3. Configure environment variables in Vercel dashboard
4. Deploy with one click

```bash
npm install -g vercel
vercel --prod
```

### Option 2: Railway.app (Full Stack)

**Best for:** Quick, modern deployment

1. Create Railway account
2. Connect GitHub repository
3. Add services:
   - Backend (Node.js)
   - Frontend (React)
   - Database (MongoDB)
4. Configure environment variables
5. Deploy

### Option 3: DigitalOcean (Self-Managed)

**Best for:** Full control and scalability

#### Step 1: Create Droplet
```bash
# Create Ubuntu 22.04 droplet (4GB RAM minimum)
# SSH into droplet
ssh root@your_ip

# Update system
apt update && apt upgrade -y

# Install Docker
apt install docker.io docker-compose -y
systemctl enable docker
systemctl start docker
```

#### Step 2: Setup Application
```bash
cd /app
git clone https://github.com/yourusername/vantair-website.git
cd vantair-website

# Create production .env
nano .env

# Pull and run containers
docker-compose -f docker-compose.prod.yml up -d
```

#### Step 3: Setup Nginx Reverse Proxy
```bash
apt install nginx certbot python3-certbot-nginx -y

# Create nginx config
cat > /etc/nginx/sites-available/vantair << 'EOF'
server {
    server_name vantair.com www.vantair.com;

    location / {
        proxy_pass http://localhost:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }

    location /api {
        proxy_pass http://localhost:5000;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
    }
}
EOF

# Enable site
ln -s /etc/nginx/sites-available/vantair /etc/nginx/sites-enabled/
nginx -t
systemctl restart nginx

# Setup SSL with Let's Encrypt
certbot --nginx -d vantair.com -d www.vantair.com
```

### Option 4: AWS (EC2 + RDS)

**Best for:** Enterprise-grade deployment

1. **EC2 Instance Setup**
   - Launch Ubuntu 22.04 instance
   - Configure security groups
   - Allocate elastic IP

2. **RDS Database**
   - Create MongoDB Atlas cluster (recommended)
   - Or use DocumentDB

3. **Application Deployment**
   - SSH into EC2
   - Clone repository
   - Install dependencies
   - Configure nginx/Apache
   - Setup SSL with ACM

---

## DATABASE SETUP

### MongoDB Atlas (Cloud Recommended)

1. Create free account at mongodb.com/cloud
2. Create cluster (free tier available)
3. Add IP whitelist
4. Create database user
5. Get connection string

```
mongodb+srv://username:password@cluster.mongodb.net/vantair?retryWrites=true&w=majority
```

### Local MongoDB Setup

```bash
# macOS
brew install mongodb-community
brew services start mongodb-community

# Linux (Ubuntu)
curl -fsSL https://www.mongodb.org/static/pgp/server-7.0.asc | apt-key add -
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-7.0.list
apt-get update
apt-get install -y mongodb-org
systemctl start mongod

# Windows
# Download from mongodb.com and run installer
```

### Initialize Database Collections

```bash
# Run seed script
node scripts/seed.js
```

**seed.js**
```javascript
const mongoose = require('mongoose');
require('dotenv').config();

const Service = require('./models/Service');
const Partner = require('./models/Partner');

async function seedDatabase() {
  try {
    await mongoose.connect(process.env.MONGODB_URI);

    // Clear existing data
    await Service.deleteMany();
    await Partner.deleteMany();

    // Seed services
    const services = [
      {
        name: 'WeHire',
        subtitle: 'Placement Consultant Services',
        description: 'Recruitment solutions, talent matching, and career consulting.',
        features: ['Recruitment Solutions', 'Talent Matching', 'Career Consulting'],
      },
      // ... add all 9 services
    ];

    await Service.insertMany(services);

    // Seed partners
    const partners = [
      {
        name: 'Turtlemint Pro',
        category: 'Insurance',
        description: 'Leading digital insurance platform',
      },
      // ... add all partners
    ];

    await Partner.insertMany(partners);

    console.log('Database seeded successfully!');
    process.exit(0);
  } catch (error) {
    console.error('Error seeding database:', error);
    process.exit(1);
  }
}

seedDatabase();
```

---

## EMAIL CONFIGURATION

### Gmail Setup (Development)

1. Enable 2-factor authentication on Google Account
2. Create App Password: https://myaccount.google.com/apppasswords
3. Use generated password in .env

```
EMAIL_USER=your-email@gmail.com
EMAIL_PASSWORD=your-16-char-app-password
```

### SendGrid Setup (Production)

1. Create SendGrid account (sendgrid.com)
2. Verify sender identity
3. Create API key
4. Update .env

```
EMAIL_SERVICE=sendgrid
SENDGRID_API_KEY=SG.xxxxxxxxxxxxx
```

### Email Templates

Create reusable email templates in `backend/templates/`

```html
<!-- templates/contact-confirmation.html -->
<h2>Thank you for contacting us!</h2>
<p>We've received your message and will respond shortly.</p>
```

---

## SECURITY & OPTIMIZATION

### Environment Variables
✅ Never commit .env file
✅ Use strong, random secrets
✅ Rotate secrets regularly
✅ Use different secrets for different environments

### HTTPS/SSL
```bash
# Let's Encrypt (free)
certbot certonly --standalone -d vantair.com

# Update nginx
listen 443 ssl http2;
ssl_certificate /etc/letsencrypt/live/vantair.com/fullchain.pem;
ssl_certificate_key /etc/letsencrypt/live/vantair.com/privkey.pem;
```

### Security Headers
```nginx
add_header X-Frame-Options "SAMEORIGIN" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-XSS-Protection "1; mode=block" always;
add_header Referrer-Policy "no-referrer-when-downgrade" always;
add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;
```

### Database Security
- Use connection string with credentials
- Whitelist IP addresses
- Enable encryption at rest
- Regular backups
- Enable audit logging

### API Security
✅ Rate limiting (implemented in server.js)
✅ Input validation
✅ CORS properly configured
✅ JWT authentication for admin endpoints
✅ SQL injection prevention (using Mongoose)

### Performance Optimization
```bash
# Frontend
npm run build  # Minified production build

# Enable gzip compression in nginx
gzip on;
gzip_types text/plain text/css text/javascript application/json;
gzip_min_length 1000;

# Enable caching
add_header Cache-Control "public, max-age=3600" always;

# CDN (Cloudflare recommended)
# Point DNS to Cloudflare nameservers
```

---

## MONITORING & ANALYTICS

### Application Monitoring

**Sentry** (Error tracking)
```bash
npm install @sentry/react @sentry/tracing
```

```javascript
// frontend/src/index.js
import * as Sentry from "@sentry/react";

Sentry.init({
  dsn: "https://key@sentry.io/project-id",
  environment: "production",
  tracesSampleRate: 1.0,
});
```

**New Relic** (Performance monitoring)
```bash
npm install newrelic
```

### Uptime Monitoring
- Use Uptime Robot (free)
- Monitor: https://vantair.com/api/health
- Get alerts if service goes down

### Analytics
```bash
npm install react-ga4
```

```javascript
// frontend/src/App.js
import GA4 from 'react-ga4';

GA4.initialize('G-XXXXXXXXXX');
```

### Logging
```bash
# Backend logging with Winston
npm install winston

// server.js
const logger = require('./config/logger');
logger.info('Application started');
```

---

## PRODUCTION CHECKLIST

### Before Going Live

- [ ] Domain name registered and DNS configured
- [ ] SSL certificate installed
- [ ] Database backups automated
- [ ] Environment variables configured
- [ ] Email service configured and tested
- [ ] Security headers implemented
- [ ] Rate limiting enabled
- [ ] CORS properly configured
- [ ] Monitoring and logging setup
- [ ] Error tracking (Sentry) configured
- [ ] Analytics (Google Analytics) configured
- [ ] CDN configured (optional)
- [ ] Database indexes created
- [ ] Nginx/reverse proxy configured
- [ ] Automated backups scheduled
- [ ] CI/CD pipeline configured
- [ ] Documentation updated
- [ ] Team trained on deployment process
- [ ] Disaster recovery plan documented
- [ ] Load testing completed
- [ ] Security audit completed

### CI/CD Pipeline (.github/workflows/deploy.yml)

```yaml
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Run tests
      run: |
        cd backend && npm test
        cd ../frontend && npm test
    
    - name: Build frontend
      run: cd frontend && npm run build
    
    - name: Deploy to production
      env:
        DEPLOY_KEY: ${{ secrets.DEPLOY_KEY }}
      run: |
        mkdir -p ~/.ssh
        echo "$DEPLOY_KEY" > ~/.ssh/deploy_key
        chmod 600 ~/.ssh/deploy_key
        ssh -i ~/.ssh/deploy_key user@server "cd /app && git pull && npm install && npm run build && systemctl restart vantair"
```

---

## TROUBLESHOOTING

### Common Issues

**1. MongoDB Connection Error**
```
Error: connect ECONNREFUSED 127.0.0.1:27017
```
Solution: Ensure MongoDB is running
```bash
systemctl start mongod
# or
brew services start mongodb-community
```

**2. Port Already in Use**
```
Error: listen EADDRINUSE: address already in use :::5000
```
Solution: Kill process on port
```bash
lsof -i :5000
kill -9 PID
```

**3. CORS Errors**
```
Access to XMLHttpRequest blocked by CORS policy
```
Solution: Check CORS configuration in server.js
```javascript
cors({
  origin: ['https://vantair.com', 'http://localhost:3000'],
  credentials: true
})
```

**4. Email Not Sending**
```
Error: Invalid login: 535-5.7.8 Username and password not accepted
```
Solution: 
- Use Gmail App Password (not regular password)
- Check email credentials in .env
- Verify sender email is authorized in SendGrid

**5. Slow Database Queries**
```
Solution: Add indexes to frequently queried fields
db.contacts.createIndex({ email: 1 })
db.partnerships.createIndex({ status: 1 })
```

**6. High Memory Usage**
```
Solution: Enable node memory debugging
node --max-old-space-size=2048 server.js
```

---

## MONITORING COMMANDS

```bash
# View application logs
docker-compose logs -f backend
tail -f /var/log/nginx/access.log

# Check disk space
df -h

# Monitor system resources
top
htop

# Database backup
mongodump --uri="mongodb+srv://user:pass@cluster.mongodb.net/vantair" --out ./backups/

# Database restore
mongorestore --uri="mongodb+srv://user:pass@cluster.mongodb.net/vantair" ./backups/vantair/
```

---

## MAINTENANCE & UPDATES

### Regular Tasks
- [ ] Weekly: Check error logs and fix issues
- [ ] Monthly: Update dependencies (`npm update`)
- [ ] Monthly: Review security updates
- [ ] Quarterly: Performance audit
- [ ] Quarterly: Security audit
- [ ] Annually: Renew SSL certificate

```bash
# Check for vulnerable packages
npm audit

# Update packages safely
npm update
npm audit fix

# Security vulnerability scan
npm audit --audit-level=high
```

---

## SUPPORT & RESOURCES

- **Documentation**: docs.vantair.com
- **API Docs**: api.vantair.com/docs
- **Support Email**: support@vantair.com
- **GitHub Issues**: github.com/vantair/website/issues
- **Status Page**: status.vantair.com

---

## QUICK DEPLOYMENT COMMAND REFERENCE

```bash
# Development
npm run dev

# Production Build
npm run build

# Docker deployment
docker-compose -f docker-compose.prod.yml up -d

# Update production
git pull origin main
npm install
npm run build
systemctl restart vantair

# View logs
docker-compose logs -f

# Database backup
mongodump --uri=$MONGODB_URI --out ./backups/$(date +%Y%m%d)
```

---

**Last Updated**: December 2024
**Version**: 1.0.0
**Maintained By**: VANTAIR Team