import type { Metadata } from 'next';

import { AppProviders } from 'src/providers';
import type { LayoutProps } from 'src/types/globals';

export const metadata: Metadata = {
  title: 'Create Next App',
  description: 'Generated by create next app',
};

export default function RootLayout({ children }: LayoutProps) {
  return (
    <html dir="rtl" lang="fa">
      <body>
        <AppProviders>{children}</AppProviders>
      </body>
    </html>
  );
}