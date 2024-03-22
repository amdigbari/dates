import { Box, Container, Typography } from '@mui/material';
import useTranslation from 'next-translate/useTranslation';

import type { MaterialSxProps } from 'src/types';

export default function Home() {
  const { t } = useTranslation('common');

  return (
    <Container maxWidth="lg">
      <Box sx={box}>
        <Typography variant="h4" component="h1" sx={typography}>
          {t('hello-world')}
        </Typography>
      </Box>
    </Container>
  );
}

// Styles
const box: MaterialSxProps = {
  my: 4,
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'center',
  alignItems: 'center',
};

const typography: MaterialSxProps = { mb: 2 };
