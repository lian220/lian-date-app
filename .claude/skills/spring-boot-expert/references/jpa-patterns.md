# JPA 패턴

## Domain Entity vs JPA Entity 분리 원칙
- Domain Entity: 순수 Kotlin, 프레임워크 의존 없음
- JPA Entity: `@Entity`, `@Table` 등 JPA 어노테이션 사용
- Mapper로 변환: `CourseMapper.toDomain()`, `CourseMapper.toJpa()`

## JPA Entity 작성
```kotlin
@Entity
@Table(name = "courses")
class JpaCourseEntity(
    @Id
    @Column(columnDefinition = "uuid")
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val region: String,

    @Column(nullable = false)
    val budget: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val dateType: String,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "course", cascade = [CascadeType.ALL], orphanRemoval = true)
    val places: MutableList<JpaCoursePlaceEntity> = mutableListOf()
)
```

## Repository 패턴
```kotlin
// Spring Data JPA Repository (Infrastructure)
interface JpaCourseRepository : JpaRepository<JpaCourseEntity, UUID> {
    fun findByRegion(region: String): List<JpaCourseEntity>
}

// Port Adapter (Infrastructure → Domain Port 구현)
@Repository
class CourseRepositoryAdapter(
    private val jpaRepository: JpaCourseRepository,
    private val mapper: CourseMapper
) : CourseRepositoryPort {

    override fun save(course: Course): Course {
        val jpaEntity = mapper.toJpa(course)
        val saved = jpaRepository.save(jpaEntity)
        return mapper.toDomain(saved)
    }

    override fun findById(id: CourseId): Course? {
        return jpaRepository.findById(id.value)
            .map { mapper.toDomain(it) }
            .orElse(null)
    }
}
```

## N+1 방지
```kotlin
// FETCH JOIN 사용
@Query("SELECT c FROM JpaCourseEntity c JOIN FETCH c.places WHERE c.id = :id")
fun findByIdWithPlaces(@Param("id") id: UUID): JpaCourseEntity?

// @EntityGraph 사용
@EntityGraph(attributePaths = ["places"])
fun findByRegion(region: String): List<JpaCourseEntity>
```

## 트랜잭션 관리
- UseCase에 `@Transactional` 부착 (Application Layer)
- 읽기 전용: `@Transactional(readOnly = true)`
- Domain Service에는 트랜잭션 어노테이션 금지
