import { test, expect } from '@playwright/test';

test.describe('AI Place Curation E2E Test', () => {
  test('should load test-curation page and display UI elements', async ({ page }) => {
    await page.goto('http://localhost:3000/test-curation');

    // Check page title
    await expect(page.locator('h1')).toContainText('장소 큐레이션 테스트');

    // Check input field exists
    const input = page.locator('input[type="text"]');
    await expect(input).toBeVisible();
    await expect(input).toHaveAttribute('placeholder', '예: 1234567890');

    // Check button exists
    const button = page.locator('button:has-text("조회")');
    await expect(button).toBeVisible();
  });

  test('should fetch and display curation data for valid place ID', async ({ page }) => {
    await page.goto('http://localhost:3000/test-curation');

    // Enter valid place ID
    const input = page.locator('input[type="text"]');
    await input.fill('1');

    // Click fetch button
    const button = page.locator('button:has-text("조회")');
    await button.click();

    // Wait for loading state
    await expect(button).toHaveText('조회 중...');

    // Wait for response and check result display
    await expect(page.locator('text=큐레이션 결과')).toBeVisible({ timeout: 10000 });

    // Check all result fields are displayed
    await expect(page.locator('text=데이트 적합도')).toBeVisible();
    await expect(page.locator('text=분위기')).toBeVisible();
    await expect(page.locator('text=가격대')).toBeVisible();
    await expect(page.locator('text=추천 시간')).toBeVisible();
    await expect(page.locator('text=추천 이유')).toBeVisible();

    // Check date score is displayed as a number
    const dateScoreElement = page.locator('text=/[0-9]+\\/10/');
    await expect(dateScoreElement).toBeVisible();

    // Check at least one mood tag is displayed
    const moodTags = page.locator('.bg-purple-100');
    await expect(moodTags.first()).toBeVisible();
  });

  test('should display error for invalid place ID', async ({ page }) => {
    await page.goto('http://localhost:3000/test-curation');

    // Enter invalid place ID
    const input = page.locator('input[type="text"]');
    await input.fill('999999999');

    // Click fetch button
    const button = page.locator('button:has-text("조회")');
    await button.click();

    // Wait for error message
    const errorBox = page.locator('.bg-red-50');
    await expect(errorBox).toBeVisible({ timeout: 10000 });
    await expect(errorBox).toContainText('장소를 찾을 수 없습니다');
  });

  test('should validate empty input', async ({ page }) => {
    await page.goto('http://localhost:3000/test-curation');

    // Click button without entering place ID
    const button = page.locator('button:has-text("조회")');
    await button.click();

    // Check error message for empty input
    const errorBox = page.locator('.bg-red-50');
    await expect(errorBox).toBeVisible();
    await expect(errorBox).toContainText('장소 ID를 입력해주세요');
  });
});
