import type { FieldPath, FieldValues, UseControllerProps } from 'react-hook-form';

export interface FormFieldCommonProps<
  TFieldValues extends FieldValues = FieldValues,
  TName extends FieldPath<TFieldValues> = FieldPath<TFieldValues>,
> extends Omit<UseControllerProps<TFieldValues, TName>, 'control' | 'shouldUnregister'> {
  label: string;

  helperText?: string;
  error?: string;
}

export type FieldState = 'valid' | 'none' | 'invalid';
