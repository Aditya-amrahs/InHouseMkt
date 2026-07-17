# Deployment Guide

This project is configured to keep local development unchanged while allowing a production deployment with:

- `Aiven MySQL`
- `Azure App Service` for the Spring Boot backend
- `Azure Static Web Apps` for the React frontend

## 1. Database first

Create the Aiven MySQL service and collect:

- host
- port
- database name
- username
- password
- SSL mode requirement

Use a JDBC URL in this format:

```text
jdbc:mysql://HOST:PORT/DATABASE?ssl-mode=REQUIRED
```

## 2. Backend second

The backend now supports a dedicated production Spring profile in:

- `marketplace-backend/src/main/resources/application-prod.properties`

Local development still uses H2 from `application.properties`.

### Azure App Service settings

Set these application settings in the backend App Service:

```text
SPRING_PROFILES_ACTIVE=prod
MARKETPLACE_DB_URL=jdbc:mysql://HOST:PORT/DATABASE?ssl-mode=REQUIRED
MARKETPLACE_DB_USERNAME=YOUR_DB_USERNAME
MARKETPLACE_DB_PASSWORD=YOUR_DB_PASSWORD
MARKETPLACE_JPA_DDL_AUTO=update
MARKETPLACE_CORS_ALLOWED_ORIGINS=https://YOUR_STATIC_WEB_APP_URL
MARKETPLACE_SESSION_COOKIE_SAME_SITE=none
MARKETPLACE_SESSION_COOKIE_SECURE=true
```

### Backend deployment order

1. Create the App Service.
2. Set the application settings above.
3. Deploy the Spring Boot jar.
4. Verify Swagger opens at:
   `https://YOUR_BACKEND.azurewebsites.net/swagger-ui.html`

## 3. Frontend third

The frontend now supports a production API base URL through:

- `marketplace-react/.env.production.example`

Copy that value into Azure Static Web Apps environment configuration:

```text
VITE_API_BASE_URL=https://YOUR_BACKEND.azurewebsites.net/api
```

The frontend also includes `staticwebapp.config.json` so direct refreshes on routes such as `/dashboard`, `/offers/1`, and `/requirements/2` resolve back to the SPA correctly.

## 4. Frontend deployment order

1. Create the Static Web App.
2. Point the build to the React app location.
3. Add `VITE_API_BASE_URL` with the backend HTTPS URL.
4. Deploy and verify login, dashboard, requirements, offers, proposals, and delete-account flow.

## 5. Local behavior

Local behavior is intentionally preserved:

- frontend dev server still runs on `http://localhost:5173`
- backend still runs on `http://localhost:8080`
- local API calls still work through the Vite proxy
- local persistence still uses H2

## 6. Recommended validation sequence

1. Verify backend health and Swagger directly on Azure.
2. Test login from the deployed frontend.
3. Create one requirement and one offer.
4. Test cross-user proposal submission.
5. Accept a proposal and verify status changes for both users.
6. Test account deletion last.
