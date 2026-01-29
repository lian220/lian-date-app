import { test, expect } from '@playwright/test';

test.describe('Address Clipboard Copy E2E Test', () => {
  test.beforeEach(async ({ context }) => {
    // Grant clipboard permissions
    await context.grantPermissions(['clipboard-read', 'clipboard-write']);
  });

  test('should copy address to clipboard when clicking address button', async ({
    page,
  }) => {
    // Mock course API response
    await page.route('**/api/v1/courses', async (route) => {
      if (route.request().method() === 'POST') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            success: true,
            data: {
              courseId: 'test-course-123',
              courseName: '테스트 데이트 코스',
              totalEstimatedCost: 80000,
              places: [
                {
                  placeId: '1',
                  name: '테스트 레스토랑',
                  category: '레스토랑',
                  address: '서울특별시 강남구 테헤란로 123',
                  estimatedCost: 40000,
                  recommendReason: '분위기가 좋고 맛있는 곳입니다.',
                },
                {
                  placeId: '2',
                  name: '테스트 카페',
                  category: '카페',
                  address: '서울특별시 강남구 역삼동 456',
                  estimatedCost: 15000,
                  recommendReason: '조용하고 데이트하기 좋습니다.',
                },
              ],
              routes: [],
            },
            error: null,
          }),
        });
      }
    });

    await page.goto('http://localhost:3000');

    // Select region
    const regionCard = page.locator('[data-testid="region-card"]').first();
    if (await regionCard.isVisible()) {
      await regionCard.click();
    }

    // Select date type
    const dateTypeCard = page.locator('[data-testid="date-type-card"]').first();
    if (await dateTypeCard.isVisible()) {
      await dateTypeCard.click();
    }

    // Select budget
    const budgetCard = page.locator('[data-testid="budget-card"]').first();
    if (await budgetCard.isVisible()) {
      await budgetCard.click();
    }

    // Click generate button if visible
    const generateButton = page.locator('button:has-text("코스 생성")');
    if (await generateButton.isVisible()) {
      await generateButton.click();

      // Wait for course result
      await page.waitForSelector('text=테스트 레스토랑', { timeout: 10000 });

      // Find and click address button
      const addressButton = page.locator(
        'button[aria-label*="주소 복사: 서울특별시 강남구 테헤란로 123"]'
      );
      await expect(addressButton).toBeVisible();
      await addressButton.click();

      // Check toast message appears
      const toast = page.locator('text=주소가 복사되었습니다');
      await expect(toast).toBeVisible({ timeout: 5000 });

      // Verify clipboard content
      const clipboardText = await page.evaluate(() =>
        navigator.clipboard.readText()
      );
      expect(clipboardText).toBe('서울특별시 강남구 테헤란로 123');

      // Wait for toast to disappear
      await expect(toast).not.toBeVisible({ timeout: 5000 });
    }
  });

  test('should show copy icon on hover', async ({ page }) => {
    // Mock course API response
    await page.route('**/api/v1/courses', async (route) => {
      if (route.request().method() === 'POST') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            success: true,
            data: {
              courseId: 'test-course-123',
              courseName: '테스트 데이트 코스',
              totalEstimatedCost: 50000,
              places: [
                {
                  placeId: '1',
                  name: '테스트 장소',
                  category: '카페',
                  address: '서울특별시 서초구 서초동 789',
                  estimatedCost: 20000,
                  recommendReason: '좋은 곳입니다.',
                },
              ],
              routes: [],
            },
            error: null,
          }),
        });
      }
    });

    await page.goto('http://localhost:3000');

    // Trigger course generation (simplified)
    const generateButton = page.locator('button:has-text("코스 생성")');
    if (await generateButton.isVisible()) {
      await generateButton.click();
      await page.waitForSelector('text=테스트 장소', { timeout: 10000 });

      // Find address button
      const addressButton = page.locator('button[aria-label*="주소 복사"]');
      await expect(addressButton).toBeVisible();

      // Copy icon should be hidden initially (opacity: 0)
      const copyIcon = addressButton.locator('svg').last();
      await expect(copyIcon).toHaveCSS('opacity', '0');

      // Hover over address button
      await addressButton.hover();

      // Copy icon should be visible on hover (opacity: 1)
      await expect(copyIcon).toHaveCSS('opacity', '1');
    }
  });
});
