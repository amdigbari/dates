'use client';

import { useMutation } from '@tanstack/react-query';
import clsx from 'clsx';
import { StatusCodes } from 'http-status-codes';
import useTranslation from 'next-translate/useTranslation';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';

import { Button, Form, FormInput, type FormProps } from 'src/shared/components';
import { HttpError, MediaManagerError, mutateService } from 'src/shared/services';
import { emailPattern, passwordPattern, useToast } from 'src/shared/utils';

interface LoginFormType {
  email: string;
  password: string;
}

interface Props {
  className?: string;
}
export function LoginForm({ className }: Props) {
  const { t } = useTranslation();

  const [showToast] = useToast();

  const router = useRouter();

  const methods = useForm<LoginFormType>({ mode: 'onTouched', defaultValues: { email: '', password: '' } });

  const { mutateAsync: loginRequest, isPending: loginLoading } = useMutation(
    mutateService('post', 'media-manager:/api/v1/auth/login'),
  );
  async function onFormSubmit(...[values]: Parameters<FormProps<LoginFormType>['onSubmit']>) {
    try {
      const response = await loginRequest({ body: values });

      if (response.status) {
        showToast({ body: t('auth/login:login-successfully'), variant: 'success' });
        router.push('/');
      }
    } catch (error) {
      if (error instanceof HttpError) {
        const _errorInfo = error.info as MediaManagerError;
        if (_errorInfo.status_code === StatusCodes.UNPROCESSABLE_ENTITY && _errorInfo.errors.length) {
          _errorInfo.errors.forEach((e) => {
            methods.setError(e.fieldName as keyof LoginFormType, { message: e.errors.join('. ') });
          });
        } else {
          showToast({ body: _errorInfo.message, variant: 'danger' });
        }
      }
    }
  }

  return (
    <Form providerProps={methods} onSubmit={onFormSubmit} className={clsx('w-100', className)}>
      <FormInput
        name="email"
        label={t('common:auth.email')}
        rules={{
          required: { value: true, message: t('common:form-messages.required', { field: t('common:auth.email') }) },
          pattern: {
            value: emailPattern,
            message: t('common:form-messages.invalid', { field: t('common:auth.email') }),
          },
        }}
        dir="ltr"
        type="email"
        className="w-100"
      />

      <FormInput
        name="password"
        label={t('common:auth.password')}
        rules={{
          required: {
            value: true,
            message: t('common:form-messages.required', { field: t('common:auth.password') }),
          },
          pattern: {
            value: passwordPattern,
            message: t('common:form-messages.invalid', { field: t('common:auth.password') }),
          },
        }}
        dir="ltr"
        type="password"
        className="w-100 mt-3"
      />

      <Button variant="primary" type="submit" className="w-100 mt-4" loading={loginLoading}>
        {t('common:auth.login')}
      </Button>
    </Form>
  );
}
