import useTranslation from 'next-translate/useTranslation';

import { LoginForm } from 'src/components';

export default function Login() {
  const { t } = useTranslation('common');

  return (
    <div className="w-100">
      <h3 className="text-center">{t('auth.login')}</h3>

      <LoginForm />
    </div>
  );
}
