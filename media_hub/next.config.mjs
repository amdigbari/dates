import NextBundleAnalyzer from '@next/bundle-analyzer';
import nextTranslate from 'next-translate-plugin';
import path, { dirname } from 'path';
import { fileURLToPath } from 'url';

const __dirname = dirname(fileURLToPath(import.meta.url));

const withBundleAnalyzer = NextBundleAnalyzer({
  enabled: process.env.BUNDLE_ANALYZE === 'true',
});

/** @type {import('next').NextConfig} */
const nextConfig = {
  output: 'standalone',
  sassOptions: {
    includePaths: [path.resolve(__dirname, 'src', 'theme')],
  },
  reactStrictMode: true,
};

export default withBundleAnalyzer(nextTranslate(nextConfig));
