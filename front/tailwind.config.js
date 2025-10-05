/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/**/*.{js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        border: 'hsl(var(--border) / <alpha-value>)',
        ring: 'hsl(var(--ring) / <alpha-value>)',
        input: 'hsl(var(--input) / <alpha-value>)',
        background: 'hsl(var(--background) / <alpha-value>)',
        foreground: 'hsl(var(--foreground) / <alpha-value>)',
        primary: 'hsl(var(--primary) / <alpha-value>)',
        secondary: 'hsl(var(--secondary) / <alpha-value>)',
        destructive: 'hsl(var(--destructive) / <alpha-value>)',
        muted: 'hsl(var(--muted) / <alpha-value>)',
        accent: 'hsl(var(--accent) / <alpha-value>)',
      },
      borderRadius: {
        sm: '0.625rem',
        md: '0.625rem',
        lg: '0.625rem',
        xl: '0.625rem',
        DEFAULT: '0.625rem', // optional: sets the default radius for `rounded`
      },
    },
  },
  plugins: [],
}

