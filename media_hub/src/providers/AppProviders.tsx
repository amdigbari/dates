'use client';

import { QueryClientProvider } from '@tanstack/react-query';
import { type ReactNode } from 'react';
import BootstrapSSRProvider from 'react-bootstrap/SSRProvider';
import BootstrapThemeProvider from 'react-bootstrap/ThemeProvider';

import { queryClient } from 'src/services';

interface Props {
  children: ReactNode;
}
export function AppProviders({ children }: Props): ReactNode {
  return (
    <QueryClientProvider client={queryClient}>
      <BootstrapSSRProvider>
        <BootstrapThemeProvider dir="rtl">{children}</BootstrapThemeProvider>
      </BootstrapSSRProvider>
    </QueryClientProvider>
  );
}
