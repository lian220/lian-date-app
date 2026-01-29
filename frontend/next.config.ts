import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "standalone", // Docker 프로덕션 배포를 위한 standalone 빌드
};

export default nextConfig;
