# Deployment Guide - Making Swagger UI Available Online

This guide provides step-by-step instructions to deploy your FX Deals Warehouse application and make the Swagger UI accessible online.

## Prerequisites
- Git installed and configured
- Your project pushed to GitHub
- Account on chosen cloud platform

---

## Option 1: Deploy to Heroku (Recommended - Easiest)

### Step 1: Install Heroku CLI
Download and install from: https://devcenter.heroku.com/articles/heroku-cli

### Step 2: Login to Heroku
```bash
heroku login
```

### Step 3: Create Heroku Application
```bash
cd "c:\Users\abdoa\Youcode A2\ProgressSoft"
heroku create fx-deals-warehouse
# This will create: https://fx-deals-warehouse-xxxxx.herokuapp.com
```

### Step 4: Add MySQL Database (Optional - Free Tier)
```bash
heroku addons:create jawsdb:kitefin
```

Or use H2 in-memory database (already configured):
```bash
heroku config:set SPRING_PROFILES_ACTIVE=prod
```

### Step 5: Deploy
```bash
git push heroku main
```

### Step 6: Open Your Application
```bash
heroku open
```

Your Swagger UI will be available at:
- **Swagger UI**: https://fx-deals-warehouse-xxxxx.herokuapp.com/swagger-ui.html
- **API Docs**: https://fx-deals-warehouse-xxxxx.herokuapp.com/api-docs
- **Health Check**: https://fx-deals-warehouse-xxxxx.herokuapp.com/api/fx-deals/health

### View Logs
```bash
heroku logs --tail
```

---

## Option 2: Deploy to Railway (Alternative - Also Free)

### Step 1: Create Account
Visit: https://railway.app and sign up with GitHub

### Step 2: Deploy from GitHub
1. Click "New Project"
2. Select "Deploy from GitHub repo"
3. Choose your repository: `FX-deals`
4. Railway will auto-detect Spring Boot and deploy

### Step 3: Add Environment Variables (Optional)
```
SPRING_PROFILES_ACTIVE=prod
PORT=8080
```

### Step 4: Generate Domain
Railway will provide a URL like: https://fx-deals-warehouse.railway.app

Your Swagger UI will be at:
- **Swagger UI**: https://fx-deals-warehouse.railway.app/swagger-ui.html

---

## Option 3: Deploy to Render (Another Alternative)

### Step 1: Create Account
Visit: https://render.com and sign up

### Step 2: Create New Web Service
1. Click "New +" → "Web Service"
2. Connect your GitHub repository
3. Configure:
   - **Name**: fx-deals-warehouse
   - **Environment**: Java
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/*.jar`
   - **Instance Type**: Free

### Step 3: Add Environment Variables
```
SPRING_PROFILES_ACTIVE=prod
```

### Step 4: Deploy
Render will automatically deploy your application.

Your Swagger UI will be at:
- **Swagger UI**: https://fx-deals-warehouse.onrender.com/swagger-ui.html

---

## Option 4: Deploy to Azure App Service

### Step 1: Install Azure CLI
Download from: https://learn.microsoft.com/en-us/cli/azure/install-azure-cli

### Step 2: Login to Azure
```bash
az login
```

### Step 3: Create Resource Group
```bash
az group create --name fx-deals-rg --location eastus
```

### Step 4: Create App Service Plan
```bash
az appservice plan create --name fx-deals-plan --resource-group fx-deals-rg --sku B1 --is-linux
```

### Step 5: Create Web App
```bash
az webapp create --resource-group fx-deals-rg --plan fx-deals-plan --name fx-deals-warehouse --runtime "JAVA:17-java17"
```

### Step 6: Deploy with Maven
```bash
./mvnw clean package -DskipTests
az webapp deploy --resource-group fx-deals-rg --name fx-deals-warehouse --src-path target/*.jar
```

Your Swagger UI will be at:
- **Swagger UI**: https://fx-deals-warehouse.azurewebsites.net/swagger-ui.html

---

## Option 5: Deploy to AWS Elastic Beanstalk

### Step 1: Install AWS EB CLI
```bash
pip install awsebcli
```

### Step 2: Initialize EB
```bash
eb init -p java-17 fx-deals-warehouse --region us-east-1
```

### Step 3: Create Environment
```bash
eb create fx-deals-env
```

### Step 4: Deploy
```bash
./mvnw clean package -DskipTests
eb deploy
```

### Step 5: Open Application
```bash
eb open
```

Your Swagger UI will be at:
- **Swagger UI**: http://fx-deals-env.xxxxx.us-east-1.elasticbeanstalk.com/swagger-ui.html

---

## Quick Start: Deploy to Heroku (5 Minutes)

Here's the fastest way to get your Swagger online:

```bash
# 1. Login to Heroku
heroku login

# 2. Create app
cd "c:\Users\abdoa\Youcode A2\ProgressSoft"
heroku create

# 3. Set environment
heroku config:set SPRING_PROFILES_ACTIVE=prod

# 4. Deploy
git push heroku main

# 5. Open browser
heroku open
```

Then navigate to `/swagger-ui.html` on the opened URL.

---

## Verification Steps

After deployment, verify your API is working:

1. **Health Check**:
   ```bash
   curl https://your-app-url.herokuapp.com/api/fx-deals/health
   ```

2. **Access Swagger UI**:
   Open browser: `https://your-app-url.herokuapp.com/swagger-ui.html`

3. **Test API**:
   Use Swagger UI to test the endpoints directly online

---

## Updating Your Application

When you make changes:

```bash
# Commit changes
git add .
git commit -m "Update application"

# Push to GitHub
git push origin main

# Deploy to Heroku
git push heroku main
```

---

## Custom Domain (Optional)

### For Heroku:
```bash
heroku domains:add www.yourdomain.com
```

Then configure your DNS provider to point to the Heroku DNS target.

---

## Troubleshooting

### Application Not Starting
```bash
# Check logs
heroku logs --tail

# Restart application
heroku restart
```

### Database Connection Issues
```bash
# Check database is attached
heroku addons

# View database credentials
heroku config
```

### Port Configuration
The application automatically uses `$PORT` environment variable provided by cloud platforms.

---

## Cost Considerations

- **Heroku**: Free tier available (with sleep after 30 min inactivity)
- **Railway**: $5/month with free trial credits
- **Render**: Free tier available
- **Azure**: Free tier for 12 months, then starts at ~$13/month
- **AWS**: Free tier for 12 months

---

## Security Considerations

For production deployment:

1. Add authentication to Swagger UI
2. Use HTTPS (automatically provided by most platforms)
3. Configure CORS properly
4. Use environment variables for sensitive data
5. Enable rate limiting

---

## Share Your Swagger URL

Once deployed, you can share:
- **Swagger UI**: `https://your-app-name.herokuapp.com/swagger-ui.html`
- **API Documentation**: `https://your-app-name.herokuapp.com/api-docs`

Perfect for:
- HR demonstrations
- Client presentations
- API documentation sharing
- Integration with other teams

---

## Next Steps

1. Deploy using one of the methods above
2. Test all endpoints via Swagger UI
3. Add the live URL to your README.md
4. Share the Swagger URL with HR/recruiters
5. Consider adding authentication for production use

---

## Support

If you encounter issues:
- Check platform-specific documentation
- Review application logs
- Verify environment variables are set correctly
- Ensure the application builds successfully locally first
