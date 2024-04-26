import * as exportedSpacings from './spacings.module.scss';

// Complete this as needed
interface Spacings {
  spacer_1: string;
  spacer_2: string;
  spacer_3: string;
  spacer_4: string;
  spacer_5: string;
}

export const themeSpacings = exportedSpacings as unknown as Spacings;
