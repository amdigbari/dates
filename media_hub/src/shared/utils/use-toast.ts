import { useContext } from 'react';

import { ToastContext, type ToastContextType } from 'src/shared/components';

export function useToast(): [ToastContextType['show'], ToastContextType['hide']] {
  const { show, hide } = useContext(ToastContext);

  return [show, hide];
}
