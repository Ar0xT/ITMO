import { useAppContext } from "../context/AppContext";
import { translations, Locale } from "../i18n/translations";
import { User, LogOut, Globe, Disc, Settings } from "lucide-react";

export const Sidebar = () => {
  const { user, logout, locale, setLocale, userColorMap } = useAppContext();
  const t = translations[locale];

  const userColor = user ? userColorMap[user] : "#0052CC";

  return (
    <div className="h-full flex flex-col bg-white/70 dark:bg-white/5 backdrop-blur-xl border border-slate-200 dark:border-white/10 rounded-2xl p-4 shadow-xl dark:shadow-2xl transition-colors duration-300">
      <div className="flex items-center gap-3 mb-8 px-2">
        <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-blue-600 to-indigo-600 dark:from-blue-500 dark:to-indigo-500 flex items-center justify-center shadow-lg">
          <Disc className="w-5 h-5 text-white" />
        </div>
        <span className="font-semibold text-lg tracking-tight text-slate-800 dark:text-white">BandManager</span>
      </div>

      <div className="flex items-center gap-3 p-3 rounded-xl bg-slate-100 dark:bg-white/5 border border-slate-200 dark:border-white/5 mb-6 transition-colors duration-300">
        <div 
          className="w-10 h-10 rounded-full flex items-center justify-center shadow-inner"
          style={{ backgroundColor: `${userColor}20`, border: `1px solid ${userColor}40` }}
        >
          <User className="w-5 h-5" style={{ color: userColor }} />
        </div>
        <div className="flex-1 min-w-0">
          <p className="text-xs text-slate-500 dark:text-white/50">{t.welcome}</p>
          <p className="text-sm font-medium text-slate-800 dark:text-white truncate">{user}</p>
        </div>
      </div>

      <nav className="flex-1 space-y-2">
        <NavItem icon={<Disc className="w-4 h-4" />} label={t.dashboard} active />
        <NavItem icon={<Settings className="w-4 h-4" />} label="Settings" />
      </nav>

      <div className="mt-auto space-y-4 pt-4 border-t border-slate-200 dark:border-white/10 transition-colors duration-300">
        <div className="space-y-2">
          <div className="flex items-center gap-2 px-2 text-xs font-medium text-slate-500 dark:text-white/40 uppercase tracking-wider mb-2">
            <Globe className="w-3 h-3" />
            {t.language}
          </div>
          <div className="grid grid-cols-5 gap-1">
            {(["en", "ru", "tr", "hr", "es-EC"] as Locale[]).map((lang) => (
              <button
                key={lang}
                onClick={() => setLocale(lang)}
                className={`py-1.5 text-xs rounded-md transition-all ${
                  locale === lang 
                    ? "bg-blue-100 text-blue-700 dark:bg-blue-500/20 dark:text-blue-400 font-medium" 
                    : "text-slate-500 hover:bg-slate-100 hover:text-slate-800 dark:text-white/40 dark:hover:bg-white/5 dark:hover:text-white"
                }`}
              >
                {lang.split("-")[0].toUpperCase()}
              </button>
            ))}
          </div>
        </div>

        <button
          onClick={logout}
          className="flex items-center gap-2 w-full p-2 text-sm text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-400/10 rounded-lg transition-colors"
        >
          <LogOut className="w-4 h-4" />
          {t.logout}
        </button>
      </div>
    </div>
  );
};

const NavItem = ({ icon, label, active = false }: { icon: React.ReactNode, label: string, active?: boolean }) => {
  return (
    <button className={`flex items-center gap-3 w-full p-2.5 rounded-lg text-sm transition-all ${
      active 
        ? "bg-slate-200 text-slate-900 dark:bg-white/10 dark:text-white shadow-sm" 
        : "text-slate-600 hover:bg-slate-100 hover:text-slate-900 dark:text-white/60 dark:hover:bg-white/5 dark:hover:text-white"
    }`}>
      {icon}
      {label}
    </button>
  );
}
