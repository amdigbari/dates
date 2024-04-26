import { Form as BootstrapForm, type FormProps as BootstrapFormProps } from 'react-bootstrap';
import { type FieldValues, FormProvider, type FormProviderProps, type UseFormHandleSubmit } from 'react-hook-form';

export interface FormProps<
  TFieldValues extends FieldValues,
  TContext = any,
  TTransformedValues extends FieldValues | undefined = undefined,
> extends Omit<BootstrapFormProps, 'onSubmit' | 'noValidate'> {
  providerProps: Omit<FormProviderProps<TFieldValues, TContext, TTransformedValues>, 'children'>;
  onSubmit: Parameters<UseFormHandleSubmit<TFieldValues, TTransformedValues>>[0];
  onInvalidSubmit?: Parameters<UseFormHandleSubmit<TFieldValues, TTransformedValues>>[1];
}
export function Form<
  TFieldValues extends FieldValues,
  TContext = any,
  TTransformedValues extends FieldValues | undefined = undefined,
>({
  children,
  onSubmit,
  onInvalidSubmit,
  providerProps,
  ...restProps
}: FormProps<TFieldValues, TContext, TTransformedValues>) {
  return (
    <FormProvider {...providerProps}>
      <BootstrapForm {...restProps} noValidate onSubmit={providerProps.handleSubmit(onSubmit, onInvalidSubmit)}>
        {children}
      </BootstrapForm>
    </FormProvider>
  );
}
