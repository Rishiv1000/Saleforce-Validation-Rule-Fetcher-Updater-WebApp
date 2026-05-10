# SF Validation Manager

Manage Salesforce Account validation rules — fetch, enable, disable, and deploy changes directly from the browser.

## Stack

| Part | Technology | Deployed On |
|---|---|---|
| Frontend | React + Vite + Tailwind | Vercel |
| Backend | Java Spring Boot | Render (Docker) |

---

## Local Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- Node.js 18+
- Salesforce Developer Org + Connected App

### 1. Clone the repo
```bash
git clone <repo-url>
```

### 2. Backend Setup
```bash
cd springboot-backend
cp .env.example .env
```

Edit `.env` and fill in your values:
```
SF_CLIENT_ID=your_consumer_key
SF_CLIENT_SECRET=your_consumer_secret
SF_REDIRECT_URI=http://localhost:8081/oauth2/callback
FRONTEND_URL=http://localhost:5173
```

Start backend:
```bash
mvn spring-boot:run
```
Runs on http://localhost:8081

### 3. Frontend Setup
```bash
cd frontend
cp .env.example .env
```

`.env` already has correct local value:
```
VITE_API_URL=http://localhost:8081
```

Start frontend:
```bash
npm install
npm run dev
```
Runs on http://localhost:5173

### 4. Salesforce Connected App

1. Setup → External Client App Manager → New
2. Callback URL: `http://localhost:8081/oauth2/callback`
3. Scopes: `api`, `web`, `refresh_token`
4. Copy Consumer Key → `SF_CLIENT_ID`
5. Copy Consumer Secret → `SF_CLIENT_SECRET`

---

## Deployment

### Backend → Render

1. Go to [render.com](https://render.com) → New Web Service
2. Connect GitHub repo
3. Settings:
   - **Language**: Docker
   - **Root Directory**: `springboot-backend`
4. Environment Variables:
   ```
   SF_CLIENT_ID=your_consumer_key
   SF_CLIENT_SECRET=your_consumer_secret
   SF_REDIRECT_URI=https://your-app.onrender.com/oauth2/callback
   FRONTEND_URL=https://your-app.vercel.app
   ```
5. Deploy → copy Render URL

### Frontend → Vercel

1. Go to [vercel.com](https://vercel.com) → New Project
2. Connect GitHub repo
3. Settings:
   - **Root Directory**: `frontend`
   - **Framework**: Vite
4. Environment Variables:
   ```
   VITE_API_URL=https://your-app.onrender.com
   ```
5. Deploy

### Update Salesforce Callback URL

Setup → External Client App Manager → Edit:
```
https://your-app.onrender.com/oauth2/callback
```

---

## Project Structure

```
frontend/               React app (Vercel)
  src/
    App.jsx
    pages/
      Login.jsx
      ValidationRules.jsx
    components/
      Spinner.jsx
      ErrorAlert.jsx
  .env.example
  vercel.json

springboot-backend/     Spring Boot API (Render)
  src/main/java/
    controller/
    service/
    model/
    config/
  Dockerfile
  .env.example
```
