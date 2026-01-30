import {
  CourseCreateResponse,
  SavedCourse,
  SaveCourseResult,
} from '@/types/course';

/**
 * 로컬 스토리지 키
 */
const STORAGE_KEY = 'dateclick_saved_courses';

/**
 * 최대 저장 가능 코스 수
 */
const MAX_SAVED_COURSES = 20;

/**
 * 브라우저 환경 여부 확인
 */
function isBrowser(): boolean {
  return typeof window !== 'undefined';
}

/**
 * 로컬 스토리지에서 저장된 코스 목록 조회
 * @returns 저장된 코스 목록 (최신순 정렬)
 */
export function getCourses(): SavedCourse[] {
  if (!isBrowser()) {
    return [];
  }

  try {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (!stored) {
      return [];
    }

    const courses: SavedCourse[] = JSON.parse(stored);
    // 최신순 정렬 (savedAt 기준 내림차순)
    return courses.sort(
      (a, b) => new Date(b.savedAt).getTime() - new Date(a.savedAt).getTime()
    );
  } catch (error) {
    console.error('Failed to parse saved courses:', error);
    return [];
  }
}

/**
 * 특정 코스 조회
 * @param courseId 코스 ID
 * @returns 저장된 코스 또는 null
 */
export function getCourse(courseId: string): SavedCourse | null {
  const courses = getCourses();
  return courses.find((course) => course.courseId === courseId) || null;
}

/**
 * 코스 저장 여부 확인
 * @param courseId 코스 ID
 * @returns 저장 여부
 */
export function isSaved(courseId: string): boolean {
  return getCourse(courseId) !== null;
}

/**
 * 저장된 코스 개수 조회
 * @returns 저장된 코스 개수
 */
export function getSavedCount(): number {
  return getCourses().length;
}

/**
 * 저장 가능 여부 확인
 * @returns 저장 가능 여부
 */
export function canSave(): boolean {
  return getSavedCount() < MAX_SAVED_COURSES;
}

/**
 * 코스 저장
 * 저장 시점의 코스 정보를 스냅샷으로 저장
 * @param course 저장할 코스 정보
 * @returns 저장 결과
 */
export function saveCourse(course: CourseCreateResponse): SaveCourseResult {
  if (!isBrowser()) {
    return {
      success: false,
      error: 'STORAGE_ERROR',
      message: '브라우저 환경에서만 사용 가능합니다.',
    };
  }

  // 이미 저장된 코스인지 확인
  if (isSaved(course.courseId)) {
    return {
      success: false,
      error: 'ALREADY_SAVED',
      message: '이미 저장된 코스입니다.',
    };
  }

  // 저장 공간 확인
  if (!canSave()) {
    return {
      success: false,
      error: 'STORAGE_FULL',
      message: `저장 공간이 가득 찼습니다. 최대 ${MAX_SAVED_COURSES}개까지 저장할 수 있습니다.`,
    };
  }

  try {
    const courses = getCourses();

    // 저장 시점의 스냅샷 생성
    const savedCourse: SavedCourse = {
      ...course,
      savedAt: new Date().toISOString(),
    };

    // 새 코스를 맨 앞에 추가
    courses.unshift(savedCourse);

    localStorage.setItem(STORAGE_KEY, JSON.stringify(courses));

    return { success: true };
  } catch (error) {
    console.error('Failed to save course:', error);
    return {
      success: false,
      error: 'STORAGE_ERROR',
      message: '코스 저장에 실패했습니다.',
    };
  }
}

/**
 * 코스 삭제
 * @param courseId 삭제할 코스 ID
 * @returns 삭제 성공 여부
 */
export function deleteCourse(courseId: string): boolean {
  if (!isBrowser()) {
    return false;
  }

  try {
    const courses = getCourses();
    const filteredCourses = courses.filter(
      (course) => course.courseId !== courseId
    );

    // 삭제할 코스가 없는 경우
    if (filteredCourses.length === courses.length) {
      return false;
    }

    localStorage.setItem(STORAGE_KEY, JSON.stringify(filteredCourses));
    return true;
  } catch (error) {
    console.error('Failed to delete course:', error);
    return false;
  }
}

/**
 * 모든 저장된 코스 삭제
 * @returns 삭제 성공 여부
 */
export function clearAllCourses(): boolean {
  if (!isBrowser()) {
    return false;
  }

  try {
    localStorage.removeItem(STORAGE_KEY);
    return true;
  } catch (error) {
    console.error('Failed to clear courses:', error);
    return false;
  }
}

/**
 * 최대 저장 가능 개수 상수 export
 */
export const MAX_COURSES = MAX_SAVED_COURSES;
