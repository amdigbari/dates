import Cookies from 'js-cookie';
import type { Translate } from 'next-translate';
import type { ReadonlyRequestCookies } from 'next/dist/server/web/spec-extension/adapters/request-cookies';

export const emailPattern =
  /(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])/;

// OTP Related Utils
export const otpMaxLength = 6;
export const otpPattern = new RegExp(`^\\d{${otpMaxLength}}$`);
export const otpCountDownTime = 120; // 2 minutes

// Password Related Utils
export interface PasswordRule {
  text: string;
  valid: boolean;
}
interface PasswordRuleWithValidator extends Omit<PasswordRule, 'valid'> {
  validator: RegExp;
}

export const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,20}$/;
export const passwordLengthPattern = /^.{8,20}$/;
export const passwordLowercaseCharacterPattern = /[a-z]/;
export const passwordUppercaseCharacterPattern = /[A-Z]/;
export const passwordDigitPattern = /\d/;

export function getValidatedPasswordRules(t: Translate, password: string): Array<PasswordRule> {
  const passwordRules: Array<PasswordRuleWithValidator> = [
    { text: t('auth/register:password-helpers.length'), validator: passwordLengthPattern },
    { text: t('auth/register:password-helpers.lowercase-character'), validator: passwordLowercaseCharacterPattern },
    { text: t('auth/register:password-helpers.uppercase-character'), validator: passwordUppercaseCharacterPattern },
    { text: t('auth/register:password-helpers.digit'), validator: passwordDigitPattern },
  ];

  return passwordRules.map<PasswordRule>(({ text, validator }) => ({ text, valid: !!password.match(validator) }));
}

type tokenType = 'access-token' | 'refresh-token';
export class TokenStorage {
  static set(type: tokenType, value: string) {
    Cookies.set(type, value);
  }

  static get(type: tokenType, cookies?: ReadonlyRequestCookies) {
    const _cookies = cookies || Cookies;

    const result = _cookies.get(type);

    return typeof result === 'string' ? result : result?.value;
  }

  static delete(type: tokenType) {
    Cookies.remove(type);
  }

  static empty() {
    Cookies.remove('access-token');
    Cookies.remove('refresh-token');
  }
}
