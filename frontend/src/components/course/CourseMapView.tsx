'use client';

import { useState, useMemo, useCallback } from 'react';
import { Place, CourseCreateResponse } from '@/types/course';
import { MapPlace } from '@/types/kakao-map';
import KakaoMap from '@/components/KakaoMap';
import PlaceDetailBottomSheet from '@/components/map/PlaceDetailBottomSheet';

interface CourseMapViewProps {
  course: CourseCreateResponse;
}

/**
 * 코스 지도 뷰 컴포넌트
 * - 순서 번호 마커 표시 (1, 2, 3...)
 * - 장소 간 경로 점선 연결
 * - 마커 클릭 시 바텀시트로 상세 정보 표시
 */
export default function CourseMapView({ course }: CourseMapViewProps) {
  const [selectedPlace, setSelectedPlace] = useState<Place | null>(null);
  const [isBottomSheetOpen, setIsBottomSheetOpen] = useState(false);

  // Place[] → MapPlace[] 변환
  const mapPlaces: MapPlace[] = useMemo(() => {
    return course.places.map((place) => ({
      id: place.placeId,
      name: place.name,
      lat: place.lat,
      lng: place.lng,
      address: place.address,
      category: place.category,
      order: place.order,
    }));
  }, [course.places]);

  // 지도 중심점 계산 (첫 번째 장소 기준)
  const mapCenter = useMemo(() => {
    if (course.places.length > 0) {
      return {
        lat: course.places[0].lat,
        lng: course.places[0].lng,
      };
    }
    return { lat: 37.5665, lng: 126.978 }; // 서울 기본값
  }, [course.places]);

  // 마커 클릭 핸들러
  const handleMarkerClick = useCallback(
    (mapPlace: MapPlace) => {
      // MapPlace에서 원본 Place 찾기
      const place = course.places.find((p) => p.placeId === mapPlace.id);
      if (place) {
        setSelectedPlace(place);
        setIsBottomSheetOpen(true);
      }
    },
    [course.places]
  );

  // 바텀시트 닫기 핸들러
  const handleCloseBottomSheet = useCallback(() => {
    setIsBottomSheetOpen(false);
  }, []);

  return (
    <div className="relative h-[500px] w-full overflow-hidden rounded-xl border border-gray-200 dark:border-gray-700">
      {/* 지도 */}
      <KakaoMap
        places={mapPlaces}
        center={mapCenter}
        zoom={5}
        onMarkerClick={handleMarkerClick}
        showRoute={true}
        showNumberMarkers={true}
      />

      {/* 범례 */}
      <div className="absolute left-4 top-4 z-10 rounded-lg bg-white/90 px-3 py-2 shadow-md backdrop-blur-sm dark:bg-gray-800/90">
        <div className="flex items-center gap-2 text-xs text-gray-600 dark:text-gray-400">
          <div className="flex h-5 w-5 items-center justify-center rounded-full bg-indigo-500 text-[10px] font-bold text-white">
            1
          </div>
          <span>마커를 탭하면 상세 정보를 볼 수 있어요</span>
        </div>
      </div>

      {/* 장소 목록 (하단 슬라이드) */}
      <div className="absolute bottom-0 left-0 right-0 z-10 bg-gradient-to-t from-white via-white/95 to-transparent px-4 pb-4 pt-8 dark:from-gray-900 dark:via-gray-900/95">
        <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-hide">
          {course.places.map((place) => (
            <button
              key={place.placeId}
              onClick={() => {
                setSelectedPlace(place);
                setIsBottomSheetOpen(true);
              }}
              className={`flex flex-shrink-0 items-center gap-2 rounded-full border px-3 py-2 text-sm transition-all ${
                selectedPlace?.placeId === place.placeId
                  ? 'border-indigo-500 bg-indigo-50 text-indigo-700 dark:bg-indigo-950 dark:text-indigo-300'
                  : 'border-gray-200 bg-white text-gray-700 hover:border-gray-300 hover:bg-gray-50 dark:border-gray-600 dark:bg-gray-800 dark:text-gray-300 dark:hover:bg-gray-700'
              }`}
            >
              <span
                className={`flex h-5 w-5 items-center justify-center rounded-full text-[10px] font-bold text-white ${
                  selectedPlace?.placeId === place.placeId
                    ? 'bg-indigo-500'
                    : 'bg-gray-400 dark:bg-gray-500'
                }`}
              >
                {place.order}
              </span>
              <span className="max-w-[120px] truncate font-medium">
                {place.name}
              </span>
            </button>
          ))}
        </div>
      </div>

      {/* 장소 상세 바텀시트 */}
      <PlaceDetailBottomSheet
        isOpen={isBottomSheetOpen}
        onClose={handleCloseBottomSheet}
        place={selectedPlace}
      />
    </div>
  );
}
