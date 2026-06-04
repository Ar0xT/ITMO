import { useState } from "react";
import { useAppContext } from "../context/AppContext";
import { translations } from "../i18n/translations";
import { Plus } from "lucide-react";
import { CRUDModal } from "./CRUDModal";
import { ThemeToggle } from "./ThemeToggle";

export const TopBar = () => {
  const { locale } = useAppContext();
  const t = translations[locale];
  const [isAddOpen, setIsAddOpen] = useState(false);

  return (
    <>
      <div className="h-14 border-b border-slate-200 dark:border-white/10 flex items-center justify-between px-4 bg-white/50 dark:bg-white/5 shrink-0 transition-colors duration-300 backdrop-blur-md">
        <div className="flex items-center gap-4">
          <div className="px-3 py-1 bg-green-100 dark:bg-green-500/20 text-green-700 dark:text-green-400 text-xs rounded-full border border-green-200 dark:border-green-500/30 flex items-center gap-1.5 font-medium transition-colors duration-300">
            <span className="w-1.5 h-1.5 bg-green-500 rounded-full animate-pulse" />
            {t.syncing}
          </div>
        </div>

        <div className="flex items-center gap-4">
          <ThemeToggle />
          <button 
            onClick={() => setIsAddOpen(true)}
            className="flex items-center gap-1.5 px-3 py-1.5 bg-blue-600 hover:bg-blue-700 dark:bg-blue-600 dark:hover:bg-blue-500 text-white text-sm rounded-lg transition-colors shadow-lg shadow-blue-500/20 dark:shadow-blue-500/20"
          >
            <Plus className="w-4 h-4" />
            {t.add}
          </button>
        </div>
      </div>

      <CRUDModal isOpen={isAddOpen} onClose={() => setIsAddOpen(false)} />
    </>
  );
};
