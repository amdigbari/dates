interface PageProps<T extends string = never, U extends string = never> {
  params: { [k in T]: string };
  searchParams: SearchParams<U>;
}

type LayoutProps<T extends string = void> = Readonly<Record<T | 'children', ReactNode>>;

// Extracted from ToastProps
type ColorVariant = 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'info' | 'dark' | 'light';
