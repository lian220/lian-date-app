/**
 * 익명 세션 ID 관리
 * - UUID 기반 세션 ID 생성
 * - localStorage에 영구 저장
 * - 최초 생성 후 재사용
 */

const SESSION_ID_KEY = "dateclick-session-id";

/**
 * UUID v4 생성
 */
function generateUUID(): string {
  return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0;
    const v = c === "x" ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}

/**
 * 세션 ID 가져오기 (없으면 생성)
 */
export function getSessionId(): string {
  if (typeof window === "undefined") {
    // SSR 환경에서는 임시 ID 반환
    return "ssr-session";
  }

  let sessionId = localStorage.getItem(SESSION_ID_KEY);

  if (!sessionId) {
    sessionId = generateUUID();
    localStorage.setItem(SESSION_ID_KEY, sessionId);
  }

  return sessionId;
}

/**
 * 세션 ID 초기화 (테스트용)
 */
export function clearSessionId(): void {
  if (typeof window !== "undefined") {
    localStorage.removeItem(SESSION_ID_KEY);
  }
}
