'use client';

import { useMutation } from '@tanstack/react-query';
import clsx from 'clsx';
import { StatusCodes } from 'http-status-codes';
import useTranslation from 'next-translate/useTranslation';
import { useRouter } from 'next/navigation';
import { omit } from 'ramda';
import { useMemo } from 'react';
import { FormControl, FormGroup, FormLabel, InputGroup } from 'react-bootstrap';
import { useForm } from 'react-hook-form';

import { Button, Form, FormInput, type FormProps, FormText } from 'src/shared/components';
import { HttpError, MediaManagerError, mutateService } from 'src/shared/services';
import {
  emailPattern,
  getCountdownTimeFromSeconds,
  getValidatedPasswordRules,
  otpCountDownTime,
  otpMaxLength,
  otpPattern,
  passwordPattern,
  useCountdown,
  useToast,
} from 'src/shared/utils';

import styles from './Form.module.scss';

interface RegisterFormType {
  email: string;
  password: string;
  confirmPassword: string;
  otp: string;
  fullName: string;
  nickname: string;
}

interface Props {
  className?: string;
}
export function RegisterForm({ className }: Props) {
  const { t } = useTranslation();

  const [showToast] = useToast();

  const router = useRouter();

  const otpTimer = useCountdown(otpCountDownTime);

  const methods = useForm<RegisterFormType>({
    mode: 'onTouched',
    defaultValues: { email: '', password: '', confirmPassword: '', otp: '', fullName: '', nickname: '' },
  });

  const { password } = methods.watch();
  const passwordValidatedRules = useMemo(() => getValidatedPasswordRules(t, password), [t, password]);

  const otpError = methods.formState.errors.otp;

  const { mutateAsync: sendOtpRequest, isPending: sendOtpLoading } = useMutation(
    mutateService('post', 'media-manager:/api/v1/auth/send-otp'),
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
    mutateService('post', 'media-manager:/api/v1/auth/register'),
  );
  async function onFormSubmit(...[values]: Parameters<FormProps<RegisterFormType>['onSubmit']>) {
    try {
      const response = await registerRequest({ body: omit(['confirmPassword'], values) });

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
        name="fullName"
        label={t('auth/register:full-name')}
        rules={{
          required: {
            value: true,
            message: t('common:form-messages.required', { field: t('auth/register:full-name') }),
          },
          minLength: {
            value: 5,
            message: t('common:form-messages.min-length', { field: t('auth/register:full-name'), length: 5 }),
          },
          maxLength: {
            value: 64,
            message: t('common:form-messages.max-length', { field: t('auth/register:full-name'), length: 64 }),
          },
        }}
        type="text"
        className="w-100 mt-4"
        helperText={t('auth/register:full-name-help')}
      />

      <FormInput
        name="nickname"
        label={t('auth/register:nickname')}
        rules={{
          required: {
            value: true,
            message: t('common:form-messages.required', { field: t('auth/register:nickname') }),
          },
          minLength: {
            value: 3,
            message: t('common:form-messages.min-length', { field: t('auth/register:nickname'), length: 3 }),
          },
          maxLength: {
            value: 20,
            message: t('common:form-messages.max-length', { field: t('auth/register:nickname'), length: 20 }),
          },
        }}
        type="text"
        className="w-100 mt-4"
        helperText={t('auth/register:nickname-help')}
      />

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
        className="w-100 mt-3"
      />

      <FormGroup className="w-100 mt-3">
        <FormLabel>{t('auth/register:otp')}</FormLabel>

        <InputGroup>
          <FormControl
            dir="ltr"
            {...methods.register('otp', {
              required: { value: true, message: t('common:form-messages.required', { field: t('auth/register:otp') }) },
              pattern: {
                value: otpPattern,
                message: t('common:form-messages.invalid', { field: t('auth/register:otp') }),
              },
            })}
            maxLength={otpMaxLength}
            isInvalid={!!otpError}
          />
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

        {!!otpError?.message && <FormText text={otpError.message} status="invalid" />}
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

      <FormInput
        name="confirmPassword"
        label={t('auth/register:confirm-password')}
        rules={{
          validate: {
            sameAsPassword: (value: string) => value === password,
          },
        }}
        dir="ltr"
        type="password"
        className="w-100 mt-3"
        // This is because the validate won't accept a text as invalid message
        error={methods.formState.errors.confirmPassword ? t('auth/register:invalid-confirm-password') : undefined}
      />

      <Button variant="primary" type="submit" className="w-100 mt-4" loading={registerLoading}>
        {t('common:auth.register')}
      </Button>
    </Form>
  );
}
