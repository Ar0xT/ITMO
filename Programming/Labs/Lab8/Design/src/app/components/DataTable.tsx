import { useState, useMemo } from "react";
import { useAppContext, MusicBand } from "../context/AppContext";
import { translations } from "../i18n/translations";
import { Search, ChevronUp, ChevronDown, Edit2, Trash2 } from "lucide-react";
import { CRUDModal } from "./CRUDModal";

type SortField = keyof MusicBand;

export const DataTable = () => {
  const { items, locale, user, deleteItem } = useAppContext();
  const t = translations[locale];
  
  const [search, setSearch] = useState("");
  const [sortField, setSortField] = useState<SortField>("id");
  const [sortAsc, setSortAsc] = useState(true);
  const [editingItem, setEditingItem] = useState<MusicBand | null>(null);

  const filteredAndSorted = useMemo(() => {
    let result = items;
    
    if (search.trim()) {
      const q = search.toLowerCase();
      result = result.filter(item => item.name.toLowerCase().includes(q));
    }
    
    result = [...result].sort((a, b) => {
      const aVal = a[sortField];
      const bVal = b[sortField];
      
      if (aVal === null || aVal === undefined) return sortAsc ? 1 : -1;
      if (bVal === null || bVal === undefined) return sortAsc ? -1 : 1;

      if (typeof aVal === 'string' && typeof bVal === 'string') {
        return sortAsc ? aVal.localeCompare(bVal) : bVal.localeCompare(aVal);
      }
      if (typeof aVal === 'number' && typeof bVal === 'number') {
        return sortAsc ? aVal - bVal : bVal - aVal;
      }
      return 0;
    });
    
    return result;
  }, [items, search, sortField, sortAsc]);

  const toggleSort = (field: SortField) => {
    if (sortField === field) {
      setSortAsc(!sortAsc);
    } else {
      setSortField(field);
      setSortAsc(true);
    }
  };

  const SortIcon = ({ field }: { field: SortField }) => {
    if (sortField !== field) return <ChevronUp className="w-3 h-3 opacity-0 group-hover:opacity-30" />;
    return sortAsc ? <ChevronUp className="w-3 h-3 text-blue-500" /> : <ChevronDown className="w-3 h-3 text-blue-500" />;
  };

  return (
    <div className="h-full flex flex-col p-4 bg-transparent text-slate-800 dark:text-white transition-colors duration-300">
      <div className="flex items-center justify-between mb-4">
        <h2 className="font-semibold text-lg">{t.items}</h2>
        <div className="relative">
          <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 dark:text-white/40" />
          <input
            type="text"
            placeholder={t.search}
            value={search}
            onChange={e => setSearch(e.target.value)}
            className="bg-white dark:bg-black/20 border border-slate-200 dark:border-white/10 rounded-lg pl-9 pr-4 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500/50 w-64 text-slate-900 dark:text-white placeholder-slate-400 dark:placeholder-white/30 transition-all shadow-sm"
          />
        </div>
      </div>

      <div className="flex-1 overflow-auto rounded-xl border border-slate-200 dark:border-white/10 bg-white/50 dark:bg-black/10 shadow-inner custom-scrollbar">
        <table className="w-full text-left text-sm whitespace-nowrap">
          <thead className="bg-slate-50/80 dark:bg-white/5 sticky top-0 backdrop-blur-md z-10 text-slate-600 dark:text-white/60">
            <tr>
              <th className="font-medium p-3 cursor-pointer group hover:text-slate-900 dark:hover:text-white transition-colors" onClick={() => toggleSort("id")}>
                <div className="flex items-center gap-1">ID <SortIcon field="id" /></div>
              </th>
              <th className="font-medium p-3 cursor-pointer group hover:text-slate-900 dark:hover:text-white transition-colors" onClick={() => toggleSort("name")}>
                <div className="flex items-center gap-1">{t.name} <SortIcon field="name" /></div>
              </th>
              <th className="font-medium p-3 cursor-pointer group hover:text-slate-900 dark:hover:text-white transition-colors" onClick={() => toggleSort("owner")}>
                <div className="flex items-center gap-1">{t.owner} <SortIcon field="owner" /></div>
              </th>
              <th className="font-medium p-3 cursor-pointer group hover:text-slate-900 dark:hover:text-white transition-colors" onClick={() => toggleSort("x")}>
                <div className="flex items-center gap-1">{t.x} <SortIcon field="x" /></div>
              </th>
              <th className="font-medium p-3 cursor-pointer group hover:text-slate-900 dark:hover:text-white transition-colors" onClick={() => toggleSort("y")}>
                <div className="flex items-center gap-1">{t.y} <SortIcon field="y" /></div>
              </th>
              <th className="font-medium p-3 cursor-pointer group hover:text-slate-900 dark:hover:text-white transition-colors" onClick={() => toggleSort("numberOfParticipants")}>
                <div className="flex items-center gap-1">{t.numberOfParticipants} <SortIcon field="numberOfParticipants" /></div>
              </th>
              <th className="font-medium p-3 cursor-pointer group hover:text-slate-900 dark:hover:text-white transition-colors" onClick={() => toggleSort("genre")}>
                <div className="flex items-center gap-1">{t.genre} <SortIcon field="genre" /></div>
              </th>
              <th className="font-medium p-3 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-200 dark:divide-white/5">
            {filteredAndSorted.length === 0 ? (
              <tr>
                <td colSpan={8} className="p-8 text-center text-slate-500 dark:text-white/40">
                  {t.no_items}
                </td>
              </tr>
            ) : (
              filteredAndSorted.map(item => (
                <tr key={item.id} className="hover:bg-white dark:hover:bg-white/5 transition-colors group">
                  <td className="p-3 text-slate-500 dark:text-white/50">{item.id}</td>
                  <td className="p-3">
                    <div className="flex items-center gap-2">
                      <div className="w-2.5 h-2.5 rounded-full shadow-sm" style={{ backgroundColor: item.color }} />
                      <span className="font-medium text-slate-900 dark:text-white">{item.name}</span>
                    </div>
                  </td>
                  <td className="p-3">
                    <span className={`px-2 py-0.5 rounded text-xs font-medium ${item.owner === user ? "bg-blue-100 text-blue-700 border-blue-200 dark:bg-white/10 dark:text-blue-400 dark:border-blue-500/30" : "bg-slate-100 text-slate-600 dark:bg-white/10 dark:text-white/70"} border`}>
                      {item.owner}
                    </span>
                  </td>
                  <td className="p-3 text-slate-600 dark:text-white/70">{item.x.toFixed(1)}</td>
                  <td className="p-3 text-slate-600 dark:text-white/70">{item.y.toFixed(1)}</td>
                  <td className="p-3 text-slate-600 dark:text-white/70">{item.numberOfParticipants}</td>
                  <td className="p-3 text-slate-600 dark:text-white/70">{item.genre || "-"}</td>
                  <td className="p-3 text-right">
                    {item.owner === user && (
                      <div className="flex items-center justify-end gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                        <button onClick={() => setEditingItem(item)} className="p-1.5 hover:bg-slate-100 dark:hover:bg-white/10 rounded-md text-slate-500 hover:text-blue-600 dark:text-white/70 dark:hover:text-blue-400 transition-colors">
                          <Edit2 className="w-4 h-4" />
                        </button>
                        <button onClick={() => deleteItem(item.id)} className="p-1.5 hover:bg-slate-100 dark:hover:bg-white/10 rounded-md text-slate-500 hover:text-red-600 dark:text-white/70 dark:hover:text-red-400 transition-colors">
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {editingItem && (
        <CRUDModal 
          isOpen={true} 
          onClose={() => setEditingItem(null)} 
          item={editingItem} 
        />
      )}
    </div>
  );
};
