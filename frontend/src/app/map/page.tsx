'use client';

import { useState } from 'react';
import KakaoMapComponent from '@/components/KakaoMap';
import type { MapPlace, KakaoMap } from '@/types/kakao-map';

/**
 * 지도 페이지
 *
 * 카카오맵 컴포넌트를 테스트하는 페이지입니다.
 * - 샘플 장소 표시
 * - 마커 클릭 이벤트 처리
 * - 지도 제어 기능 테스트
 */
export default function MapPage() {
  // 샘플 장소 데이터
  const [places] = useState<MapPlace[]>([
    {
      id: 'place-1',
      name: '서울시청',
      lat: 37.5665,
      lng: 126.978,
      address: '서울 중구 세종대로 110',
      category: '시청',
    },
    {
      id: 'place-2',
      name: '롯데월드',
      lat: 37.5112,
      lng: 127.1001,
      address: '서울 송파구 올림픽로 240',
      category: '테마파크',
    },
    {
      id: 'place-3',
      name: '남산타워',
      lat: 37.5527,
      lng: 126.9882,
      address: '서울 용산구 남산공원길 105',
      category: '관광지',
    },
    {
      id: 'place-4',
      name: '강남역',
      lat: 37.4979,
      lng: 127.0276,
      address: '서울 강남구 강남역로 1',
      category: '역',
    },
    {
      id: 'place-5',
      name: '경복궁',
      lat: 37.5789,
      lng: 126.977,
      address: '서울 종로구 사직로 161',
      category: '궁궐',
    },
  ]);

  const [selectedPlace, setSelectedPlace] = useState<MapPlace | null>(null);
  const [mapInstance, setMapInstance] = useState<KakaoMap | null>(null);

  const handleMapReady = (map: KakaoMap) => {
    console.log('Map is ready', map);
    setMapInstance(map);
  };

  const handleMarkerClick = (place: MapPlace) => {
    console.log('Marker clicked:', place);
    setSelectedPlace(place);
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      {/* 헤더 */}
      <header className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-6 py-4">
          <h1 className="text-2xl font-bold text-gray-900">데이트 장소 지도</h1>
          <p className="text-sm text-gray-600 mt-1">
            서울의 주요 데이트 장소를 확인해보세요
          </p>
        </div>
      </header>

      {/* 메인 컨텐츠 */}
      <div className="flex-1 max-w-7xl w-full mx-auto px-6 py-8 grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* 지도 */}
        <div className="lg:col-span-2">
          <div className="bg-white rounded-lg shadow-md overflow-hidden">
            <KakaoMapComponent
              places={places}
              center={{ lat: 37.5665, lng: 126.978 }}
              zoom={5}
              onMapReady={handleMapReady}
              onMarkerClick={handleMarkerClick}
            />
          </div>
        </div>

        {/* 사이드바 */}
        <aside className="space-y-6">
          {/* 장소 목록 */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">
              장소 목록 ({places.length}개)
            </h2>
            <div className="space-y-2 max-h-96 overflow-y-auto">
              {places.map((place) => (
                <button
                  key={place.id}
                  onClick={() => setSelectedPlace(place)}
                  className={`
                    w-full text-left p-3 rounded-lg transition-colors
                    ${
                      selectedPlace?.id === place.id
                        ? 'bg-blue-50 border-2 border-blue-500'
                        : 'bg-gray-50 border-2 border-transparent hover:bg-gray-100'
                    }
                  `}
                >
                  <p className="font-medium text-gray-900">{place.name}</p>
                  <p className="text-xs text-gray-500 mt-1">{place.category}</p>
                </button>
              ))}
            </div>
          </div>

          {/* 선택된 장소 정보 */}
          {selectedPlace && (
            <div className="bg-blue-50 rounded-lg shadow-md p-6 border-l-4 border-blue-500">
              <h3 className="text-lg font-semibold text-gray-900 mb-3">
                {selectedPlace.name}
              </h3>
              <div className="space-y-2 text-sm">
                <div>
                  <p className="text-gray-600">주소</p>
                  <p className="text-gray-900 font-medium">{selectedPlace.address}</p>
                </div>
                <div>
                  <p className="text-gray-600">좌표</p>
                  <p className="text-gray-900 font-medium">
                    {selectedPlace.lat.toFixed(4)}, {selectedPlace.lng.toFixed(4)}
                  </p>
                </div>
                <div>
                  <p className="text-gray-600">카테고리</p>
                  <p className="text-gray-900 font-medium">{selectedPlace.category}</p>
                </div>
              </div>
            </div>
          )}

          {/* 지도 정보 */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">지도 기능</h3>
            <ul className="space-y-2 text-sm text-gray-700">
              <li className="flex items-start">
                <span className="text-blue-500 mr-2">✓</span>
                <span>줌 인/아웃으로 지도 확대/축소</span>
              </li>
              <li className="flex items-start">
                <span className="text-blue-500 mr-2">✓</span>
                <span>마커 클릭으로 장소 정보 확인</span>
              </li>
              <li className="flex items-start">
                <span className="text-blue-500 mr-2">✓</span>
                <span>드래그로 지도 이동</span>
              </li>
              <li className="flex items-start">
                <span className="text-blue-500 mr-2">✓</span>
                <span>모든 마커를 포함하도록 자동 조정</span>
              </li>
            </ul>
          </div>
        </aside>
      </div>
    </div>
  );
}
