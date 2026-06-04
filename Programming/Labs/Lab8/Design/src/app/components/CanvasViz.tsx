import { useRef, useEffect, useState } from "react";
import { useTheme } from "next-themes";
import { useAppContext, MusicBand } from "../context/AppContext";
import { translations } from "../i18n/translations";
import { Maximize2, MousePointer2 } from "lucide-react";
import { motion, AnimatePresence } from "motion/react";

export const CanvasViz = () => {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const { items, locale, user } = useAppContext();
  const { theme } = useTheme();
  const t = translations[locale];
  
  const [selectedItem, setSelectedItem] = useState<MusicBand | null>(null);
  const [dimensions, setDimensions] = useState({ width: 0, height: 0 });
  const [mousePos, setMousePos] = useState({ x: 0, y: 0 });
  const [hoveredItemId, setHoveredItemId] = useState<number | null>(null);

  // Animation values for rendering
  const itemsRenderState = useRef<Record<number, { scale: number, opacity: number, x: number, y: number }>>({});

  useEffect(() => {
    const observer = new ResizeObserver(entries => {
      if (entries[0]) {
        const { width, height } = entries[0].contentRect;
        setDimensions({ width, height });
      }
    });
    
    if (containerRef.current) {
      observer.observe(containerRef.current);
    }
    
    return () => observer.disconnect();
  }, []);

  // Main Render Loop
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    let animationFrameId: number;
    const isDark = theme === "dark" || (theme === "system" && window.matchMatchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches);

    const render = () => {
      // Clear canvas
      ctx.clearRect(0, 0, dimensions.width, dimensions.height);
      
      // Draw grid
      ctx.strokeStyle = isDark ? "rgba(255, 255, 255, 0.05)" : "rgba(0, 0, 0, 0.05)";
      ctx.lineWidth = 1;
      const gridSize = 50;
      
      // Offset grid to center origin
      const offsetX = dimensions.width / 2;
      const offsetY = dimensions.height / 2;

      for (let x = offsetX % gridSize; x < dimensions.width; x += gridSize) {
        ctx.beginPath();
        ctx.moveTo(x, 0);
        ctx.lineTo(x, dimensions.height);
        ctx.stroke();
      }
      for (let y = offsetY % gridSize; y < dimensions.height; y += gridSize) {
        ctx.beginPath();
        ctx.moveTo(0, y);
        ctx.lineTo(dimensions.width, y);
        ctx.stroke();
      }

      // Draw Axes
      ctx.strokeStyle = isDark ? "rgba(255, 255, 255, 0.2)" : "rgba(0, 0, 0, 0.2)";
      ctx.lineWidth = 2;
      ctx.beginPath();
      ctx.moveTo(offsetX, 0);
      ctx.lineTo(offsetX, dimensions.height);
      ctx.stroke();
      ctx.beginPath();
      ctx.moveTo(0, offsetY);
      ctx.lineTo(dimensions.width, offsetY);
      ctx.stroke();

      const activeIds = new Set(items.map(i => i.id));
      
      Object.keys(itemsRenderState.current).forEach(idStr => {
        const id = Number(idStr);
        if (!activeIds.has(id)) {
          delete itemsRenderState.current[id];
        }
      });

      // Draw items
      items.forEach(item => {
        if (!itemsRenderState.current[item.id]) {
          itemsRenderState.current[item.id] = { scale: 0, opacity: 0, x: item.x, y: item.y };
        }
        
        const state = itemsRenderState.current[item.id];
        
        state.scale += (1 - state.scale) * 0.15;
        state.opacity += (1 - state.opacity) * 0.15;
        state.x += (item.x - state.x) * 0.15;
        state.y += (item.y - state.y) * 0.15;

        const isHovered = hoveredItemId === item.id;
        const isSelected = selectedItem?.id === item.id;
        
        const screenX = state.x + offsetX;
        const screenY = -state.y + offsetY; // Invert Y axis
        
        // Base size on participants, but keep it within bounds
        const radius = Math.max(15, Math.min(60, 10 + item.numberOfParticipants * 3));

        ctx.save();
        ctx.globalAlpha = state.opacity;
        ctx.translate(screenX, screenY);
        ctx.scale(state.scale, state.scale);

        ctx.shadowColor = item.color;
        ctx.shadowBlur = isHovered || isSelected ? 25 : 12;
        ctx.shadowOffsetX = 0;
        ctx.shadowOffsetY = 4;

        ctx.beginPath();
        ctx.arc(0, 0, radius, 0, Math.PI * 2);
        
        const grad = ctx.createRadialGradient(0, 0, 0, 0, 0, radius);
        grad.addColorStop(0, `${item.color}FF`);
        grad.addColorStop(1, `${item.color}DD`);
        ctx.fillStyle = grad;
        ctx.fill();

        ctx.strokeStyle = isDark ? "rgba(255, 255, 255, 0.9)" : "rgba(255, 255, 255, 1)";
        ctx.lineWidth = isSelected ? 4 : (isHovered ? 2 : 1.5);
        ctx.stroke();

        // Label
        if (radius > 20 || isHovered) {
          ctx.fillStyle = "#FFF";
          ctx.font = "bold 10px sans-serif";
          ctx.textAlign = "center";
          ctx.textBaseline = "middle";
          ctx.shadowBlur = 4;
          ctx.shadowColor = "rgba(0,0,0,0.5)";
          // Truncate name
          const shortName = item.name.length > 10 ? item.name.substring(0, 8) + ".." : item.name;
          ctx.fillText(shortName, 0, 0);
        }

        if (item.owner === user) {
          ctx.beginPath();
          ctx.arc(radius * 0.7, -radius * 0.7, 5, 0, Math.PI * 2);
          ctx.fillStyle = "#FFF";
          ctx.fill();
        }

        ctx.restore();
      });

      animationFrameId = requestAnimationFrame(render);
    };

    render();

    return () => cancelAnimationFrame(animationFrameId);
  }, [items, dimensions, hoveredItemId, selectedItem, user, theme]);

  const handleMouseMove = (e: React.MouseEvent<HTMLCanvasElement>) => {
    if (!canvasRef.current) return;
    const rect = canvasRef.current.getBoundingClientRect();
    const clientX = e.clientX - rect.left;
    const clientY = e.clientY - rect.top;
    
    const offsetX = dimensions.width / 2;
    const offsetY = dimensions.height / 2;
    
    // Map screen to world coordinates
    const worldX = clientX - offsetX;
    const worldY = -(clientY - offsetY);
    
    setMousePos({ x: worldX, y: worldY });

    let foundHover = null;
    for (let i = items.length - 1; i >= 0; i--) {
      const item = items[i];
      const radius = Math.max(15, Math.min(60, 10 + item.numberOfParticipants * 3));
      
      const dx = worldX - item.x;
      const dy = worldY - item.y;
      
      if (Math.sqrt(dx * dx + dy * dy) <= radius) {
        foundHover = item.id;
        break;
      }
    }
    setHoveredItemId(foundHover);
  };

  const handleClick = () => {
    if (hoveredItemId !== null) {
      const item = items.find(i => i.id === hoveredItemId);
      if (item) setSelectedItem(item);
    } else {
      setSelectedItem(null);
    }
  };

  return (
    <div className="h-full flex flex-col p-4">
      <div className="flex items-center justify-between mb-4">
        <h2 className="font-semibold text-lg flex items-center gap-2 text-slate-800 dark:text-white">
          <Maximize2 className="w-5 h-5 text-blue-600 dark:text-blue-400" />
          Visualization Area
        </h2>
        <div className="flex items-center gap-2 text-xs text-slate-500 dark:text-white/50 bg-white/50 dark:bg-black/20 px-3 py-1.5 rounded-full border border-slate-200 dark:border-white/10 backdrop-blur-sm">
          <MousePointer2 className="w-3.5 h-3.5" />
          X: {mousePos.x.toFixed(0)}, Y: {mousePos.y.toFixed(0)}
        </div>
      </div>

      <div 
        ref={containerRef} 
        className="flex-1 relative bg-white/50 dark:bg-black/30 rounded-xl border border-slate-200 dark:border-white/10 overflow-hidden cursor-crosshair shadow-inner transition-colors duration-300"
      >
        <canvas
          ref={canvasRef}
          width={dimensions.width}
          height={dimensions.height}
          onMouseMove={handleMouseMove}
          onClick={handleClick}
          onMouseLeave={() => setHoveredItemId(null)}
          className="absolute inset-0 w-full h-full"
        />

        <AnimatePresence>
          {selectedItem && (
            <motion.div
              initial={{ opacity: 0, x: 20, scale: 0.95 }}
              animate={{ opacity: 1, x: 0, scale: 1 }}
              exit={{ opacity: 0, x: 20, scale: 0.95 }}
              className="absolute top-4 right-4 w-72 bg-white/90 dark:bg-slate-800/95 backdrop-blur-xl border border-slate-200 dark:border-white/20 rounded-xl p-5 shadow-2xl pointer-events-none transition-colors duration-300"
            >
              <h3 className="font-semibold text-slate-900 dark:text-white mb-3 pb-3 border-b border-slate-200 dark:border-white/10">{t.details}</h3>
              <div className="space-y-3 text-sm">
                <div className="flex justify-between items-center">
                  <span className="text-slate-500 dark:text-white/50">ID</span>
                  <span className="font-mono text-slate-800 dark:text-white/80 bg-slate-100 dark:bg-white/5 px-2 py-0.5 rounded">{selectedItem.id}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-slate-500 dark:text-white/50">{t.name}</span>
                  <span className="font-medium text-slate-900 dark:text-white truncate max-w-[150px]">{selectedItem.name}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-slate-500 dark:text-white/50">{t.owner}</span>
                  <span className="flex items-center gap-1.5 font-medium text-slate-900 dark:text-white">
                    <span className="w-2.5 h-2.5 rounded-full shadow-sm" style={{ backgroundColor: selectedItem.color }} />
                    {selectedItem.owner}
                  </span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-slate-500 dark:text-white/50">{t.genre}</span>
                  <span className="text-slate-800 dark:text-white/80 font-medium">{selectedItem.genre || "N/A"}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-slate-500 dark:text-white/50">{t.numberOfParticipants}</span>
                  <span className="text-slate-800 dark:text-white/80 font-medium">{selectedItem.numberOfParticipants}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-slate-500 dark:text-white/50">Coordinates</span>
                  <span className="font-mono text-slate-800 dark:text-white/80 bg-slate-100 dark:bg-white/5 px-2 py-0.5 rounded">
                    {selectedItem.x.toFixed(1)}, {selectedItem.y.toFixed(1)}
                  </span>
                </div>
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </div>
  );
};
