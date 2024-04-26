import clsx from 'clsx';
import { FormText as BootstrapFormText } from 'react-bootstrap';

import { FieldState } from 'src/shared/types';

interface Props {
  text: string;
  status?: FieldState;
}
export function FormText({ text, status = 'none' }: Props) {
  return (
    <p className="ms-2">
      <i
        className={clsx('bi small me-2', {
          'text-success bi-check-lg': status === 'valid',
          'text-danger bi-exclamation-triangle': status === 'invalid',
          'bi-exclamation-triangle': status === 'none',
        })}
      />

      <BootstrapFormText
        className={clsx({
          'text-success': status === 'valid',
          'text-danger': status === 'invalid',
        })}
      >
        {text}
      </BootstrapFormText>
    </p>
  );
}
