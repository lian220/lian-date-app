'use client';

import { useEffect, useRef, useState } from 'react';
import type { KakaoMap, Marker, MapPlace, LatLng } from '@/types/kakao-map';

interface KakaoMapProps {
  places: MapPlace[];
  center?: { lat: number; lng: number };
  zoom?: number;
  onMapReady?: (map: KakaoMap) => void;
  onMarkerClick?: (place: MapPlace) => void;
}

const KAKAO_MAP_API_KEY = process.env.NEXT_PUBLIC_KAKAO_MAP_API_KEY;

/**
 * 카카오맵 컴포넌트
 *
 * Features:
 * - 카카오맵 SDK 연동
 * - 지도 렌더링
 * - 마커 표시
 * - 줌 인/아웃 컨트롤
 * - 드래그 이동 (기본 지원)
 * - 지도 영역 자동 맞춤
 */
export default function KakaoMapComponent({
  places,
  center = { lat: 37.5665, lng: 126.978 }, // 서울 중심
  zoom = 3,
  onMapReady,
  onMarkerClick,
}: KakaoMapProps) {
  const mapContainer = useRef<HTMLDivElement>(null);
  const map = useRef<KakaoMap | null>(null);
  const markers = useRef<Map<string, Marker>>(new Map());
  const [isMapReady, setIsMapReady] = useState(false);
  const [currentZoom, setCurrentZoom] = useState(zoom);

  // 카카오맵 SDK 로드
  useEffect(() => {
    if (!mapContainer.current || map.current) return;

    const script = document.createElement('script');
    script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${KAKAO_MAP_API_KEY}&libraries=drawing,services`;
    script.async = true;

    script.onload = () => {
      if (!window.kakao) {
        console.error('Kakao Maps SDK failed to load');
        return;
      }

      // 지도 초기화
      const mapOption = {
        center: new window.kakao.maps.LatLng(center.lat, center.lng),
        level: zoom,
        draggable: true,
        scrollwheel: true,
      };

      map.current = new window.kakao.maps.Map(mapContainer.current!, mapOption);

      // 지도 레벨 변경 이벤트
      window.kakao.maps.event.addListener(map.current, 'zoom_changed', () => {
        if (map.current) {
          setCurrentZoom(map.current.getLevel());
        }
      });

      setIsMapReady(true);
      onMapReady?.(map.current);
    };

    script.onerror = () => {
      console.error('Failed to load Kakao Maps SDK');
    };

    document.head.appendChild(script);

    return () => {
      if (script.parentNode) {
        script.parentNode.removeChild(script);
      }
    };
  }, [KAKAO_MAP_API_KEY, center, zoom, onMapReady]);

  // 마커 업데이트
  useEffect(() => {
    if (!isMapReady || !map.current || !window.kakao) return;

    // 기존 마커 제거
    markers.current.forEach((marker) => {
      marker.setMap(null);
    });
    markers.current.clear();

    if (places.length === 0) return;

    // 새 마커 추가
    const bounds = new window.kakao.maps.LatLngBounds();

    places.forEach((place) => {
      const position = new window.kakao.maps.LatLng(place.lat, place.lng);
      const markerOptions = {
        position,
        map: map.current,
        title: place.name,
      };

      const marker = new window.kakao.maps.Marker(markerOptions);

      // 마커 클릭 이벤트
      if (onMarkerClick) {
        window.kakao.maps.event.addListener(marker, 'click', () => {
          onMarkerClick(place);
        });
      }

      markers.current.set(place.id, marker);
      bounds.extend(position);
    });

    // 모든 마커를 포함하는 영역으로 자동 맞춤
    map.current.setBounds(bounds);
  }, [places, isMapReady, onMarkerClick]);

  // 줌 인/아웃 함수
  const handleZoomIn = () => {
    if (map.current && currentZoom > 1) {
      map.current.setLevel(currentZoom - 1);
    }
  };

  const handleZoomOut = () => {
    if (map.current && currentZoom < 14) {
      map.current.setLevel(currentZoom + 1);
    }
  };

  // 현재 위치로 이동
  const handleCenterMap = () => {
    if (map.current) {
      const currentCenter = new window.kakao.maps.LatLng(center.lat, center.lng);
      map.current.panTo(currentCenter);
    }
  };

  return (
    <div className="relative w-full h-full">
      {/* 지도 컨테이너 */}
      <div
        ref={mapContainer}
        className="w-full h-full"
        style={{ minHeight: '400px' }}
      />

      {/* 줌 컨트롤 */}
      <div className="absolute bottom-4 right-4 flex flex-col gap-2 z-10">
        {/* 줌 인 버튼 */}
        <button
          onClick={handleZoomIn}
          disabled={currentZoom <= 1}
          className={`
            flex items-center justify-center
            w-10 h-10 rounded-lg shadow-md
            transition-all duration-200
            ${
              currentZoom <= 1
                ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                : 'bg-white text-gray-800 hover:bg-blue-50 active:bg-blue-100'
            }
          `}
          title="줌 인"
        >
          <svg
            className="w-5 h-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 6v6m0 0v6m0-6h6m0 0h6"
            />
          </svg>
        </button>

        {/* 줌 아웃 버튼 */}
        <button
          onClick={handleZoomOut}
          disabled={currentZoom >= 14}
          className={`
            flex items-center justify-center
            w-10 h-10 rounded-lg shadow-md
            transition-all duration-200
            ${
              currentZoom >= 14
                ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                : 'bg-white text-gray-800 hover:bg-blue-50 active:bg-blue-100'
            }
          `}
          title="줌 아웃"
        >
          <svg
            className="w-5 h-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M20 12H4"
            />
          </svg>
        </button>

        {/* 중심으로 이동 버튼 */}
        <button
          onClick={handleCenterMap}
          className={`
            flex items-center justify-center
            w-10 h-10 rounded-lg shadow-md
            bg-white text-gray-800 hover:bg-blue-50 active:bg-blue-100
            transition-all duration-200
          `}
          title="중심으로 이동"
        >
          <svg
            className="w-5 h-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"
            />
          </svg>
        </button>
      </div>

      {/* 줌 레벨 표시 */}
      <div className="absolute top-4 right-4 bg-white px-3 py-2 rounded-lg shadow-md text-sm font-medium text-gray-700 z-10">
        줌 레벨: {currentZoom}
      </div>

      {/* 로딩 상태 */}
      {!isMapReady && (
        <div className="absolute inset-0 flex items-center justify-center bg-gray-50 z-5">
          <div className="text-center">
            <div className="w-8 h-8 border-4 border-blue-200 border-t-blue-600 rounded-full animate-spin mx-auto mb-2"></div>
            <p className="text-sm text-gray-600">지도를 로드하고 있습니다...</p>
          </div>
        </div>
      )}
    </div>
  );
}
