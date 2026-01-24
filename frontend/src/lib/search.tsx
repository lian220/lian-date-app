import { Region } from '@/types/region';
import React from 'react';

/**
 * 정규식 메타문자 이스케이프
 */
function escapeRegExp(string: string): string {
  return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

/**
 * 검색어로 권역 필터링
 * 권역명과 키워드 모두 검색 대상
 */
export function filterRegions(
  regions: Region[],
  searchQuery: string
): Region[] {
  if (!searchQuery.trim()) {
    return regions;
  }

  const query = searchQuery.toLowerCase();

  return regions.filter(region => {
    const nameMatch = region.name.toLowerCase().includes(query);
    const keywordMatch = region.keywords.some(keyword =>
      keyword.toLowerCase().includes(query)
    );

    return nameMatch || keywordMatch;
  });
}

/**
 * 텍스트에서 검색어 매칭 부분을 찾아 하이라이트 처리
 */
export function highlightText(text: string, searchQuery: string): string {
  if (!searchQuery.trim()) {
    return text;
  }

  const escapedQuery = escapeRegExp(searchQuery);
  const regex = new RegExp(`(${escapedQuery})`, 'gi');
  return text.replace(regex, '<mark>$1</mark>');
}

/**
 * 검색어 매칭 부분을 React 엘리먼트로 변환
 */
export function getHighlightedText(
  text: string,
  searchQuery: string
): React.ReactNode[] {
  if (!searchQuery.trim()) {
    return [text];
  }

  const escapedQuery = escapeRegExp(searchQuery);
  const parts = text.split(new RegExp(`(${escapedQuery})`, 'gi'));

  return parts.map((part, index) => {
    if (part.toLowerCase() === searchQuery.toLowerCase()) {
      return (
        <mark
          key={index}
          className="bg-yellow-200 text-gray-900 dark:bg-yellow-500 dark:text-gray-900"
        >
          {part}
        </mark>
      );
    }
    return part;
  });
}
