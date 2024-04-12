interface PageProps<T extends string = never, U extends string = never> {
  params: { [k in T]: string };
  searchParams: SearchParams<U>;
}

type LayoutProps<T extends string = void> = Readonly<Record<T | 'children', ReactNode>>;

// Extracted from ToastProps
type Variant = 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'info' | 'dark' | 'light';

interface Toast {
  id: string;
  header?: string | null | (() => ReactNode);
  body: string | (() => NonNullable<ReactNode>);
  duration?: number;
  variant: Variant;
}
