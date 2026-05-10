import { useState, useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import axios from 'axios';
import Login           from './pages/Login';
import ValidationRules from './pages/ValidationRules';
import Spinner         from './components/Spinner';

axios.defaults.withCredentials = true;

export default function App() {
  const [user,    setUser]    = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axios.get('/auth/user')
      .then(res => { if (res.data.authenticated) setUser(res.data); })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const logout = async () => {
    await axios.post('/auth/logout');
    setUser(null);
  };

  if (loading) {
    return (
      <div className="flex h-screen items-center justify-center">
        <Spinner size="lg" />
      </div>
    );
  }

  return (
    <Routes>
      <Route path="/login" element={
        user ? <Navigate to="/" replace /> : <Login />
      } />
      <Route path="/" element={
        user
          ? <ValidationRules user={user} logout={logout} />
          : <Navigate to="/login" replace />
      } />
      <Route path="*" element={<Navigate to={user ? '/' : '/login'} replace />} />
    </Routes>
  );
}
