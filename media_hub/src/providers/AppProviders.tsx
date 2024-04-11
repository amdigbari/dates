'use client';

import { QueryClientProvider } from '@tanstack/react-query';
import { type ReactNode } from 'react';
import { SSRProvider as BootstrapSSRProvider, ThemeProvider as BootstrapThemeProvider } from 'react-bootstrap';

import { ToastProvider } from 'src/components';
import { queryClient } from 'src/services';

interface Props {
  children: ReactNode;
}
export function AppProviders({ children }: Props): ReactNode {
  return (
    <QueryClientProvider client={queryClient}>
      <BootstrapSSRProvider>
        <BootstrapThemeProvider dir="rtl">
          <ToastProvider>{children}</ToastProvider>
        </BootstrapThemeProvider>
      </BootstrapSSRProvider>
    </QueryClientProvider>
  );
}
