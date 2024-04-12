import Image from 'next/image';

import styles from './layout.module.scss';

export default function AuthLayout({ children }: LayoutProps) {
  return (
    <div className={styles['container']}>
      <div className={styles['graphics-wrapper']}>
        <Image src="/images/layout-background.jpeg" alt="couple at beach" fill />
      </div>

      <div className={styles['overlay']} />
      <div className={styles['children-wrapper']}>
        <div className={styles['children-wrapper_children']}>{children}</div>
      </div>
    </div>
  );
}
