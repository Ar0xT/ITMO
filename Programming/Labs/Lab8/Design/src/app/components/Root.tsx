import { Outlet } from "react-router";
import { ThemeProvider } from "next-themes";
import { AppProvider } from "../context/AppContext";

export const Root = () => {
  return (
    <ThemeProvider attribute="class" defaultTheme="system" enableSystem>
      <AppProvider>
        <div className="min-h-screen bg-slate-50 text-slate-900 dark:bg-[#0c0c0e] dark:text-slate-100 font-sans transition-colors duration-300">
          <div className="absolute inset-0 overflow-hidden pointer-events-none z-0">
            <div className="absolute top-[-20%] left-[-10%] w-[50%] h-[50%] bg-blue-600/5 dark:bg-blue-600/10 rounded-full blur-[140px]" />
            <div className="absolute bottom-[-20%] right-[-10%] w-[50%] h-[50%] bg-indigo-600/5 dark:bg-indigo-600/10 rounded-full blur-[140px]" />
          </div>
          <div className="relative z-10 h-screen w-full flex flex-col">
            <Outlet />
          </div>
        </div>
      </AppProvider>
    </ThemeProvider>
  );
};
