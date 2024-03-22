/// <reference types="@emotion/react/types/css-prop" />
import type { SxProps, Theme } from '@mui/material';
import type { ReactNode } from 'react';

interface PageProps<T extends string = never, U extends string = never> {
  params: { [k in T]: string };
  searchParams: SearchParams<U>;
}

type LayoutProps<T extends string = void> = Readonly<Record<T | 'children', ReactNode>>;

type MaterialSxProps = SxProps<Theme>;
