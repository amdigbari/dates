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
    includePaths: [path.resolve(__dirname, 'src', 'shared', 'theme')],
  },
  reactStrictMode: true,

  async rewrites() {
    return [
      {
        source: '/api/media-manager/:path*',
        destination: 'http://localhost:8080/:path*',
      },
    ];
  },
};

export default withBundleAnalyzer(nextTranslate(nextConfig));
