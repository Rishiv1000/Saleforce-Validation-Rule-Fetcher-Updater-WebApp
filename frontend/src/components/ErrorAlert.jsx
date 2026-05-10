import { AlertCircle, X } from 'lucide-react';

export default function ErrorAlert({ message, onClose }) {
  if (!message) return null;
  return (
    <div className="flex items-start gap-3 rounded-lg border border-red-200 bg-red-50 p-4 text-red-700">
      <AlertCircle className="mt-0.5 h-5 w-5 shrink-0" />
      <span className="flex-1 text-sm">{message}</span>
      {onClose && (
        <button onClick={onClose} className="shrink-0 hover:text-red-900">
          <X className="h-4 w-4" />
        </button>
      )}
    </div>
  );
}
