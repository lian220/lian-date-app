"use client";

import { useState, useEffect } from "react";
import type { Rating, RatingWidgetProps } from "@/types/rating";
import { getSessionId } from "@/lib/sessionManager";

const EMOJI_RATINGS = [
  { score: 1, emoji: "😞", label: "별로예요" },
  { score: 2, emoji: "😐", label: "그저 그래요" },
  { score: 3, emoji: "🙂", label: "괜찮아요" },
  { score: 4, emoji: "😊", label: "좋아요" },
  { score: 5, emoji: "🥰", label: "최고예요" },
];

const STORAGE_KEY_PREFIX = "rating-";

export default function RatingWidget({
  courseId,
  onRatingComplete,
}: RatingWidgetProps) {
  const [rating, setRating] = useState<Rating | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 페이지 로드 시 이미 평가했는지 확인 (localStorage)
  useEffect(() => {
    const storageKey = `${STORAGE_KEY_PREFIX}${courseId}`;
    const savedRating = localStorage.getItem(storageKey);
    if (savedRating) {
      try {
        setRating(JSON.parse(savedRating));
      } catch {
        localStorage.removeItem(storageKey);
      }
    }
  }, [courseId]);

  const handleRate = async (score: number) => {
    if (rating || isSubmitting) return;

    setIsSubmitting(true);
    setError(null);

    try {
      const sessionId = getSessionId();
      const response = await fetch(`/api/courses/${courseId}/ratings`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ score, sessionId }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "평가 제출에 실패했습니다");
      }

      const data = await response.json();
      const newRating: Rating = data.data;

      setRating(newRating);

      // localStorage에 저장 (1회 평가 제한)
      const storageKey = `${STORAGE_KEY_PREFIX}${courseId}`;
      localStorage.setItem(storageKey, JSON.stringify(newRating));

      onRatingComplete?.(newRating);
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "평가 제출에 실패했습니다"
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  // 평가 완료 상태 UI (LAD-47)
  if (rating) {
    const selectedEmoji = EMOJI_RATINGS.find((r) => r.score === rating.score);

    return (
      <div className="mt-8 rounded-lg border border-green-200 bg-green-50 p-6 text-center">
        <p className="mb-3 text-lg font-semibold text-green-800">
          소중한 의견 감사합니다! 🎉
        </p>
        <div className="flex items-center justify-center gap-2">
          <span className="text-4xl">{selectedEmoji?.emoji}</span>
          <span className="text-gray-700">{selectedEmoji?.label}</span>
        </div>
      </div>
    );
  }

  // 평가 UI (LAD-46)
  return (
    <div className="mt-8 rounded-lg border border-gray-200 bg-white p-6">
      <h3 className="mb-4 text-center text-lg font-semibold text-gray-900">
        이 추천이 마음에 드시나요?
      </h3>

      <div className="flex justify-center gap-3">
        {EMOJI_RATINGS.map(({ score, emoji, label }) => (
          <button
            key={score}
            onClick={() => handleRate(score)}
            disabled={isSubmitting}
            className="group flex flex-col items-center gap-1 rounded-lg p-3 transition-all hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
            aria-label={`${score}점: ${label}`}
          >
            <span className="text-4xl transition-transform group-hover:scale-110">
              {emoji}
            </span>
            <span className="text-xs text-gray-600">{label}</span>
          </button>
        ))}
      </div>

      {error && (
        <p className="mt-4 text-center text-sm text-red-600">{error}</p>
      )}

      {isSubmitting && (
        <p className="mt-4 text-center text-sm text-gray-500">
          평가를 저장하는 중...
        </p>
      )}
    </div>
  );
}
