import Image from 'next/image';

import styles from './layout.module.scss';

export default function AuthLayout({ children }: LayoutProps) {
  return (
    <div className={styles['container']}>
      <div className={styles['graphics-wrapper']}>
        {/* Desktop */}
        <Image
          src="/images/auth/layout-background-desktop.jpeg"
          alt="couple at a beach"
          fill
          className="d-none d-md-block"
        />

        {/* Mobile */}
        <Image
          src="/images/auth/layout-background-mobile.jpeg"
          alt="couple at a beach"
          fill
          className="d-block d-md-none"
        />

        <div className={styles['graphics-wrapper_overlay']} />
      </div>

      <div className={styles['children-wrapper']}>
        <div className={styles['children-wrapper_children']}>{children}</div>
      </div>
    </div>
  );
}
