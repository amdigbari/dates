import { type ForwardedRef, forwardRef, useMemo } from 'react';
import { FormControl, type FormControlProps, FormGroup, FormLabel } from 'react-bootstrap';
import { type FieldPath, type FieldValues, useController, useFormContext } from 'react-hook-form';

import type { FieldState, FormFieldCommonProps } from 'src/shared/types';

import { FormText } from './FormText';

interface Props<
  TFieldValues extends FieldValues = Record<string, string>,
  TName extends FieldPath<TFieldValues> = FieldPath<TFieldValues>,
> extends FormFieldCommonProps<TFieldValues, TName>,
    Omit<FormControlProps, 'name' | 'value' | 'onChange' | 'defaultValue'> {
  placeholder?: string;
  changeMiddleware?: (value: string) => string;
  hideError?: boolean;
}
export const FormInput = forwardRef(function ForwardedRefFormInput<
  TFieldValues extends FieldValues = Record<string, string>,
  TName extends FieldPath<TFieldValues> = FieldPath<TFieldValues>,
>(props: Props<TFieldValues, TName>, ref: ForwardedRef<HTMLDivElement>) {
  const {
    name,
    rules,
    defaultValue,
    disabled,

    label,
    helperText,
    error: propsError,
    hideError = false,

    changeMiddleware,
    onBlur: propsBlur,

    className,

    ...formControlProps
  } = props;

  const { control } = useFormContext<TFieldValues>();
  const {
    field: { value: fieldValue, ...field },
    fieldState: { error: formError },
  } = useController<TFieldValues, TName>({ name, control, rules, defaultValue, disabled });

  const fieldError = useMemo(() => propsError || formError?.message, [formError, propsError]);

  const fieldState = useMemo<FieldState>(() => {
    return fieldError ? 'invalid' : 'none';
  }, [fieldError]);

  function onChange(
    event: Parameters<NonNullable<FormControlProps['onChange']>>[0],
  ): ReturnType<NonNullable<FormControlProps['onChange']>> {
    const value = event.target.value;
    const alteredValue = changeMiddleware ? changeMiddleware(value) : value;

    field.onChange(alteredValue);
  }

  function onBlur(
    ...params: Parameters<NonNullable<FormControlProps['onBlur']>>
  ): ReturnType<NonNullable<FormControlProps['onBlur']>> {
    field.onBlur();

    propsBlur?.(...params);
  }

  return (
    <FormGroup className={className} ref={ref}>
      <FormLabel>{label}</FormLabel>
      <FormControl
        {...formControlProps}
        {...field}
        value={fieldValue}
        onChange={onChange}
        onBlur={onBlur}
        isInvalid={fieldState === 'invalid'}
      />

      {/* Show error only in case hideError is false */}
      {!!((!hideError && fieldError) || helperText) && (
        <FormText text={(!hideError && fieldError) || helperText || ''} status={fieldState} />
      )}
    </FormGroup>
  );
});
