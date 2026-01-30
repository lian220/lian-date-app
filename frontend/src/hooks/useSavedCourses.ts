'use client';

import { useState, useCallback, useSyncExternalStore } from 'react';
import {
  CourseCreateResponse,
  SavedCourse,
  SaveCourseResult,
} from '@/types/course';
import {
  getCourses,
  getCourse,
  saveCourse,
  deleteCourse,
  isSaved,
  canSave,
  MAX_COURSES,
} from '@/lib/courseStorage';

/**
 * useSavedCourses 훅 반환 타입
 */
interface UseSavedCoursesReturn {
  /** 저장된 코스 목록 (최신순 정렬) */
  courses: SavedCourse[];
  /** 저장된 코스 개수 */
  count: number;
  /** 최대 저장 가능 개수 */
  maxCourses: number;
  /** 저장 가능 여부 */
  canSaveMore: boolean;
  /** 로딩 상태 */
  isLoading: boolean;
  /** 코스 저장 */
  save: (course: CourseCreateResponse) => SaveCourseResult;
  /** 코스 삭제 */
  remove: (courseId: string) => boolean;
  /** 코스 조회 */
  get: (courseId: string) => SavedCourse | null;
  /** 저장 여부 확인 */
  checkSaved: (courseId: string) => boolean;
  /** 목록 새로고침 */
  refresh: () => void;
}

// 로컬 스토리지 변경 이벤트를 위한 구독 시스템
let listeners: Array<() => void> = [];

function emitChange() {
  for (const listener of listeners) {
    listener();
  }
}

function subscribe(listener: () => void) {
  listeners = [...listeners, listener];
  return () => {
    listeners = listeners.filter((l) => l !== listener);
  };
}

function getSnapshot(): SavedCourse[] {
  return getCourses();
}

function getServerSnapshot(): SavedCourse[] {
  return [];
}

/**
 * 저장된 코스 관리 훅
 *
 * 로컬 스토리지 기반의 코스 CRUD 기능을 React 상태와 연동
 *
 * @example
 * ```tsx
 * const { courses, save, remove, checkSaved, canSaveMore } = useSavedCourses();
 *
 * // 코스 저장
 * const result = save(courseData);
 * if (result.success) {
 *   toast('코스가 저장되었습니다');
 * } else if (result.error === 'STORAGE_FULL') {
 *   toast('저장 공간이 가득 찼습니다');
 * }
 *
 * // 저장 여부 확인
 * const isFavorite = checkSaved(courseId);
 *
 * // 코스 삭제
 * remove(courseId);
 * ```
 */
export function useSavedCourses(): UseSavedCoursesReturn {
  // useSyncExternalStore를 사용하여 로컬 스토리지와 동기화
  const courses = useSyncExternalStore(subscribe, getSnapshot, getServerSnapshot);
  const [isLoading] = useState(false);

  // 목록 새로고침
  const refresh = useCallback(() => {
    emitChange();
  }, []);

  // 코스 저장
  const save = useCallback((course: CourseCreateResponse): SaveCourseResult => {
    const result = saveCourse(course);
    if (result.success) {
      emitChange();
    }
    return result;
  }, []);

  // 코스 삭제
  const remove = useCallback((courseId: string): boolean => {
    const success = deleteCourse(courseId);
    if (success) {
      emitChange();
    }
    return success;
  }, []);

  // 코스 조회
  const get = useCallback((courseId: string): SavedCourse | null => {
    return getCourse(courseId);
  }, []);

  // 저장 여부 확인
  const checkSaved = useCallback((courseId: string): boolean => {
    return isSaved(courseId);
  }, []);

  return {
    courses,
    count: courses.length,
    maxCourses: MAX_COURSES,
    canSaveMore: canSave(),
    isLoading,
    save,
    remove,
    get,
    checkSaved,
    refresh,
  };
}

/**
 * 특정 코스의 저장 상태를 관리하는 훅
 *
 * 단일 코스의 저장/해제를 간편하게 처리
 *
 * @param courseId 코스 ID
 *
 * @example
 * ```tsx
 * const { isSaved, isLoading, refresh } = useCourseSaveState(courseId);
 *
 * return (
 *   <button onClick={() => { ... }} disabled={isLoading}>
 *     {isSaved ? '저장됨' : '저장하기'}
 *   </button>
 * );
 * ```
 */
export function useCourseSaveState(courseId: string) {
  const getSnapshotForCourse = useCallback(() => {
    return isSaved(courseId);
  }, [courseId]);

  const savedState = useSyncExternalStore(
    subscribe,
    getSnapshotForCourse,
    () => false
  );

  const refresh = useCallback(() => {
    emitChange();
  }, []);

  return {
    isSaved: savedState,
    isLoading: false,
    refresh,
  };
}
