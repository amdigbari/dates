'use client';

import { createContext } from 'react';

export interface ToastContextType {
  show: (toast: Omit<Toast, 'id'>) => Toast['id']; // Returns ID
  hide: (id: Toast['id']) => void;
}
export const ToastContext = createContext<ToastContextType>({ show: () => '', hide: () => false });
