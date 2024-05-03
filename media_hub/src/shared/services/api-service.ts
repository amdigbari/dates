import { StatusCodes } from 'http-status-codes';
import { redirect } from 'next/navigation';
import createClient, {
  type ClientMethod,
  type FetchOptions,
  type FetchResponse,
  type HeadersOptions,
} from 'openapi-fetch';
import type { FilterKeys, HttpMethod, PathsWithMethod as PathsWith } from 'openapi-typescript-helpers';

import { isServer } from 'src/shared/configs';

import { LocaleService, TokenStorage } from '../utils';
import type { paths as MediaManager } from './.generated/types/media-manager';
import { type APIServicesCombinedErrors, HttpError } from './api-service-errors';

// NOTE: This approach assumes that every backed services will be served via a API Gateway
// and there will be only one base_url for every service API paths
const API_BASE_URL = process.env.NEXT_APP_API_BASE_URL || 'http://localhost:3000/api/';

type PathGen<BasePath extends string, Paths> = {
  [k in keyof Paths & string as `${BasePath}${k}`]: Paths[k];
};
type Paths = PathGen<'media-manager:', MediaManager>;

export type HttpPaths<M extends HttpMethod> = PathsWith<Paths, M>;
export type HttpRequestData<M extends HttpMethod, P extends HttpPaths<M>> = FetchOptions<FilterKeys<Paths[P], M>>;

type OpenapiResponse<M extends HttpMethod, P extends HttpPaths<M>> = NonNullable<
  FetchResponse<M extends keyof Paths[P] ? Paths[P][keyof Paths[P] & M] : unknown, object>
>;
export type HttpResponseData<M extends HttpMethod, P extends HttpPaths<M>> = NonNullable<OpenapiResponse<M, P>['data']>;

/**
 *
 * @param path The path of the request that is exists in the Paths.
 * @returns The path value which the : after the service name is replaced with /.
 * @example getRequestPath('sample:/'): 'sample/'
 * @example getRequestPath('sample:/pets'): 'sample/pets'
 */
function getRequestPath(path: keyof Paths) {
  // This is due to openapi-fetch behavior that removes the trailing slash of the base url
  return `/${path.replace(':', '/')}`.replace(/\/{2,}/g, '/');
}

const client = createClient<Paths>({
  baseUrl: API_BASE_URL,
});

export type ClientFetchParams<M extends HttpMethod, P extends HttpPaths<M>> =
  HttpRequestData<M, P> extends { params: any } | { body: any }
    ? [M, P, HttpRequestData<M, P>]
    : [M, P] | [M, P, HttpRequestData<M, P>];
export async function clientFetch<M extends HttpMethod, P extends HttpPaths<M>>(
  ...[method, url, options = {} as HttpRequestData<M, P>]: ClientFetchParams<M, P>
): Promise<HttpResponseData<M, P>> {
  try {
    // Inject Headers
    options.headers = injectFieldsToRequestHeaders(options.headers, {
      key: 'Accept-Language',
      value: LocaleService.get(),
    });

    const accessToken = TokenStorage.get('access-token');
    accessToken &&
      injectFieldsToRequestHeaders(options.headers, { key: 'Authorization', value: `Bearer ${accessToken}` });

    const { data, error, response } = (await (
      client[method.toUpperCase() as Uppercase<M>] as ClientMethod<any, HttpMethod>
    )(getRequestPath(url), options)) as OpenapiResponse<M, P>;

    if (error) {
      if (typeof error === 'string') {
        throw new HttpError({ status: false, message: error, status_code: response.status });
      }

      // Refresh The token and Retry
      if (response.status === StatusCodes.UNAUTHORIZED) {
        const refreshToken = TokenStorage.get('refresh-token');
        if (refreshToken) {
          const refreshTokenResponse = await client.POST('media-manager:/api/v1/auth/refresh-token', {
            body: { token: refreshToken },
          });
          if (refreshTokenResponse.data) {
            const { accessToken: newAccessToken, refreshToken: newRefreshToken } = refreshTokenResponse.data.data;

            options.headers = injectFieldsToRequestHeaders(options.headers, {
              key: 'Authorization',
              value: `Bearer ${newAccessToken}`,
            });
            const retryResponse = (await (
              client[method.toUpperCase() as Uppercase<M>] as ClientMethod<any, HttpMethod>
            )(getRequestPath(url), options)) as OpenapiResponse<M, P>;

            if (retryResponse.data) {
              TokenStorage.set('access-token', newAccessToken);
              TokenStorage.set('refresh-token', newRefreshToken);

              return retryResponse as HttpResponseData<M, P>;
            }
          }
        }

        TokenStorage.empty();
        if (isServer) {
          redirect('/auth/login');
        } else {
          window.location.pathname = '/auth/login';
        }
      }

      throw new HttpError({ ...error, status_code: response.status } as unknown as APIServicesCombinedErrors);
    }

    return data as HttpResponseData<M, P>;
  } catch (error) {
    if (error instanceof HttpError) {
      throw error;
    }

    // In case the openapi itself throws an error.
    // Like making request without a network

    // The `${error}` makes sure that the message value is a string
    throw new HttpError({ status: false, message: `${error}`, status_code: StatusCodes.INTERNAL_SERVER_ERROR });
  }
}

function injectFieldsToRequestHeaders(
  header: HeadersOptions | undefined,
  // The value type is based on the type in the
  ...fields: { key: string; value: string }[]
): HeadersOptions {
  if (header instanceof Headers) {
    fields.forEach(({ key, value }) => {
      header.set(key, value);
    });

    return header;
  }

  if (Array.isArray(header)) {
    return [...header, ...fields.map<[string, string]>(({ key, value }) => [key, value])];
  }

  return { ...(header || {}), ...fields.reduce((result, { key, value }) => ({ ...result, [key]: value }), {}) };
}
