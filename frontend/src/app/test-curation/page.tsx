'use client';

import { useState } from 'react';
import { fetchPlaceCuration } from '@/lib/api';
import { PlaceCurationInfo } from '@/types/place';

export default function TestCurationPage() {
  const [placeId, setPlaceId] = useState('');
  const [curation, setCuration] = useState<PlaceCurationInfo | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleFetch = async () => {
    if (!placeId.trim()) {
      setError('장소 ID를 입력해주세요');
      return;
    }

    setLoading(true);
    setError(null);
    setCuration(null);

    try {
      const result = await fetchPlaceCuration(placeId.trim());
      setCuration(result);
    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : '큐레이션 조회에 실패했습니다';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-2xl mx-auto">
        <h1 className="text-3xl font-bold mb-8">장소 큐레이션 테스트</h1>

        <div className="bg-white p-6 rounded-lg shadow mb-6">
          <label htmlFor="place-id" className="block text-sm font-medium text-gray-700 mb-2">
            장소 ID (카카오 장소 ID)
          </label>
          <div className="flex gap-2">
            <input
              id="place-id"
              type="text"
              value={placeId}
              onChange={(e) => setPlaceId(e.target.value)}
              placeholder="예: 1234567890"
              className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
            <button
              onClick={handleFetch}
              disabled={loading}
              className="px-6 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 disabled:bg-gray-300"
            >
              {loading ? '조회 중...' : '조회'}
            </button>
          </div>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded-lg mb-6">
            <p>{error}</p>
          </div>
        )}

        {curation && (
          <div className="bg-white p-6 rounded-lg shadow">
            <h2 className="text-2xl font-bold mb-4">큐레이션 결과</h2>
            <div className="space-y-4">
              <div>
                <span className="text-sm text-gray-500">데이트 적합도</span>
                <div className="text-2xl font-bold">{curation.dateScore}/10</div>
              </div>
              <div>
                <span className="text-sm text-gray-500">분위기</span>
                <div className="flex gap-2 mt-1">
                  {curation.moodTags.map((tag, i) => (
                    <span key={i} className="px-3 py-1 bg-purple-100 text-purple-700 rounded-full">
                      {tag}
                    </span>
                  ))}
                </div>
              </div>
              <div>
                <span className="text-sm text-gray-500">가격대</span>
                <p className="text-lg font-semibold">{curation.priceRange.toLocaleString()}원</p>
              </div>
              <div>
                <span className="text-sm text-gray-500">추천 시간</span>
                <p className="text-lg">{curation.bestTime}</p>
              </div>
              <div>
                <span className="text-sm text-gray-500">추천 이유</span>
                <p className="mt-1">{curation.recommendation}</p>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
