import { StatusCodes } from 'http-status-codes';
import createClient, { type ClientMethod, type FetchOptions, type FetchResponse } from 'openapi-fetch';
import type { FilterKeys, HttpMethod, PathsWithMethod as PathsWith } from 'openapi-typescript-helpers';

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
  ...[method, url, options]: ClientFetchParams<M, P>
): Promise<HttpResponseData<M, P>> {
  try {
    const { data, error, response } = (await (
      client[method.toUpperCase() as Uppercase<M>] as ClientMethod<any, HttpMethod>
    )(getRequestPath(url), options)) as OpenapiResponse<M, P>;

    if (error) {
      if (typeof error === 'string') {
        throw new HttpError({ status: false, message: error, status_code: response.status });
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
