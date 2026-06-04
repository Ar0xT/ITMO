import { useState } from "react";
import { useNavigate } from "react-router";
import { useAppContext } from "../context/AppContext";
import { translations } from "../i18n/translations";
import { motion } from "motion/react";
import { Disc } from "lucide-react";
import { ThemeToggle } from "./ThemeToggle";

export const Login = () => {
  const { login, locale, setLocale } = useAppContext();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const t = translations[locale];

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (username.trim()) {
      login(username.trim());
      navigate("/dashboard");
    }
  };

  return (
    <div className="flex-1 flex items-center justify-center p-6 relative">
      <div className="absolute top-6 right-6">
        <ThemeToggle />
      </div>

      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, ease: "easeOut" }}
        className="w-full max-w-md bg-white/70 dark:bg-black/40 backdrop-blur-3xl border border-slate-200/50 dark:border-white/10 rounded-3xl p-8 shadow-2xl dark:shadow-2xl dark:shadow-black/50"
      >
        <div className="flex justify-center mb-8">
          <div className="w-16 h-16 bg-blue-500/10 dark:bg-blue-500/20 rounded-2xl flex items-center justify-center border border-blue-500/20 dark:border-blue-500/30 shadow-inner">
            <Disc className="w-8 h-8 text-blue-600 dark:text-blue-400" />
          </div>
        </div>
        
        <h1 className="text-3xl font-semibold text-center mb-8 tracking-tight text-slate-800 dark:text-white/90">
          {t.enter}
        </h1>

        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="space-y-2">
            <label className="text-sm font-medium text-slate-600 dark:text-white/60 ml-1">
              {t.username}
            </label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full bg-white dark:bg-black/20 border border-slate-200 dark:border-white/10 rounded-xl px-4 py-3 text-slate-900 dark:text-white placeholder-slate-400 dark:placeholder-white/30 focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-all shadow-sm"
              placeholder="e.g. admin"
              required
            />
          </div>

          <div className="space-y-2">
            <label className="text-sm font-medium text-slate-600 dark:text-white/60 ml-1">
              {t.password}
            </label>
            <input
              type="password"
              className="w-full bg-white dark:bg-black/20 border border-slate-200 dark:border-white/10 rounded-xl px-4 py-3 text-slate-900 dark:text-white placeholder-slate-400 dark:placeholder-white/30 focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-all shadow-sm"
              placeholder="••••••••"
            />
          </div>

          <button
            type="submit"
            className="w-full bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600 text-white font-medium rounded-xl px-4 py-3 shadow-lg shadow-blue-500/25 transition-all active:scale-[0.98]"
          >
            {t.login}
          </button>
        </form>

        <div className="mt-8 flex justify-center gap-3">
          {(["en", "ru", "tr", "hr", "es-EC"] as const).map((lang) => (
            <button
              key={lang}
              onClick={() => setLocale(lang)}
              className={`text-xs px-2 py-1 rounded-md transition-colors ${
                locale === lang 
                  ? "bg-slate-200 text-slate-800 dark:bg-white/20 dark:text-white font-medium" 
                  : "text-slate-500 hover:text-slate-800 hover:bg-slate-100 dark:text-white/40 dark:hover:text-white dark:hover:bg-white/10"
              }`}
            >
              {lang.toUpperCase()}
            </button>
          ))}
        </div>
      </motion.div>
    </div>
  );
};
