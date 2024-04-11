import * as exportedColors from './colors.module.scss';

// Complete this as needed
interface Colors {
  // Theme colors
  primary: string;

  // Helper colors
  background: string;

  // Blues
  blue_100: string;
}

export const themeColors = exportedColors as unknown as Colors;
