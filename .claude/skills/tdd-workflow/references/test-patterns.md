# TDD 테스트 작성 패턴

## Backend (Kotlin/JUnit 5)

### Domain Service 테스트
```kotlin
@Test
fun `should create course with valid budget`() {
    // Given
    val port = mockk<CourseRepositoryPort>()
    val service = CourseService(port)
    // When & Then
    ...
}
```

### UseCase 테스트
```kotlin
@Test
fun `should orchestrate course creation`() {
    // Given - Port를 mock
    val coursePort = mockk<CourseRepositoryPort>()
    val useCase = CreateCourseUseCase(coursePort)
    // When & Then
    ...
}
```

### Controller 테스트
```kotlin
@WebMvcTest(CourseController::class)
class CourseControllerTest {
    @Autowired lateinit var mockMvc: MockMvc
    @MockkBean lateinit var useCase: CreateCourseUseCase

    @Test
    fun `POST courses returns 201`() {
        mockMvc.perform(post("/v1/courses")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"region": "강남", "budget": 50000}"""))
            .andExpect(status().isCreated)
    }
}
```

### Repository 테스트
```kotlin
@DataJpaTest
class CourseRepositoryTest {
    @Autowired lateinit var repository: JpaCourseRepository
    // DB 연동 테스트
}
```

## Frontend (TypeScript)

### 컴포넌트 테스트
```typescript
// __tests__/CourseCard.test.tsx
describe('CourseCard', () => {
  it('should render course title', () => {
    render(<CourseCard course={mockCourse} />)
    expect(screen.getByText('강남 데이트 코스')).toBeInTheDocument()
  })
})
```

## 테스트 위치

| 대상 | 위치 |
|------|------|
| Domain Service | `test/.../domain/{도메인}/service/` |
| UseCase | `test/.../application/{도메인}/` |
| Controller | `test/.../presentation/rest/` |
| Repository | `test/.../infrastructure/persistence/` |
| Frontend 컴포넌트 | `__tests__/` 또는 `*.test.tsx` |
