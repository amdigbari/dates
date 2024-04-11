import useTranslation from 'next-translate/useTranslation';

import { RegisterForm } from 'src/components';

export default function Register() {
  const { t } = useTranslation('common');

  return (
    <div className="w-100">
      <h3 className="text-center">{t('auth.register')}</h3>

      <RegisterForm />
    </div>
  );
}
