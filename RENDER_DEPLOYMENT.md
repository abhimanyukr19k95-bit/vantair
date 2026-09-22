# Vantair Render Deployment Guide

## ✅ Already Complete
- PostgreSQL Database Created: `vantair-db` (dpg-daoursjtqb8s73en5msg-a)
- Database Status: Available
- Region: Singapore
- Database Name: `vantair`
- Database User: `vantair_user`

## Next Steps: Deploy via Render Dashboard

### 1. Push Code to GitHub/GitLab (Required)

Render deploys from a git repository. Push your code to GitHub:

```bash
# Create a new repository on GitHub, then:
git remote add origin https://github.com/YOUR_USERNAME/vantair.git
git branch -M main
git push -u origin main
```

### 2. Deploy Spring Boot Backend

**Dashboard URL:** https://dashboard.render.com/create?type=web

**Settings:**
- **Name:** `vantair-api`
- **Region:** Singapore
- **Branch:** `main`
- **Root Directory:** (leave empty)
- **Environment:** Docker
- **Dockerfile Path:** `./vantair-backend/Dockerfile`
- **Plan:** Free

**Environment Variables:**
```
SPRING_DATASOURCE_URL=<Get from vantair-db connection info>
SPRING_DATASOURCE_USERNAME=vantair_user
SPRING_DATASOURCE_PASSWORD=<Get from vantair-db connection info>
SPRING_PROFILES_ACTIVE=prod
VANTAIR_CORS_ALLOWED_ORIGINS=*
```

**Health Check Path:** `/api/products`

### 3. Deploy React Frontend

**Dashboard URL:** https://dashboard.render.com/create?type=static

**Settings:**
- **Name:** `vantair`
- **Region:** Singapore
- **Branch:** `main`
- **Root Directory:** `VANTAIR-Website-Complete`
- **Build Command:** `npm ci && npm run build`
- **Publish Directory:** `dist`
- **Plan:** Free

**Rewrite Rules (for API proxy):**
```
Source: /api/*
Destination: https://vantair-api.onrender.com/api/:splat
```

## Database Connection Info

Get your database connection details:
1. Go to https://dashboard.render.com/d/dpg-daoursjtqb8s73en5msg-a
2. Click "Info" tab
3. Copy the "Internal Database URL" for backend environment variable

## Expected URLs After Deployment

- **Frontend:** https://vantair.onrender.com
- **Backend API:** https://vantair-api.onrender.com
- **Swagger UI:** https://vantair-api.onrender.com/swagger-ui.html
- **Database:** dpg-daoursjtqb8s73en5msg-a.singapore-postgres.render.com

## Important Notes

1. **Free Tier Spin Down:** Render free tier services spin down after 15 minutes of inactivity. First request will take ~30 seconds to wake up.

2. **Database Username:** Note that we're using `vantair_user` instead of `postgres` (Render reserves that username).

3. **CORS:** Backend is configured to accept requests from any origin (`*`) for initial testing. Restrict this in production.

4. **Build Time:** Backend build takes ~3-5 minutes (Maven + Docker). Frontend build takes ~1-2 minutes.

## Verification

After deployment:
1. Check backend health: `https://vantair-api.onrender.com/api/products`
2. Check Swagger docs: `https://vantair-api.onrender.com/swagger-ui.html`
3. Open frontend: `https://vantair.onrender.com`

## Troubleshooting

If backend fails to start:
- Check logs in Render dashboard
- Verify database connection string is correct
- Ensure database password is set correctly
- Check that port 8080 is exposed (handled by Dockerfile)

If frontend can't reach API:
- Verify rewrite rules are configured
- Check that backend is fully deployed and healthy
- Test backend endpoint directly first
