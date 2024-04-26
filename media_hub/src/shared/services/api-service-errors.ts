import { StatusCodes } from 'http-status-codes';

// NOTE: This must update in case any services added here.
export type APIServicesCombinedErrors = GeneralError | MediaManagerError;

// This class is used to unify the structure of the errors
export class HttpError<T extends APIServicesCombinedErrors = APIServicesCombinedErrors> extends Error {
  constructor(public info: T) {
    super(info.message);
    this.info = info;
  }
}

// General Error
export type GeneralError = { status: false; message: string; status_code: StatusCodes };

// Media Manager service errors
interface MediaManagerCommonErrors {
  status: false;
  message: string;
}
interface MediaManagerNonFormErrors {
  status_code: Exclude<StatusCodes, StatusCodes.UNPROCESSABLE_ENTITY>;
}
interface MediaManagerFormErrors {
  status_code: StatusCodes.UNPROCESSABLE_ENTITY;
  errors: Array<{
    fieldName: string;
    errors: string[];
  }>;
}
export type MediaManagerError = MediaManagerCommonErrors & (MediaManagerNonFormErrors | MediaManagerFormErrors);
