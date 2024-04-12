import useTranslation from 'next-translate/useTranslation';

export default function Home() {
  const { t } = useTranslation('common');

  return (
    <div>
      <h2>{t('hello-world')}</h2>
    </div>
  );
}
