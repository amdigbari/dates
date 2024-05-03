import clsx from 'clsx';
import { useContext, useEffect } from 'react';
import { Toast as BootstrapToast, ToastBody, ToastHeader } from 'react-bootstrap';

import { TOAST_DEFAULT_DURATION } from './ToastConstants';
import { type Toast, ToastContext } from './ToastContext';

interface Props {
  toast: Toast;
}
export function ToastItem(props: Props) {
  const { id, duration = TOAST_DEFAULT_DURATION, header, body, variant } = props.toast;
  const { hide } = useContext(ToastContext);

  useEffect(() => {
    if (duration !== Infinity) {
      const timeout = setTimeout(() => hide(id), Math.max(0, duration));

      return () => clearTimeout(timeout);
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <BootstrapToast bg={variant} className="d-block my-1">
      {!!header && <ToastHeader>{typeof header === 'function' ? header() : <strong>{header}</strong>}</ToastHeader>}

      <ToastBody className={clsx({ 'text-white': variant === 'dark' })}>
        {typeof body === 'function' ? body() : <p>{body}</p>}
      </ToastBody>
    </BootstrapToast>
  );
}
