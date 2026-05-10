# SF Validation Manager

Manage Salesforce Account validation rules from a web app — fetch, enable, disable, and deploy changes.

## Stack

- **Frontend**: React + Vite + Tailwind CSS → deployed on **Netlify**
- **Backend**: Java Spring Boot → deployed on **Render**

---

## Local Development

### Backend (Spring Boot)
```bash
cd springboot-backend
mvn spring-boot:run
```
Set these environment variables first:
```
SF_CLIENT_ID=your_consumer_key
SF_CLIENT_SECRET=your_consumer_secret
SF_REDIRECT_URI=http://localhost:8081/oauth2/callback
FRONTEND_URL=http://localhost:5173
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```
Open http://localhost:5173

---

## Deployment

### Step 1 — Deploy Backend on Render

1. Go to [render.com](https://render.com) → New → Web Service
2. Connect your GitHub repo
3. Set:
   - **Root Directory**: `springboot-backend`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/*.jar`
   - **Environment**: Java
4. Add Environment Variables:
   ```
   SF_CLIENT_ID=your_consumer_key
   SF_CLIENT_SECRET=your_consumer_secret
   SF_REDIRECT_URI=https://your-app.onrender.com/oauth2/callback
   FRONTEND_URL=https://your-app.netlify.app
   ```
5. Deploy → copy your Render URL (e.g. `https://sf-manager.onrender.com`)

### Step 2 — Update Salesforce Connected App

In Salesforce Setup → External Client App Manager → your app → Edit:

Change Callback URL to:
```
https://your-app.onrender.com/oauth2/callback
```

### Step 3 — Deploy Frontend on Netlify

1. Go to [netlify.com](https://netlify.com) → New Site → Import from Git
2. Connect your GitHub repo
3. Set:
   - **Base directory**: `frontend`
   - **Build command**: `npm run build`
   - **Publish directory**: `frontend/dist`
4. Update `frontend/netlify.toml` — replace `your-app.onrender.com` with your actual Render URL
5. Deploy → copy your Netlify URL (e.g. `https://sf-manager.netlify.app`)

### Step 4 — Update Render FRONTEND_URL

Go back to Render → your service → Environment → update:
```
FRONTEND_URL=https://sf-manager.netlify.app
```

---

## Environment Variables Reference

| Variable | Local | Production |
|---|---|---|
| `SF_CLIENT_ID` | Consumer Key | same |
| `SF_CLIENT_SECRET` | Consumer Secret | same |
| `SF_REDIRECT_URI` | `http://localhost:8081/oauth2/callback` | `https://your-app.onrender.com/oauth2/callback` |
| `FRONTEND_URL` | `http://localhost:5173` | `https://your-app.netlify.app` |
| `PORT` | 8081 | set by Render automatically |
