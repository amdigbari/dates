import Cookies from 'js-cookie';
import { useEffect, useState } from 'react';

type Locales = 'fa-IR' | 'en-US';
export const ALL_LOCALES: Locales[] = ['fa-IR', 'en-US'];

// Based on next-translate settings. https://github.com/aralroca/next-translate?tab=readme-ov-file#11-how-to-save-the-user-defined-language
const LOCALE_COOKIE_KEY = 'NEXT_LOCALE';

export class LocaleService {
  static DEFAULT_LOCALE: Locales = 'fa-IR';

  static set(value: Locales) {
    Cookies.set(LOCALE_COOKIE_KEY, value);
  }

  static get() {
    const locale = Cookies.get(LOCALE_COOKIE_KEY) as Locales | undefined;

    return locale && ALL_LOCALES.includes(locale) ? locale : this.DEFAULT_LOCALE;
  }

  static getCookiesLocale() {
    return Cookies.get(LOCALE_COOKIE_KEY);
  }

  static empty() {
    Cookies.remove(LOCALE_COOKIE_KEY);
  }
}

export function useLocaleState() {
  const [locale, setLocale] = useState(LocaleService.get());

  //   Set Locale to Cookies if not set yet
  useEffect(() => {
    const cookieLocale = LocaleService.getCookiesLocale();

    if (!cookieLocale) {
      LocaleService.set(LocaleService.DEFAULT_LOCALE);
      setLocale(LocaleService.DEFAULT_LOCALE);
    }
  }, []);

  return [locale, setLocale];
}
