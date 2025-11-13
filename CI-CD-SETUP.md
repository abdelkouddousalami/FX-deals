# CI/CD Pipeline Setup Guide

This project includes a complete CI/CD pipeline using GitHub Actions that automatically builds, tests, and deploys your application.

## Pipeline Overview

The CI/CD pipeline includes:
- **Continuous Integration**: Automated build and testing on every push/PR
- **Code Quality**: SonarCloud integration for code analysis
- **Docker**: Automated Docker image building and publishing
- **Continuous Deployment**: Automatic deployment to staging and production

## Workflows

### 1. CI/CD Pipeline (`.github/workflows/ci-cd.yml`)

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop`

**Jobs:**

#### Build and Test
- Checkout code
- Set up JDK 17
- Build with Maven
- Run all tests (23 tests)
- Generate code coverage report
- Upload coverage to Codecov
- Package application as JAR
- Upload artifact for deployment

#### Docker Build
- Build Docker image
- Push to Docker Hub
- Tag with branch name and SHA
- Cache layers for faster builds

#### Code Quality Analysis
- Run SonarCloud analysis
- Check code quality metrics
- Identify code smells and vulnerabilities

#### Deploy to Staging
- Deploy to Heroku staging environment
- Run health checks
- Verify deployment

#### Deploy to Production
- Deploy to Heroku production (requires approval)
- Run health checks
- Notify on success

### 2. Pull Request Checks (`.github/workflows/pr-check.yml`)

**Triggers:**
- Pull request opened, synchronized, or reopened

**Actions:**
- Validate Maven project
- Check code formatting
- Run all tests
- Verify test coverage
- Build project
- Comment on PR with results

## Setup Instructions

### Step 1: Configure GitHub Secrets

Go to your repository: `Settings` → `Secrets and variables` → `Actions`

Add these secrets:

#### Required Secrets:

1. **Docker Hub** (for Docker image publishing):
   ```
   DOCKER_USERNAME = your-dockerhub-username
   DOCKER_PASSWORD = your-dockerhub-password or access token
   ```

2. **Heroku** (for deployment):
   ```
   HEROKU_API_KEY = your-heroku-api-key
   HEROKU_EMAIL = your-heroku-email
   ```

3. **SonarCloud** (for code quality - optional):
   ```
   SONAR_TOKEN = your-sonarcloud-token
   ```

### Step 2: Get Heroku API Key

```bash
# Login to Heroku
heroku login

# Get your API key
heroku auth:token
```

Copy the token and add it as `HEROKU_API_KEY` secret in GitHub.

### Step 3: Create Heroku Apps

```bash
# Create staging app
heroku create fx-deals-staging

# Create production app
heroku create fx-deals-warehouse

# Set Java version
heroku config:set JAVA_RUNTIME_VERSION=17 -a fx-deals-staging
heroku config:set JAVA_RUNTIME_VERSION=17 -a fx-deals-warehouse
```

### Step 4: Set Up Docker Hub (Optional)

1. Create account at https://hub.docker.com
2. Create repository: `fx-deals-warehouse`
3. Generate access token: Account Settings → Security → New Access Token
4. Add `DOCKER_USERNAME` and `DOCKER_PASSWORD` to GitHub secrets

### Step 5: Set Up SonarCloud (Optional)

1. Go to https://sonarcloud.io
2. Sign in with GitHub
3. Add your repository
4. Get your token: My Account → Security → Generate Token
5. Add `SONAR_TOKEN` to GitHub secrets

### Step 6: Enable GitHub Actions

1. Go to repository → `Actions` tab
2. Enable workflows if prompted
3. Push code to trigger the pipeline

## Pipeline Stages

### Stage 1: Build and Test (Runs on every push/PR)
```
Checkout → Setup Java → Build → Test → Coverage → Package → Upload Artifact
```

### Stage 2: Docker Build (Runs on push to main)
```
Checkout → Setup Docker → Login → Build Image → Push to Registry
```

### Stage 3: Code Quality (Runs in parallel)
```
Checkout → Setup Java → SonarCloud Analysis
```

### Stage 4: Deploy Staging (Runs after build on main)
```
Download Artifact → Deploy to Heroku Staging → Health Check
```

### Stage 5: Deploy Production (Runs after staging)
```
Download Artifact → Deploy to Heroku Production → Health Check → Notify
```

## Deployment URLs

After successful deployment, your application will be available at:

- **Staging**: https://fx-deals-staging.herokuapp.com
  - Swagger UI: https://fx-deals-staging.herokuapp.com/swagger-ui.html
  
- **Production**: https://fx-deals-warehouse.herokuapp.com
  - Swagger UI: https://fx-deals-warehouse.herokuapp.com/swagger-ui.html

## Monitoring Pipeline Status

### View Pipeline Status
Go to repository → `Actions` tab

### Status Badges
Add to your README.md:

```markdown
![CI/CD Pipeline](https://github.com/abdelkouddousalami/FX-deals/actions/workflows/ci-cd.yml/badge.svg)
![PR Checks](https://github.com/abdelkouddousalami/FX-deals/actions/workflows/pr-check.yml/badge.svg)
```

## Triggering the Pipeline

### Automatic Triggers:

1. **Push to main/develop**:
   ```bash
   git add .
   git commit -m "feat: new feature"
   git push origin main
   ```

2. **Create Pull Request**:
   - Create PR on GitHub
   - Pipeline runs automatically
   - Results posted as comment

3. **Manual Trigger**:
   - Go to Actions tab
   - Select workflow
   - Click "Run workflow"

## Pipeline Flow Diagram

```
Push to main
    ↓
Build & Test ──→ Tests Pass? ──→ No ──→ Fail & Notify
    ↓                Yes
    ↓
Docker Build ──→ Push Image
    ↓
Code Quality ──→ SonarCloud
    ↓
Deploy Staging ──→ Health Check ──→ Pass?
    ↓                                 Yes
    ↓
Deploy Production ──→ Health Check ──→ Success!
    ↓
Notify (Swagger URL available)
```

## Troubleshooting

### Pipeline Fails at Build
- Check Java version compatibility
- Verify Maven dependencies
- Check for compilation errors locally

### Pipeline Fails at Test
- Run tests locally: `./mvnw test`
- Check test logs in Actions tab
- Fix failing tests and push again

### Deployment Fails
- Verify Heroku API key is correct
- Check Heroku app names match
- Verify Procfile exists
- Check Heroku logs: `heroku logs --tail -a app-name`

### Docker Build Fails
- Verify Docker Hub credentials
- Check Dockerfile syntax
- Ensure repository exists on Docker Hub

## Manual Deployment

If you need to deploy manually:

```bash
# Deploy to staging
git push heroku-staging main

# Deploy to production
git push heroku-production main
```

## Environment Variables

Set these on Heroku for each environment:

```bash
# Set production environment
heroku config:set SPRING_PROFILES_ACTIVE=prod -a fx-deals-warehouse

# Set staging environment
heroku config:set SPRING_PROFILES_ACTIVE=prod -a fx-deals-staging
```

## Next Steps

1. Push code to trigger first pipeline run
2. Monitor in Actions tab
3. Fix any issues that arise
4. Configure branch protection rules
5. Set up deployment approvals for production

## Benefits

- Automated testing on every change
- Consistent build process
- Automatic deployment to staging and production
- Docker images for containerized deployment
- Code quality monitoring
- Fast feedback on PRs
- Zero-downtime deployments

Your application is now fully automated from code commit to production deployment!
