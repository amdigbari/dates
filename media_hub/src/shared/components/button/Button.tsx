import useTranslation from 'next-translate/useTranslation';
import { type ForwardedRef, forwardRef, useMemo } from 'react';
import { Button as BootstrapButton, type ButtonProps as BootstrapButtonProps, Spinner } from 'react-bootstrap';

export interface ButtonProps extends Omit<BootstrapButtonProps, 'children' | 'variant'> {
  loading?: boolean;
  children: NonNullable<BootstrapButtonProps['children']>;
  spinnerVariant?: ColorVariant;
  variant:
    | ColorVariant
    | 'link'
    | 'outline-primary'
    | 'outline-secondary'
    | 'outline-success'
    | 'outline-danger'
    | 'outline-warning'
    | 'outline-info'
    | 'outline-dark'
    | 'outline-light';
}
export const Button = forwardRef(function ForwardedRefButton(
  { children, variant: buttonVariant, spinnerVariant, loading = false, ...restProps }: ButtonProps,
  ref: ForwardedRef<HTMLButtonElement>,
) {
  const { t } = useTranslation();

  const spinnerFinalVariant = useMemo<ColorVariant>(() => {
    if (spinnerVariant) {
      return spinnerVariant;
    }

    if (buttonVariant === 'light') {
      return 'dark';
    }

    if (buttonVariant.startsWith('outline')) {
      return buttonVariant.replace('outline-', '') as ColorVariant;
    }

    if (buttonVariant === 'link') {
      return 'primary';
    }

    return 'light';
  }, [buttonVariant, spinnerVariant]);

  return (
    <BootstrapButton variant={buttonVariant} {...restProps} ref={ref}>
      {loading ? (
        <>
          <Spinner
            variant={spinnerFinalVariant}
            as="span"
            size="sm"
            animation="border"
            role="status"
            aria-hidden="true"
          />
          <span className="visually-hidden">{t('common:loading')}</span>
        </>
      ) : (
        children
      )}
    </BootstrapButton>
  );
});
