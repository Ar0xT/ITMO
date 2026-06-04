import { useEffect } from "react";
import { useNavigate } from "react-router";
import { Panel, PanelGroup, PanelResizeHandle } from "react-resizable-panels";
import { useAppContext } from "../context/AppContext";
import { Sidebar } from "./Sidebar";
import { DataTable } from "./DataTable";
import { CanvasViz } from "./CanvasViz";
import { TopBar } from "./TopBar";

export const Dashboard = () => {
  const { user } = useAppContext();
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) {
      navigate("/");
    }
  }, [user, navigate]);

  if (!user) return null;

  return (
    <div className="flex-1 flex h-full p-2 gap-2 overflow-hidden bg-slate-100/50 dark:bg-black/40 transition-colors duration-300">
      <div className="w-64 flex-shrink-0">
        <Sidebar />
      </div>
      
      <div className="flex-1 flex flex-col min-w-0 bg-white/70 dark:bg-white/5 backdrop-blur-xl border border-slate-200 dark:border-white/10 rounded-2xl overflow-hidden shadow-xl dark:shadow-2xl transition-colors duration-300">
        <TopBar />
        
        <div className="flex-1 h-full">
          <PanelGroup direction="vertical">
            <Panel defaultSize={40} minSize={20} className="relative bg-white/50 dark:bg-black/20 transition-colors duration-300">
              <DataTable />
            </Panel>
            
            <PanelResizeHandle className="h-1 bg-slate-200 dark:bg-white/5 hover:bg-blue-500/50 dark:hover:bg-blue-500/50 transition-colors cursor-row-resize" />
            
            <Panel defaultSize={60} minSize={20} className="relative">
              <CanvasViz />
            </Panel>
          </PanelGroup>
        </div>
      </div>
    </div>
  );
};
