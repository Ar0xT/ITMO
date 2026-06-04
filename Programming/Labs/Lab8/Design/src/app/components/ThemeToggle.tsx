import { useTheme } from "next-themes";
import { useEffect, useState } from "react";
import { Moon, Sun, Monitor } from "lucide-react";
import { useAppContext } from "../context/AppContext";
import { translations } from "../i18n/translations";

export const ThemeToggle = () => {
  const [mounted, setMounted] = useState(false);
  const { theme, setTheme } = useTheme();
  const { locale } = useAppContext();
  const t = translations[locale];

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return <div className="w-[104px] h-9" />; // Placeholder
  }

  return (
    <div className="flex bg-slate-200/50 dark:bg-black/30 p-1 rounded-xl border border-slate-300/50 dark:border-white/10 backdrop-blur-md">
      <button
        onClick={() => setTheme("light")}
        className={`p-1.5 rounded-lg transition-all ${
          theme === "light" 
            ? "bg-white text-blue-600 shadow-sm" 
            : "text-slate-500 hover:text-slate-800"
        }`}
        title={t.light}
      >
        <Sun className="w-4 h-4" />
      </button>
      <button
        onClick={() => setTheme("system")}
        className={`p-1.5 rounded-lg transition-all ${
          theme === "system" 
            ? "bg-white dark:bg-white/10 text-slate-800 dark:text-white shadow-sm" 
            : "text-slate-500 dark:text-white/40 hover:text-slate-800 dark:hover:text-white"
        }`}
        title={t.system}
      >
        <Monitor className="w-4 h-4" />
      </button>
      <button
        onClick={() => setTheme("dark")}
        className={`p-1.5 rounded-lg transition-all ${
          theme === "dark" 
            ? "bg-white/10 text-blue-400 shadow-sm" 
            : "text-slate-500 dark:text-white/40 hover:text-slate-800 dark:hover:text-white"
        }`}
        title={t.dark}
      >
        <Moon className="w-4 h-4" />
      </button>
    </div>
  );
};
