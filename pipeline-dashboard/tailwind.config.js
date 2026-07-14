/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,ts}"],
  theme: {
    extend: {
      colors: {
        void: "#0F1417",
        panel: "#171D21",
        "panel-raised": "#1D2529",
        rail: "#2A3237",
        ink: "#E8E6E1",
        "ink-dim": "#8B959B",
        "ink-faint": "#5A6469",
        copper: "#C97B4A",
        amber: "#E8A33D",
        signal: "#4FAE7A",
        danger: "#E5556B",
      },
      fontFamily: {
        display: ["'Space Grotesk'", "sans-serif"],
        body: ["'Inter'", "sans-serif"],
        mono: ["'JetBrains Mono'", "monospace"],
      },
      keyframes: {
        flow: {
          "0%": { backgroundPosition: "0 0" },
          "100%": { backgroundPosition: "24px 0" },
        },
        "pulse-ring": {
          "0%": { boxShadow: "0 0 0 0 rgba(232,163,61,0.45)" },
          "100%": { boxShadow: "0 0 0 8px rgba(232,163,61,0)" },
        },
      },
      animation: {
        flow: "flow 0.9s linear infinite",
        "pulse-ring": "pulse-ring 1.4s cubic-bezier(0.4,0,0.6,1) infinite",
      },
    },
  },
  plugins: [],
};
