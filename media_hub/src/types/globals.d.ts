interface PageProps<T extends string = never, U extends string = never> {
  params: { [k in T]: string };
  searchParams: SearchParams<U>;
}

type LayoutProps<T extends string = void> = Readonly<Record<T | 'children', ReactNode>>;
