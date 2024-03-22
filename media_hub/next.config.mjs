import NextBundleAnalyzer from '@next/bundle-analyzer';
import nextTranslate from 'next-translate-plugin';

const withBundleAnalyzer = NextBundleAnalyzer({
  enabled: process.env.BUNDLE_ANALYZE === 'true',
});

/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  compiler: { emotion: true },
};

export default withBundleAnalyzer(nextTranslate(nextConfig));
