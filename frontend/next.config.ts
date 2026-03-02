import type { NextConfig } from "next";
import { withSentryConfig } from "@sentry/nextjs";

const nextConfig: NextConfig = {
  output: "standalone", // Docker 프로덕션 배포를 위한 standalone 빌드
};

export default withSentryConfig(nextConfig, {
  org: process.env.SENTRY_ORG ?? "sfn-oh",
  project: process.env.SENTRY_PROJECT ?? "lian-date-app-frontend",
  // CI 환경에서만 source maps 업로드 (SENTRY_AUTH_TOKEN 필요)
  silent: !process.env.CI,
  widenClientFileUpload: true,
  // 클라이언트 번들에서 source maps 숨김 (보안)
  hideSourceMaps: true,
  disableLogger: true,
  automaticVercelMonitors: false,
});
