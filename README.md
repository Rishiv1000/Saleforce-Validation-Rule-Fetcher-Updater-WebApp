# SF Validation Manager

> Manage Salesforce Account validation rules — fetch, enable, disable, and deploy changes directly from the browser.

---

## Tech Stack

| Frontend | React + Vite + Tailwind CSS | Vercel |
| Backend | Java Spring Boot | Render (Docker) |

---


## Local Setup

### 1. Clone the repo

### 2. Create a Salesforce Connected App

> Do this first — the backend needs these credentials.

1. In Salesforce: **Setup → External Client App Manager → New**
2. Set the **Callback URL** to:
   ```
   http://localhost:8081/oauth2/callback
   ```
3. Enable OAuth Scopes: `api`, `web`, `refresh_token`
4. Save, then copy your **Consumer Key** and **Consumer Secret**

---

### 3. Backend

```bash
cd springboot-backend
cp .env.example .env
```

Fill in `.env` with your Salesforce credentials:

```env
SF_CLIENT_ID=your_consumer_key
SF_CLIENT_SECRET=your_consumer_secret
SF_REDIRECT_URI=http://localhost:8081/oauth2/callback
FRONTEND_URL=http://localhost:5173
```

Start the server:

```bash
mvn spring-boot:run
```

Runs on **http://localhost:8081**

---

### 4. Frontend

```bash
cd frontend
cp .env.example .env
```

The default `.env` already points to your local backend — no changes needed:

```env
VITE_API_URL=http://localhost:8081
```

Install and start:

```bash
npm install
npm run dev
```

Runs on **http://localhost:5173**

---


---

## License

MIT
