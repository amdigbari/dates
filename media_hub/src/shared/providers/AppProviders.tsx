'use client';

import { QueryClientProvider } from '@tanstack/react-query';
import { type ReactNode } from 'react';
import { SSRProvider as BootstrapSSRProvider, ThemeProvider as BootstrapThemeProvider } from 'react-bootstrap';

import { ToastProvider } from 'src/shared/components';
import { queryClient } from 'src/shared/services';

import { useLocaleState } from '../utils';

interface Props {
  children: ReactNode;
}
export function AppProviders({ children }: Props): ReactNode {
  // Sets the locale to the Cookies at start
  useLocaleState();

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
