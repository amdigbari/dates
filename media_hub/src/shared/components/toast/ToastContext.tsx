'use client';

import { type ReactNode, createContext } from 'react';

export interface Toast {
  id: string;
  header?: string | null | (() => ReactNode);
  body: string | (() => NonNullable<ReactNode>);
  duration?: number;
  variant: ColorVariant;
}

export interface ToastContextType {
  show: (toast: Omit<Toast, 'id'>) => Toast['id']; // Returns ID
  hide: (id: Toast['id']) => void;
}
export const ToastContext = createContext<ToastContextType>({ show: () => '', hide: () => false });
