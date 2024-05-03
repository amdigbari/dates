'use client';

import { ReactNode, useMemo, useState } from 'react';
import { ToastContainer as BootstrapToastContainer } from 'react-bootstrap';
import { v4 as uuidv4 } from 'uuid';

import { TOAST_MAX_SIZE } from './ToastConstants';
import { type Toast, ToastContext, type ToastContextType } from './ToastContext';
import { ToastItem } from './ToastItem';

type Props = Readonly<Record<'children', ReactNode>>;
export function ToastProvider({ children }: Props) {
  const [toasts, setToasts] = useState<Toast[]>([]);

  function addToast(toast: Omit<Toast, 'id'>): Toast['id'] {
    const newToastId = uuidv4();

    setToasts((t) => [{ ...toast, id: newToastId }, ...t].slice(0, TOAST_MAX_SIZE));

    return newToastId;
  }

  function removeToast(id: Toast['id']) {
    setToasts((t) => t.filter((toast) => toast.id !== id));
  }

  const setterValue = useMemo<ToastContextType>(() => ({ show: addToast, hide: removeToast }), []);

  return (
    <ToastContext.Provider value={setterValue}>
      {children}

      <BootstrapToastContainer position="bottom-start" className="mb-4 ms-4">
        {toasts.map((toast) => (
          <ToastItem key={toast.id} toast={toast} />
        ))}
      </BootstrapToastContainer>
    </ToastContext.Provider>
  );
}
