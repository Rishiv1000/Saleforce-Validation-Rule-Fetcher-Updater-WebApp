/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        sf: {
          blue:    '#00a1e0',
          dark:    '#032d60',
          light:   '#e8f4fb',
          success: '#22c55e',
          danger:  '#ef4444',
          warn:    '#f59e0b'
        }
      }
    }
  },
  plugins: []
};
