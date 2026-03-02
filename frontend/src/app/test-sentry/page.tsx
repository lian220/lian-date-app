'use client';

import * as Sentry from '@sentry/nextjs';

export default function TestSentryPage() {
  const triggerFrontendError = () => {
    throw new Error('[Sentry 테스트] 프론트엔드 에러 강제 발생');
  };

  const triggerCapturedError = () => {
    try {
      throw new Error('[Sentry 테스트] captureException 테스트');
    } catch (e) {
      Sentry.captureException(e);
      alert('Sentry에 에러 전송 완료 (콘솔 확인)');
    }
  };

  const triggerBackendError = async () => {
    const apiUrl = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080';
    const res = await fetch(`${apiUrl}/test/error/backend`);
    alert(`백엔드 응답: ${res.status}`);
  };

  return (
    <div className="p-8 flex flex-col gap-4 max-w-md">
      <h1 className="text-xl font-bold">Sentry 테스트</h1>
      <button
        onClick={triggerFrontendError}
        className="bg-red-500 text-white px-4 py-2 rounded"
      >
        프론트 Unhandled Error (ErrorBoundary 캡처)
      </button>
      <button
        onClick={triggerCapturedError}
        className="bg-orange-500 text-white px-4 py-2 rounded"
      >
        프론트 captureException
      </button>
      <button
        onClick={triggerBackendError}
        className="bg-purple-500 text-white px-4 py-2 rounded"
      >
        백엔드 에러 + Slack 알림
      </button>
    </div>
  );
}
