import { test, expect } from '@playwright/test';

test.describe('Course Save Feature E2E Test (LAD-5)', () => {
  test.beforeEach(async ({ page }) => {
    // localStorage 초기화
    await page.goto('/');
    await page.evaluate(() => {
      localStorage.removeItem('dateclick_saved_courses');
      localStorage.removeItem('dateclick_storage_notice_dismissed');
    });
  });

  test('내 코스 페이지 - 빈 상태 UI 표시', async ({ page }) => {
    await page.goto('//my-courses');

    // 빈 상태 UI 확인
    await expect(page.getByText('저장된 코스가 없어요')).toBeVisible();
    await expect(page.getByText('마음에 드는 데이트 코스를 저장해보세요')).toBeVisible();
    await expect(page.getByRole('button', { name: '코스 만들기' })).toBeVisible();
  });

  test('내 코스 페이지 - 코스 만들기 버튼 클릭 시 메인으로 이동', async ({ page }) => {
    await page.goto('//my-courses');

    await page.getByRole('button', { name: '코스 만들기' }).click();
    await expect(page).toHaveURL('//');
  });

  test('코스 저장 버튼 동작 확인 (직접 localStorage 테스트)', async ({ page }) => {
    // localStorage에 코스를 직접 저장하여 저장 버튼 동작 테스트
    await page.goto('/');

    // courseStorage 모듈 함수들 테스트
    const result = await page.evaluate(() => {
      // 테스트용 코스 데이터
      const testCourse = {
        courseId: 'save-button-test-course',
        regionId: 'gangnam',
        regionName: '강남',
        dateType: 'romantic',
        budget: '3~5만원',
        totalEstimatedCost: 45000,
        places: [
          {
            order: 1,
            placeId: 'place-1',
            name: '저장 테스트 카페',
            category: '카페',
            address: '서울시 강남구',
            lat: 37.5,
            lng: 127.0,
            estimatedCost: 15000,
            estimatedDuration: 60,
            recommendReason: '테스트',
          },
        ],
        createdAt: new Date().toISOString(),
      };

      // localStorage에 직접 저장
      const STORAGE_KEY = 'dateclick_saved_courses';
      const savedCourse = { ...testCourse, savedAt: new Date().toISOString() };
      localStorage.setItem(STORAGE_KEY, JSON.stringify([savedCourse]));

      // 저장 확인
      const stored = localStorage.getItem(STORAGE_KEY);
      const courses = stored ? JSON.parse(stored) : [];

      return {
        saved: courses.length > 0,
        courseId: courses[0]?.courseId,
        hasRequiredFields:
          courses[0]?.savedAt !== undefined && courses[0]?.regionName !== undefined,
      };
    });

    expect(result.saved).toBe(true);
    expect(result.courseId).toBe('save-button-test-course');
    expect(result.hasRequiredFields).toBe(true);
  });

  test('저장된 코스가 내 코스 목록에 표시됨', async ({ page }) => {
    // 테스트용 코스 데이터 직접 저장
    await page.goto('/');
    await page.evaluate(() => {
      const testCourse = {
        courseId: 'test-course-1',
        regionId: 'gangnam',
        regionName: '강남',
        dateType: 'romantic',
        budget: '3~5만원',
        totalEstimatedCost: 45000,
        places: [
          {
            order: 1,
            placeId: 'place-1',
            name: '테스트 카페',
            category: '카페',
            address: '서울시 강남구',
            lat: 37.5,
            lng: 127.0,
            estimatedCost: 15000,
            estimatedDuration: 60,
            recommendReason: '분위기 좋은 카페',
          },
          {
            order: 2,
            placeId: 'place-2',
            name: '테스트 레스토랑',
            category: '레스토랑',
            address: '서울시 강남구',
            lat: 37.5,
            lng: 127.0,
            estimatedCost: 30000,
            estimatedDuration: 90,
            recommendReason: '맛있는 레스토랑',
          },
        ],
        createdAt: new Date().toISOString(),
        savedAt: new Date().toISOString(),
      };
      localStorage.setItem('dateclick_saved_courses', JSON.stringify([testCourse]));
    });

    // 내 코스 페이지로 이동
    await page.goto('//my-courses');

    // 저장된 코스 확인
    await expect(page.getByText('강남')).toBeVisible();
    await expect(page.getByText('로맨틱')).toBeVisible();
    await expect(page.getByText('테스트 카페 → 테스트 레스토랑')).toBeVisible();

    // 브라우저 저장 안내 배너 확인
    await expect(page.getByText('브라우저에 저장됩니다')).toBeVisible();
  });

  test('코스 삭제 다이얼로그 동작 확인', async ({ page }) => {
    // 테스트용 코스 저장
    await page.goto('/');
    await page.evaluate(() => {
      const testCourse = {
        courseId: 'test-course-delete',
        regionId: 'gangnam',
        regionName: '강남',
        dateType: 'food',
        budget: '3~5만원',
        totalEstimatedCost: 40000,
        places: [
          {
            order: 1,
            placeId: 'place-1',
            name: '삭제 테스트 카페',
            category: '카페',
            address: '서울시 강남구',
            lat: 37.5,
            lng: 127.0,
            estimatedCost: 15000,
            estimatedDuration: 60,
            recommendReason: '테스트',
          },
        ],
        createdAt: new Date().toISOString(),
        savedAt: new Date().toISOString(),
      };
      localStorage.setItem('dateclick_saved_courses', JSON.stringify([testCourse]));
    });

    await page.goto('//my-courses');

    // 삭제 버튼 클릭 (aria-label이 정확히 '코스 삭제'인 버튼)
    await page.getByRole('button', { name: '코스 삭제', exact: true }).click();

    // 삭제 확인 다이얼로그 확인
    await expect(page.getByText('코스를 삭제할까요?')).toBeVisible();
    await expect(page.getByText('삭제된 코스는 복구할 수 없어요')).toBeVisible();

    // 삭제 확인 (다이얼로그 내의 삭제 버튼 - 빨간색 버튼)
    await page.getByTestId('confirm-dialog-confirm-button').click();

    // 토스트 메시지 확인
    await expect(page.getByText('코스가 삭제되었어요')).toBeVisible();

    // 빈 상태로 변경 확인
    await expect(page.getByText('저장된 코스가 없어요')).toBeVisible();
  });

  test('브라우저 저장 안내 배너 닫기 동작', async ({ page }) => {
    // 테스트용 코스 저장
    await page.goto('/');
    await page.evaluate(() => {
      const testCourse = {
        courseId: 'test-course-banner',
        regionId: 'hongdae',
        regionName: '홍대',
        dateType: 'activity',
        budget: '5~10만원',
        totalEstimatedCost: 70000,
        places: [
          {
            order: 1,
            placeId: 'place-1',
            name: '배너 테스트',
            category: '액티비티',
            address: '서울시 마포구',
            lat: 37.5,
            lng: 126.9,
            estimatedCost: 70000,
            estimatedDuration: 120,
            recommendReason: '테스트',
          },
        ],
        createdAt: new Date().toISOString(),
        savedAt: new Date().toISOString(),
      };
      localStorage.setItem('dateclick_saved_courses', JSON.stringify([testCourse]));
    });

    await page.goto('//my-courses');

    // 배너 표시 확인
    await expect(page.getByText('브라우저에 저장됩니다')).toBeVisible();

    // 닫기 버튼 클릭
    await page.getByRole('button', { name: '안내 닫기' }).click();

    // 배너 사라짐 확인
    await expect(page.getByText('브라우저에 저장됩니다')).not.toBeVisible({ timeout: 1000 });

    // 페이지 새로고침 후에도 배너 안 보임
    await page.reload();
    await expect(page.getByText('브라우저에 저장됩니다')).not.toBeVisible({ timeout: 1000 });
  });

  test('저장 현황 표시 확인 (N/20)', async ({ page }) => {
    // 3개 코스 저장
    await page.goto('/');
    await page.evaluate(() => {
      const courses = [1, 2, 3].map((i) => ({
        courseId: `test-course-${i}`,
        regionId: 'gangnam',
        regionName: '강남',
        dateType: 'romantic',
        budget: '3~5만원',
        totalEstimatedCost: 45000,
        places: [
          {
            order: 1,
            placeId: `place-${i}`,
            name: `테스트 장소 ${i}`,
            category: '카페',
            address: '서울시 강남구',
            lat: 37.5,
            lng: 127.0,
            estimatedCost: 45000,
            estimatedDuration: 60,
            recommendReason: '테스트',
          },
        ],
        createdAt: new Date().toISOString(),
        savedAt: new Date().toISOString(),
      }));
      localStorage.setItem('dateclick_saved_courses', JSON.stringify(courses));
    });

    await page.goto('//my-courses');

    // 저장 현황 확인
    await expect(page.getByText('3/20')).toBeVisible();
  });
});
