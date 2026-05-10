import { useEffect, useState } from 'react';
import axios from 'axios';
import Spinner    from '../components/Spinner';
import ErrorAlert from '../components/ErrorAlert';
import { LogOut, RefreshCw, RotateCcw, Rocket } from 'lucide-react';

function Toggle({ checked, onChange }) {
  return (
    <label className="relative inline-flex cursor-pointer items-center select-none">
      <input type="checkbox" className="sr-only" checked={checked} onChange={onChange} />
      <div className={`relative h-7 w-14 rounded-full transition-colors duration-200 ${checked ? 'bg-blue-500' : 'bg-gray-300'}`}>
        <div className={`absolute top-1 h-5 w-5 rounded-full bg-white shadow transition-transform duration-200 ${checked ? 'translate-x-7' : 'translate-x-1'}`} />
        <span className={`absolute top-1 text-xs font-bold text-white ${checked ? 'left-2' : 'right-1.5'}`}>
          {checked ? 'ON' : 'OFF'}
        </span>
      </div>
    </label>
  );
}

export default function ValidationRules({ user, logout }) {
  const [original,  setOriginal]  = useState([]);
  const [pending,   setPending]   = useState([]);
  const [loading,   setLoading]   = useState(true);
  const [deploying, setDeploying] = useState(false);
  const [error,     setError]     = useState('');
  const [success,   setSuccess]   = useState('');

  const load = () => {
    setLoading(true);
    setError('');
    setSuccess('');
    axios.get('/api/validation-rules')
      .then(res => {
        setOriginal(res.data);
        setPending(res.data.map(r => ({ ...r })));
      })
      .catch(err => setError(err.response?.data?.error || err.message))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const toggle     = id => setPending(p => p.map(r => r.Id === id ? { ...r, Active: !r.Active } : r));
  const enableAll  = () => setPending(p => p.map(r => ({ ...r, Active: true  })));
  const disableAll = () => setPending(p => p.map(r => ({ ...r, Active: false })));
  const rollback   = () => { setPending(original.map(r => ({ ...r }))); setError(''); setSuccess(''); };

  const changed = pending.filter(pr => {
    const orig = original.find(r => r.Id === pr.Id);
    return orig && orig.Active !== pr.Active;
  });

  const deploy = async () => {
    if (changed.length === 0) return setError('No changes to deploy.');
    setDeploying(true); setError(''); setSuccess('');
    try {
      await axios.post('/api/validation-rules/bulk-toggle', {
        rules: changed.map(r => ({ id: r.Id, active: r.Active }))
      });
      setOriginal(pending.map(r => ({ ...r })));
      setSuccess(`Deployed ${changed.length} change(s) to Salesforce.`);
    } catch (err) {
      setError(err.response?.data?.error || err.message);
    } finally {
      setDeploying(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-[#032d60] text-white px-6 py-4 flex items-center justify-between shadow">
        <div className="flex items-center gap-3">
          <div className="h-8 w-8 rounded-lg bg-[#00a1e0] flex items-center justify-center font-bold text-sm">SF</div>
          <span className="font-bold text-lg">SF Validation Manager</span>
        </div>
        <div className="flex items-center gap-4">
          <span className="text-sm text-white/70">{user?.username}</span>
          <button onClick={logout} className="flex items-center gap-1.5 text-sm text-white/70 hover:text-white transition-colors">
            <LogOut className="h-4 w-4" /> Logout
          </button>
        </div>
      </header>

      <div className="max-w-4xl mx-auto px-6 py-8">
        <div className="mb-6 rounded-lg bg-blue-50 border border-blue-200 px-5 py-4 text-sm text-blue-800">
          Toggle rules on/off, then click <strong>Deploy Changes</strong> to apply to Salesforce.
          Use <strong>Rollback</strong> to undo before deploying.
        </div>

        {error   && <div className="mb-4"><ErrorAlert message={error} onClose={() => setError('')} /></div>}
        {success && <div className="mb-4 rounded-lg border border-green-200 bg-green-50 px-5 py-3 text-sm text-green-700">{success}</div>}

        {loading ? (
          <div className="flex justify-center py-24"><Spinner size="lg" /></div>
        ) : (
          <div className="rounded-xl border border-gray-200 bg-white shadow-sm overflow-hidden">
            <div className="flex items-center justify-between px-6 py-4 bg-gray-50 border-b border-gray-200">
              <span className="font-bold text-gray-800">Account — Validation Rules</span>
              <div className="flex items-center gap-2">
                <button onClick={load} className="flex items-center gap-1.5 rounded-lg border border-gray-300 px-3 py-1.5 text-xs font-medium text-gray-600 hover:bg-gray-100">
                  <RefreshCw className="h-3.5 w-3.5" /> Refresh
                </button>
                <button onClick={enableAll}  className="rounded-lg bg-green-500 px-3 py-1.5 text-xs font-bold text-white hover:bg-green-600">ENABLE ALL</button>
                <button onClick={disableAll} className="rounded-lg bg-red-500 px-3 py-1.5 text-xs font-bold text-white hover:bg-red-600">DISABLE ALL</button>
              </div>
            </div>

            {pending.length === 0 ? (
              <div className="py-16 text-center text-gray-400">No validation rules found on Account object.</div>
            ) : (
              <table className="w-full text-sm">
                <tbody className="divide-y divide-gray-100">
                  {pending.map(rule => {
                    const orig    = original.find(r => r.Id === rule.Id);
                    const isDirty = orig && orig.Active !== rule.Active;
                    return (
                      <tr key={rule.Id} className={isDirty ? 'bg-amber-50' : 'hover:bg-gray-50'}>
                        <td className="px-6 py-4">
                          <p className="font-medium text-gray-900">{rule.ValidationName}</p>
                          {rule.Description && <p className="text-xs text-gray-400 mt-0.5">{rule.Description}</p>}
                        </td>
                        <td className="px-4 py-4 text-xs text-gray-400 italic max-w-xs truncate hidden sm:table-cell">
                          {rule.ErrorMessage || ''}
                        </td>
                        <td className="px-6 py-4 text-right whitespace-nowrap">
                          {isDirty && (
                            <span className="mr-3 inline-flex rounded-full bg-amber-100 px-2 py-0.5 text-xs font-semibold text-amber-700">
                              modified
                            </span>
                          )}
                          <Toggle checked={rule.Active} onChange={() => toggle(rule.Id)} />
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            )}

            <div className="flex items-center justify-between px-6 py-4 bg-gray-50 border-t border-gray-200">
              <button onClick={rollback}
                className="flex items-center gap-2 rounded-lg border border-amber-300 bg-amber-50 px-4 py-2 text-sm font-semibold text-amber-700 hover:bg-amber-100">
                <RotateCcw className="h-4 w-4" /> Rollback to Original
              </button>
              <button onClick={deploy} disabled={deploying || changed.length === 0}
                className={`flex items-center gap-2 rounded-lg px-5 py-2 text-sm font-semibold text-white
                  ${changed.length > 0 ? 'bg-[#00a1e0] hover:bg-blue-600' : 'bg-gray-300 cursor-not-allowed'}`}>
                {deploying ? <Spinner size="sm" /> : <Rocket className="h-4 w-4" />}
                {deploying ? 'Deploying...' : `Deploy Changes${changed.length > 0 ? ` (${changed.length})` : ''}`}
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
