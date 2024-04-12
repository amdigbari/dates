'use client';

import { useMutation } from '@tanstack/react-query';
import clsx from 'clsx';
import { StatusCodes } from 'http-status-codes';
import useTranslation from 'next-translate/useTranslation';
import { useRouter } from 'next/navigation';
import { useMemo } from 'react';
import { FormControl, FormGroup, FormLabel, InputGroup } from 'react-bootstrap';
import { useForm } from 'react-hook-form';

import { Button, Form, FormInput, type FormProps, FormText } from 'src/components';
import { HttpError, MediaManagerError, mutateService } from 'src/services';
import {
  emailPattern,
  getCountdownTimeFromSeconds,
  getValidatedPasswordRules,
  otpCountDownTime,
  otpPattern,
  passwordPattern,
  useCountdown,
  useToast,
} from 'src/utils';

import styles from './RegisterForm.module.scss';

interface RegisterFormType {
  email: string;
  password: string;
  otp: string;
}

interface Props {
  className?: string;
}
export function RegisterForm({ className }: Props) {
  const { t } = useTranslation();

  const [showToast] = useToast();

  const router = useRouter();

  const otpTimer = useCountdown(otpCountDownTime);

  const methods = useForm<RegisterFormType>({ mode: 'onTouched', defaultValues: { email: '', password: '', otp: '' } });
  const { password } = methods.watch();

  const passwordValidatedRules = useMemo(() => getValidatedPasswordRules(t, password), [t, password]);

  const { mutateAsync: sendOtpRequest, isPending: sendOtpLoading } = useMutation(
    mutateService('post', 'media-manager:/api/auth/send-otp'),
  );
  async function sendOtp() {
    const { email } = methods.getValues();

    const isEmailValid = await methods.trigger('email');
    if (isEmailValid) {
      try {
        const response = await sendOtpRequest({ body: { email } });

        if (response.status) {
          otpTimer.start();
        }
      } catch (error) {
        if (error instanceof HttpError) {
          const _errorInfo = error.info as MediaManagerError;
          if (_errorInfo.status_code === StatusCodes.UNPROCESSABLE_ENTITY) {
            _errorInfo.errors.forEach((e) => {
              methods.setError(e.fieldName as keyof RegisterFormType, { message: e.errors.join('. ') });
            });
          } else {
            showToast({ body: _errorInfo.message, variant: 'danger' });
          }
        }
      }
    }
  }

  const { mutateAsync: registerRequest, isPending: registerLoading } = useMutation(
    mutateService('post', 'media-manager:/api/auth/register'),
  );
  async function onFormSubmit(...[values]: Parameters<FormProps<RegisterFormType>['onSubmit']>) {
    try {
      const response = await registerRequest({ body: values });

      if (response.status) {
        showToast({ body: t('auth/register:register-successfully'), variant: 'success' });
        router.push('/');
      }
    } catch (error) {
      if (error instanceof HttpError) {
        const _errorInfo = error.info as MediaManagerError;
        if (_errorInfo.status_code === StatusCodes.UNPROCESSABLE_ENTITY) {
          _errorInfo.errors.forEach((e) => {
            methods.setError(e.fieldName as keyof RegisterFormType, { message: e.errors.join('. ') });
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

      <FormGroup className="w-100 mt-3">
        <FormLabel>{t('auth/register:otp')}</FormLabel>

        <InputGroup>
          <FormControl dir="ltr" {...methods.register('otp', { required: true, pattern: otpPattern })} />
          <Button
            type="button"
            variant="outline-secondary"
            onClick={sendOtp}
            disabled={otpTimer.counting}
            className={styles['otp-button']}
            loading={sendOtpLoading}
          >
            {otpTimer.counting ? getCountdownTimeFromSeconds(otpTimer.time) : t('auth/register:get-otp')}
          </Button>
        </InputGroup>
      </FormGroup>

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
        helperText={t('auth/register:password-helpers.note')}
        hideError
      />
      {passwordValidatedRules.map((rule) => (
        <div key={rule.text} className="ms-2">
          <FormText text={rule.text} status={rule.valid ? 'valid' : 'invalid'} />
        </div>
      ))}

      <Button variant="primary" type="submit" className="w-100 mt-4" loading={registerLoading}>
        {t('common:auth.register')}
      </Button>
    </Form>
  );
}
