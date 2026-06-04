import { useState, useEffect } from "react";
import { useAppContext, MusicBand, MusicGenre } from "../context/AppContext";
import { translations } from "../i18n/translations";
import { motion, AnimatePresence } from "motion/react";
import { X } from "lucide-react";

interface CRUDModalProps {
  isOpen: boolean;
  onClose: () => void;
  item?: MusicBand;
}

export const CRUDModal = ({ isOpen, onClose, item }: CRUDModalProps) => {
  const { addItem, updateItem, locale } = useAppContext();
  const t = translations[locale];
  
  const [formData, setFormData] = useState({
    name: "",
    x: 0,
    y: 0,
    numberOfParticipants: 1,
    singlesCount: 1,
    establishmentDate: "",
    genre: "RAP" as MusicGenre,
    studioName: "",
  });

  useEffect(() => {
    if (item && isOpen) {
      setFormData({
        name: item.name,
        x: item.x,
        y: item.y,
        numberOfParticipants: item.numberOfParticipants,
        singlesCount: item.singlesCount,
        establishmentDate: item.establishmentDate || "",
        genre: item.genre || "RAP",
        studioName: item.studioName || "",
      });
    } else if (!item && isOpen) {
      setFormData({
        name: "",
        x: Math.floor(Math.random() * 200) - 50,
        y: Math.floor(Math.random() * 200) - 50,
        numberOfParticipants: 4,
        singlesCount: 1,
        establishmentDate: new Date().toISOString().split("T")[0],
        genre: "POST_ROCK",
        studioName: "",
      });
    }
  }, [item, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const data = {
      ...formData,
      establishmentDate: formData.establishmentDate || null,
      studioName: formData.studioName || null,
    };
    if (item) {
      updateItem(item.id, data);
    } else {
      addItem(data);
    }
    onClose();
  };

  return (
    <AnimatePresence>
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 dark:bg-black/60 backdrop-blur-sm p-4 transition-colors duration-300">
        <motion.div
          initial={{ opacity: 0, scale: 0.95, y: 10 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.95, y: 10 }}
          className="w-full max-w-md bg-white dark:bg-[#1a1b1e] border border-slate-200 dark:border-white/10 shadow-2xl rounded-2xl overflow-hidden"
        >
          <div className="flex items-center justify-between p-4 border-b border-slate-200 dark:border-white/10 bg-slate-50 dark:bg-white/5 transition-colors duration-300">
            <h3 className="font-semibold text-slate-800 dark:text-white">{item ? t.edit : t.add}</h3>
            <button onClick={onClose} className="p-1 hover:bg-slate-200 dark:hover:bg-white/10 rounded-lg transition-colors text-slate-500 dark:text-white/70">
              <X className="w-4 h-4" />
            </button>
          </div>

          <form onSubmit={handleSubmit} className="p-4 space-y-4 max-h-[70vh] overflow-y-auto custom-scrollbar">
            <div className="space-y-1.5">
              <label className="text-xs font-medium text-slate-600 dark:text-white/60">{t.name}</label>
              <input
                required
                type="text"
                value={formData.name}
                onChange={e => setFormData({ ...formData, name: e.target.value })}
                className="w-full bg-slate-50 dark:bg-black/20 border border-slate-200 dark:border-white/10 rounded-lg px-3 py-2 text-sm text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-all shadow-sm"
              />
            </div>
            
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <label className="text-xs font-medium text-slate-600 dark:text-white/60">{t.x} {`>`} -97</label>
                <input
                  required
                  type="number"
                  min="-96"
                  value={formData.x}
                  onChange={e => setFormData({ ...formData, x: Number(e.target.value) })}
                  className="w-full bg-slate-50 dark:bg-black/20 border border-slate-200 dark:border-white/10 rounded-lg px-3 py-2 text-sm text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-all shadow-sm"
                />
              </div>
              <div className="space-y-1.5">
                <label className="text-xs font-medium text-slate-600 dark:text-white/60">{t.y} {`>`} -480</label>
                <input
                  required
                  type="number"
                  step="0.1"
                  min="-479.9"
                  value={formData.y}
                  onChange={e => setFormData({ ...formData, y: Number(e.target.value) })}
                  className="w-full bg-slate-50 dark:bg-black/20 border border-slate-200 dark:border-white/10 rounded-lg px-3 py-2 text-sm text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-all shadow-sm"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <label className="text-xs font-medium text-slate-600 dark:text-white/60">{t.numberOfParticipants}</label>
                <input
                  required
                  type="number"
                  min="1"
                  value={formData.numberOfParticipants}
                  onChange={e => setFormData({ ...formData, numberOfParticipants: Number(e.target.value) })}
                  className="w-full bg-slate-50 dark:bg-black/20 border border-slate-200 dark:border-white/10 rounded-lg px-3 py-2 text-sm text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-all shadow-sm"
                />
              </div>
              <div className="space-y-1.5">
                <label className="text-xs font-medium text-slate-600 dark:text-white/60">{t.singlesCount}</label>
                <input
                  required
                  type="number"
                  min="1"
                  value={formData.singlesCount}
                  onChange={e => setFormData({ ...formData, singlesCount: Number(e.target.value) })}
                  className="w-full bg-slate-50 dark:bg-black/20 border border-slate-200 dark:border-white/10 rounded-lg px-3 py-2 text-sm text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-all shadow-sm"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <label className="text-xs font-medium text-slate-600 dark:text-white/60">{t.establishmentDate}</label>
                <input
                  type="date"
                  value={formData.establishmentDate}
                  onChange={e => setFormData({ ...formData, establishmentDate: e.target.value })}
                  className="w-full bg-slate-50 dark:bg-black/20 border border-slate-200 dark:border-white/10 rounded-lg px-3 py-2 text-sm text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-all shadow-sm [color-scheme:light] dark:[color-scheme:dark]"
                />
              </div>
              <div className="space-y-1.5">
                <label className="text-xs font-medium text-slate-600 dark:text-white/60">{t.genre}</label>
                <select
                  value={formData.genre}
                  onChange={e => setFormData({ ...formData, genre: e.target.value as MusicGenre })}
                  className="w-full bg-slate-50 dark:bg-black/20 border border-slate-200 dark:border-white/10 rounded-lg px-3 py-2 text-sm text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-all shadow-sm"
                >
                  <option value="RAP">RAP</option>
                  <option value="HIP_HOP">HIP_HOP</option>
                  <option value="POST_ROCK">POST_ROCK</option>
                  <option value="BRIT_POP">BRIT_POP</option>
                </select>
              </div>
            </div>

            <div className="space-y-1.5">
              <label className="text-xs font-medium text-slate-600 dark:text-white/60">{t.studio}</label>
              <input
                type="text"
                value={formData.studioName}
                onChange={e => setFormData({ ...formData, studioName: e.target.value })}
                className="w-full bg-slate-50 dark:bg-black/20 border border-slate-200 dark:border-white/10 rounded-lg px-3 py-2 text-sm text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-all shadow-sm"
              />
            </div>

            <div className="pt-4 flex justify-end gap-2 border-t border-slate-200 dark:border-white/10 mt-4">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2 text-sm text-slate-600 hover:bg-slate-100 dark:text-white/70 dark:hover:text-white dark:hover:bg-white/5 rounded-lg transition-colors"
              >
                {t.cancel}
              </button>
              <button
                type="submit"
                className="px-4 py-2 text-sm bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600 text-white rounded-lg transition-colors shadow-lg shadow-blue-500/20"
              >
                {t.save}
              </button>
            </div>
          </form>
        </motion.div>
      </div>
    </AnimatePresence>
  );
};
