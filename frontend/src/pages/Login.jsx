import { useState } from 'react';

export default function Login() {
  const [env, setEnv] = useState('production');
  const oauthError    = new URLSearchParams(window.location.search).get('error');

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-[#032d60] to-[#00a1e0] p-4">
      <div className="w-full max-w-md rounded-2xl bg-white p-10 shadow-2xl">

        <div className="mb-8 text-center">
          <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-[#032d60]">
            <svg width="32" height="32" viewBox="0 0 48 48" fill="none">
              <ellipse cx="24" cy="20" rx="18" ry="12" fill="#00a1e0"/>
              <ellipse cx="24" cy="20" rx="14" ry="9"  fill="#032d60"/>
              <rect x="18" y="28" width="12" height="14" rx="2" fill="#00a1e0"/>
            </svg>
          </div>
          <h1 className="text-2xl font-bold text-gray-900">SF Validation Manager</h1>
          <p className="mt-1 text-sm text-gray-500">Login with your Salesforce org</p>
        </div>

        {oauthError && (
          <div className="mb-5 rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">
            {decodeURIComponent(oauthError)}
          </div>
        )}

        <div className="mb-5">
          <label className="mb-2 block text-sm font-semibold text-gray-700">Select Org Type</label>
          <div className="grid grid-cols-2 gap-3">
            {[
              { value: 'production', label: 'Production / Developer', sub: 'login.salesforce.com' },
              { value: 'sandbox',    label: 'Sandbox',                sub: 'test.salesforce.com'  }
            ].map(opt => (
              <button key={opt.value} onClick={() => setEnv(opt.value)}
                className={`rounded-xl border-2 p-3 text-left transition-all
                  ${env === opt.value ? 'border-[#00a1e0] bg-blue-50' : 'border-gray-200 hover:border-gray-300'}`}>
                <p className={`text-sm font-semibold ${env === opt.value ? 'text-[#00a1e0]' : 'text-gray-700'}`}>
                  {opt.label}
                </p>
                <p className="text-xs text-gray-400 mt-0.5">{opt.sub}</p>
              </button>
            ))}
          </div>
        </div>

        <a href={`${import.meta.env.VITE_API_URL}/auth/login?env=${env}`}
          className="flex w-full items-center justify-center gap-3 rounded-xl bg-[#00a1e0] px-6 py-3.5 text-base font-semibold text-white shadow transition hover:bg-blue-600 active:scale-95">
          <svg width="20" height="20" viewBox="0 0 48 48" fill="none">
            <ellipse cx="24" cy="20" rx="18" ry="12" fill="white" fillOpacity="0.9"/>
            <ellipse cx="24" cy="20" rx="14" ry="9"  fill="#00a1e0"/>
            <rect x="18" y="28" width="12" height="14" rx="2" fill="white" fillOpacity="0.9"/>
          </svg>
          Login with Salesforce
        </a>

        <p className="mt-5 text-center text-xs text-gray-400">
          Any org's credentials will work — Developer, Sandbox, or Production.
        </p>
      </div>
    </div>
  );
}
