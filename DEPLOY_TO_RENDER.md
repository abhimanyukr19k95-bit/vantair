# Deploy Vantair to Render.com

## ✅ Database Already Created
- **Name:** vantair-db
- **ID:** dpg-daoursjtqb8s73en5msg-a
- **Status:** Available ✓
- **Region:** Singapore

## Step 1: Push to GitHub (Required)

Render deploys from GitHub/GitLab/Bitbucket. Push your code:

```bash
# 1. Create a new repository on GitHub at: https://github.com/new
#    Name it: vantair
#    Keep it public or private (both work with Render)
#    Do NOT initialize with README (your repo already has files)

# 2. Push your code (replace YOUR_USERNAME with your GitHub username)
git remote add origin https://github.com/YOUR_USERNAME/vantair.git
git branch -M main
git push -u origin main
```

## Step 2: Deploy Backend to Render

1. Go to: https://dashboard.render.com/create?type=web
2. Connect your GitHub repository
3. Configure:
   - **Name:** `vantair-api`
   - **Region:** Singapore
   - **Branch:** main
   - **Root Directory:** (leave empty)
   - **Environment:** Docker
   - **Dockerfile Path:** `./vantair-backend/Dockerfile`
   - **Plan:** Free

4. Add Environment Variables (click "Advanced"):

   Get database connection info from: https://dashboard.render.com/d/dpg-daoursjtqb8s73en5msg-a

   ```
   SPRING_DATASOURCE_URL=<Copy "Internal Database URL" from database dashboard>
   SPRING_DATASOURCE_USERNAME=vantair_user
   SPRING_DATASOURCE_PASSWORD=<Copy password from database dashboard>
   SPRING_PROFILES_ACTIVE=prod
   VANTAIR_CORS_ALLOWED_ORIGINS=*
   ```

5. Set Health Check Path: `/api/products`
6. Click **Create Web Service**

Build will take ~3-5 minutes.

## Step 3: Deploy Frontend to Render

1. Go to: https://dashboard.render.com/create?type=static
2. Select the same GitHub repository
3. Configure:
   - **Name:** `vantair`
   - **Region:** Singapore
   - **Branch:** main
   - **Root Directory:** `VANTAIR-Website-Complete`
   - **Build Command:** `npm ci && npm run build`
   - **Publish Directory:** `dist`
   - **Plan:** Free

4. Add Rewrite Rule (after deployment, in Settings → Redirects/Rewrites):
   ```
   Source: /api/*
   Destination: https://vantair-api.onrender.com/api/:splat
   Action: Rewrite
   ```

5. Click **Create Static Site**

Build will take ~1-2 minutes.

## Step 4: Verify Deployment

After both services deploy successfully:

- **Frontend:** https://vantair.onrender.com
- **Backend API:** https://vantair-api.onrender.com/api/products
- **Swagger Docs:** https://vantair-api.onrender.com/swagger-ui.html
- **Database Dashboard:** https://dashboard.render.com/d/dpg-daoursjtqb8s73en5msg-a

## Important Notes

### Free Tier Behavior
- Services spin down after 15 minutes of inactivity
- First request after spin-down takes ~30 seconds to wake up
- Database stays always available

### Database Credentials
- Username: `vantair_user` (not `postgres` - Render reserves that)
- Get connection string from database dashboard "Info" tab

### CORS Configuration
- Backend currently accepts all origins (`*`) for testing
- Update `VANTAIR_CORS_ALLOWED_ORIGINS` to your frontend URL in production

### Build Times
- Backend: 3-5 minutes (Maven + Docker multi-stage build)
- Frontend: 1-2 minutes (npm install + Vite build)

## Troubleshooting

### Backend won't start?
- Check logs in Render dashboard
- Verify database connection string is correct (use "Internal Database URL")
- Ensure all environment variables are set
- Database must be in "Available" status

### Frontend can't reach API?
- Verify backend is fully deployed (check health endpoint)
- Confirm rewrite rule is configured correctly
- Test backend endpoint directly: `curl https://vantair-api.onrender.com/api/products`

### Database connection errors?
- Copy exact connection string from database dashboard
- Use "Internal Database URL" (not External)
- Verify password matches

## Your API Key

Your Render API key is: `rnd_iFmYAvlJ0tTTRA70TgC2y09JpW3P`

Keep this secure. View/manage at: https://dashboard.render.com/u/settings#api-keys
