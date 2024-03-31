import { QueryClient, type UseMutationOptions, type UseQueryOptions, isServer } from '@tanstack/react-query';
import createClient, { type ClientMethod, type FetchOptions, type FetchResponse } from 'openapi-fetch';
import type { FilterKeys, HttpMethod, PathsWithMethod as PathsWith } from 'openapi-typescript-helpers';

import type { paths as MediaManagerPaths } from './.generated/types/media-manager';

// NOTE: This approach assumes that every backed services will be served via a API Gateway
// and there will be only one base_url for every service API paths
const API_BASE_URL = process.env.NEXT_APP_API_BASE_URL || 'http://localhost:8000/api/';

type PathGen<BasePath extends string, Paths> = {
  [k in keyof Paths & string as `${BasePath}${k}`]: Paths[k];
};
type Paths = PathGen<'media-manager:', MediaManagerPaths>;

/**
 *
 * @param path The path of the request that is exists in the Paths.
 * @returns The path value which the : after the service name is replaced with /.
 * @example getRequestPath('sample:/'): 'sample/'
 * @example getRequestPath('sample:/pets'): 'sample/pets'
 */
function getRequestPath(path: keyof Paths) {
  return path.replace(':', '/').replace(/\/\//g, '/');
}

const client = createClient<Paths>({
  baseUrl: API_BASE_URL,
});

type PathsOf<M extends HttpMethod> = PathsWith<Paths, M>;
type RequestData<M extends HttpMethod, P extends PathsOf<M>> = FetchOptions<FilterKeys<Paths[P], M>>;
type ResponseData<M extends HttpMethod, P extends PathsOf<M>> = NonNullable<
  FetchResponse<M extends keyof Paths[P] ? Paths[P][keyof Paths[P] & M] : unknown, never>['data']
>;

// This makes the init key optional in case the params are all optional
type QueryServiceParams<P extends PathsOf<'get'>> =
  RequestData<'get', P> extends { params: any } ? [P, RequestData<'get', P>] : [P] | [P, RequestData<'get', P>];
export function queryService<P extends PathsOf<'get'>>(
  ...[url, init]: QueryServiceParams<P>
): UseQueryOptions<ResponseData<'get', P>> {
  return { queryKey: [getRequestPath(url), init] };
}

export function mutateService<M extends HttpMethod, P extends PathsOf<M>>(
  ...[method, url]: [M, P]
): UseMutationOptions<ResponseData<M, P>, never, RequestData<M, P>> {
  return {
    mutationKey: [getRequestPath(url)],
    mutationFn: (data: RequestData<M, P>) =>
      (client[method.toUpperCase() as Uppercase<M>] as ClientMethod<any, HttpMethod>)(
        getRequestPath(url),
        data,
      ) as Promise<ResponseData<M, P>>,
  };
}

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      throwOnError: true,
      retry: isServer ? false : undefined,
      queryFn: async (context) => {
        const [url, ...init] = context.queryKey;
        // TODO: validating the shape of the queryKey
        return client.GET(url as any, ...(init as any));
      },
    },
  },
});
