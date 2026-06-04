import React, { createContext, useContext, useState } from "react";
import { Locale } from "../i18n/translations";

export type MusicGenre = "RAP" | "HIP_HOP" | "POST_ROCK" | "BRIT_POP";

export interface MusicBand {
  id: number;
  name: string;
  owner: string;
  x: number;
  y: number;
  creationDate: string;
  numberOfParticipants: number;
  singlesCount: number;
  establishmentDate: string | null;
  genre: MusicGenre | null;
  studioName: string | null;
  color: string;
}

interface AppContextType {
  user: string | null;
  login: (username: string) => void;
  logout: () => void;
  locale: Locale;
  setLocale: (locale: Locale) => void;
  items: MusicBand[];
  addItem: (item: Omit<MusicBand, "id" | "owner" | "creationDate" | "color">) => void;
  updateItem: (id: number, item: Partial<MusicBand>) => void;
  deleteItem: (id: number) => void;
  userColorMap: Record<string, string>;
}

const AppContext = createContext<AppContextType | null>(null);

// Elegant, sophisticated colors for users (macOS/Yandex inspired)
const PREDEFINED_COLORS = [
  "#0052CC", // Deep Blue
  "#00875A", // Emerald
  "#DE350B", // Crimson
  "#FF991F", // Amber
  "#6554C0", // Indigo
  "#00B8D9", // Cyan
  "#36B37E", // Mint
];

// Initial mock data
const MOCK_ITEMS: MusicBand[] = [
  {
    id: 1,
    name: "The Velvet Underground",
    owner: "admin",
    x: 150,
    y: 150,
    creationDate: new Date().toISOString(),
    numberOfParticipants: 4,
    singlesCount: 12,
    establishmentDate: "1964-01-01",
    genre: "POST_ROCK",
    studioName: "Scepter Studios",
    color: "#0052CC",
  },
  {
    id: 2,
    name: "Wu-Tang Clan",
    owner: "guest",
    x: 300,
    y: -250,
    creationDate: new Date().toISOString(),
    numberOfParticipants: 9,
    singlesCount: 35,
    establishmentDate: "1992-01-01",
    genre: "HIP_HOP",
    studioName: "Firehouse Studio",
    color: "#00875A",
  },
  {
    id: 3,
    name: "Oasis",
    owner: "admin",
    x: -50,
    y: 100,
    creationDate: new Date().toISOString(),
    numberOfParticipants: 5,
    singlesCount: 29,
    establishmentDate: "1991-01-01",
    genre: "BRIT_POP",
    studioName: "Sawmills Studio",
    color: "#0052CC",
  },
];

export const AppProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<string | null>(null);
  const [locale, setLocale] = useState<Locale>("en");
  const [items, setItems] = useState<MusicBand[]>(MOCK_ITEMS);
  const [userColorMap, setUserColorMap] = useState<Record<string, string>>({
    "admin": "#0052CC",
    "guest": "#00875A"
  });

  const getUserColor = (username: string) => {
    if (userColorMap[username]) return userColorMap[username];
    const colorIndex = Object.keys(userColorMap).length % PREDEFINED_COLORS.length;
    const color = PREDEFINED_COLORS[colorIndex];
    setUserColorMap(prev => ({ ...prev, [username]: color }));
    return color;
  };

  const login = (username: string) => {
    setUser(username);
    getUserColor(username); // Ensure color is assigned
  };

  const logout = () => {
    setUser(null);
  };

  const addItem = (itemData: Omit<MusicBand, "id" | "owner" | "creationDate" | "color">) => {
    if (!user) return;
    const maxId = items.length > 0 ? Math.max(...items.map(i => i.id)) : 0;
    const newItem: MusicBand = {
      ...itemData,
      id: maxId + 1,
      owner: user,
      color: getUserColor(user),
      creationDate: new Date().toISOString(),
    };
    setItems((prev) => [...prev, newItem]);
  };

  const updateItem = (id: number, itemData: Partial<MusicBand>) => {
    setItems((prev) => prev.map(item => item.id === id ? { ...item, ...itemData } : item));
  };

  const deleteItem = (id: number) => {
    setItems((prev) => prev.filter(item => item.id !== id));
  };

  return (
    <AppContext.Provider value={{ user, login, logout, locale, setLocale, items, addItem, updateItem, deleteItem, userColorMap }}>
      {children}
    </AppContext.Provider>
  );
};

export const useAppContext = () => {
  const context = useContext(AppContext);
  if (!context) throw new Error("useAppContext must be used within AppProvider");
  return context;
};
