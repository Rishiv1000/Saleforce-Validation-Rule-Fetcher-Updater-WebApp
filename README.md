# SF Validation Manager

A web app to manage Salesforce Account validation rules — fetch, enable, disable, and deploy changes directly from the browser.

## Project Structure

```
frontend/          React + Vite + Tailwind CSS
springboot-backend/ Java Spring Boot backend
```

## Setup

### 1. Salesforce Connected App

1. Go to Salesforce Setup → External Client App Manager → New
2. Enable OAuth, set Callback URL: `http://localhost:8080/oauth2/callback`
3. Add scopes: `api`, `web`, `refresh_token`
4. Save and copy Consumer Key + Consumer Secret

### 2. Backend (Node.js)

> Note: Node.js backend files are in the `nodejs-backend` folder if present.

```bash
cd nodejs-backend
cp .env.example .env
# Fill in SF_CLIENT_ID and SF_CLIENT_SECRET
npm install
node server.js
```
Runs on http://localhost:8080

### 3. Backend (Spring Boot)

```bash
cd springboot-backend
# Set environment variables:
# SF_CLIENT_ID, SF_CLIENT_SECRET, SF_REDIRECT_URI, FRONTEND_URL
mvn spring-boot:run
```
Runs on http://localhost:8081

### 4. Frontend

```bash
cd frontend
npm install
npm run dev
```
Runs on http://localhost:5173

## Environment Variables

| Variable | Description |
|---|---|
| `SF_CLIENT_ID` | Salesforce Connected App Consumer Key |
| `SF_CLIENT_SECRET` | Salesforce Connected App Consumer Secret |
| `SF_REDIRECT_URI` | `http://localhost:8080/oauth2/callback` |
| `FRONTEND_URL` | `http://localhost:5173` |
| `SESSION_SECRET` | Any random string |
| `PORT` | Backend port (default 8080) |

## Features

- Login with any Salesforce org via OAuth 2.0
- Fetch Account validation rules
- Toggle rules on/off
- Enable All / Disable All
- Deploy changes to Salesforce
- Rollback pending changes
