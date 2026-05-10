# SF Validation Manager

Manage Salesforce Account validation rules — fetch, enable, disable, and deploy changes directly from the browser.

## Stack

| Part | Technology | Deployed On |
|---|---|---|
| Frontend | React + Vite + Tailwind | Vercel |
| Backend | Java Spring Boot | Render (Docker) |

---

## Local Development

### Backend
```bash
cd springboot-backend
cp .env.example .env
# Fill in your SF_CLIENT_ID and SF_CLIENT_SECRET
mvn spring-boot:run
```
Runs on http://localhost:8081

### Frontend
```bash
cd frontend
npm install
npm run dev
```
Runs on http://localhost:5173

---

## Deployment

### Step 1 — Deploy Backend on Render

1. Push code to GitHub
2. Go to [render.com](https://render.com) → New → Web Service
3. Connect your GitHub repo
4. Set:
   - **Language**: Docker
   - **Root Directory**: `springboot-backend`
5. Add Environment Variables:
   ```
   SF_CLIENT_ID=your_consumer_key
   SF_CLIENT_SECRET=your_consumer_secret
   SF_REDIRECT_URI=https://your-app.onrender.com/oauth2/callback
   FRONTEND_URL=https://your-app.vercel.app
   ```
6. Click Deploy
7. Copy your Render URL e.g. `https://sf-manager.onrender.com`

### Step 2 — Update Salesforce Connected App

Setup → External Client App Manager → your app → Edit Callback URL:
```
https://your-app.onrender.com/oauth2/callback
```

### Step 3 — Deploy Frontend on Vercel

1. Go to [vercel.com](https://vercel.com) → New Project → Import from GitHub
2. Set:
   - **Root Directory**: `frontend`
   - **Framework**: Vite
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
3. Before deploying — update `frontend/vercel.json`:
   Replace `your-app.onrender.com` with your actual Render URL
4. Click Deploy
5. Copy your Vercel URL e.g. `https://sf-manager.vercel.app`

### Step 4 — Update Render FRONTEND_URL

Render → your service → Environment → update:
```
FRONTEND_URL=https://sf-manager.vercel.app
```
Then redeploy.

---

## Environment Variables

### Backend (`springboot-backend/.env`)

| Variable | Local | Production |
|---|---|---|
| `SF_CLIENT_ID` | your Consumer Key | same |
| `SF_CLIENT_SECRET` | your Consumer Secret | same |
| `SF_REDIRECT_URI` | `http://localhost:8081/oauth2/callback` | `https://your-app.onrender.com/oauth2/callback` |
| `FRONTEND_URL` | `http://localhost:5173` | `https://your-app.vercel.app` |

---

## For Seniors / Reviewers

```bash
git clone <repo-url>

# Backend
cd springboot-backend
cp .env.example .env
# Add your SF_CLIENT_ID and SF_CLIENT_SECRET in .env
mvn spring-boot:run

# Frontend (new terminal)
cd frontend
npm install
npm run dev
```

Open http://localhost:5173 and login with your Salesforce org.
