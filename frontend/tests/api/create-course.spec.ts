import { test, expect } from "@playwright/test";

test("POST /v1/courses creates a course", async ({ request }) => {
  const response = await request.post("http://localhost:8080/v1/courses", {
    headers: {
      "X-Session-Id": "playwright-session",
    },
    data: {
      regionId: "gangnam",
      dateType: "food",
      budget: "30000-50000",
    },
  });

  const body = await response.json().catch(() => ({}));
  console.log("status:", response.status());
  console.log("body:", JSON.stringify(body));

  expect(response.status(), "API should return 200").toBe(200);
  expect(body?.success, "API should respond success=true").toBe(true);
});
