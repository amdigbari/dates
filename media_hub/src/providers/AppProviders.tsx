'use client';

import { AppRouterCacheProvider } from '@mui/material-nextjs/v14-appRouter';
import CssBaseline from '@mui/material/CssBaseline';
import { ThemeProvider } from '@mui/material/styles';
import { QueryClientProvider } from '@tanstack/react-query';
import { type ReactNode } from 'react';

import { queryClient } from 'src/services';
import { theme } from 'src/theme';

interface Props {
  children: ReactNode;
}
export function AppProviders({ children }: Props): ReactNode {
  return (
    <QueryClientProvider client={queryClient}>
      <AppRouterCacheProvider options={{ enableCssLayer: true }}>
        <ThemeProvider theme={theme}>
          <CssBaseline />

          {children}
        </ThemeProvider>
      </AppRouterCacheProvider>
    </QueryClientProvider>
  );
}
