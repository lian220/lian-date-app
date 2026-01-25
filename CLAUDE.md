# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Date Click (데이트 딸깍)** - AI-powered date course recommendation service for Korean couples. The system generates optimized date courses based on region, date type, and budget using AI (OpenAI) and location data (Kakao Maps).

**Tech Stack**:
- **Backend**: Spring Boot 3.4.1 + Kotlin 1.9.25 + PostgreSQL 15
- **Frontend**: Next.js 16.1.4 + React 19 + TypeScript + Tailwind CSS 4
- **Infrastructure**: Docker Compose (PostgreSQL, Backend, Frontend)

## Architecture

### Backend: Hexagonal Architecture (Ports & Adapters)

The backend follows hexagonal architecture with clear separation of concerns:

```
backend/src/main/kotlin/com/dateclick/api/
├── domain/              # Core business logic (ports, entities, value objects)
│   ├── course/
│   │   ├── entity/      # Course, CoursePlace, Route
│   │   ├── port/
│   │   │   ├── inbound/   # Use case interfaces (empty in current impl)
│   │   │   └── outbound/  # Repository & external service ports
│   │   ├── service/     # Domain services
│   │   └── vo/          # Value objects (CourseId, Budget, DateType, etc.)
│   ├── place/           # Place domain
│   ├── rating/          # Rating domain
│   └── region/          # Region domain
├── application/         # Use cases (orchestration layer)
│   ├── course/          # CreateCourseUseCase, GetCourseUseCase, etc.
│   ├── place/           # GetPlaceDetailUseCase
│   └── region/          # GetRegionsUseCase
├── infrastructure/      # External adapters
│   ├── config/          # Spring configuration (OpenApiConfig, OpenAiConfig)
│   ├── external/        # External API clients
│   │   ├── kakao/       # Kakao Maps API client
│   │   └── openai/      # OpenAI API client
│   └── persistence/     # JPA repositories and mappers
│       ├── entity/      # JPA entities
│       ├── mapper/      # Domain ↔ JPA mappers
│       └── repository/  # JPA repositories
└── presentation/        # API layer
    ├── advice/          # Exception handling
    ├── mapper/          # Request/Response mappers
    └── rest/            # Controllers (course, place, region, health)
```

**Key Architectural Principles**:
- **Domain Layer**: Independent of frameworks, contains business logic
- **Application Layer**: Use cases orchestrate domain services
- **Infrastructure Layer**: Implements ports (repositories, external APIs)
- **Presentation Layer**: REST controllers, request/response mapping

### Frontend: Next.js App Router

```
frontend/src/
├── app/            # Next.js app router (pages)
├── components/     # React components
├── lib/            # Utilities and helpers
└── types/          # TypeScript type definitions
```

## Development Commands

### Docker Environment (Recommended)

Start all services (PostgreSQL, Backend, Frontend):
```bash
docker-compose up -d
```

View logs:
```bash
docker-compose logs -f [service]  # service: postgres, backend, frontend
```

Stop services:
```bash
docker-compose down
```

Clean restart (removes volumes):
```bash
docker-compose down -v
docker-compose up -d
```

**Service URLs**:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- PostgreSQL: localhost:5432

### Backend Development

**Build**:
```bash
cd backend
./gradlew build
```

**Run (local, outside Docker)**:
```bash
./gradlew bootRun
```

**Run Tests**:
```bash
./gradlew test
```

**Run Specific Test**:
```bash
./gradlew test --tests "com.dateclick.api.YourTestClass"
```

**Clean Build**:
```bash
./gradlew clean build
```

**Note**: Backend requires PostgreSQL running. Use Docker Compose or configure local PostgreSQL with credentials from `.env.example`.

### Frontend Development

**Install Dependencies**:
```bash
cd frontend
npm install
```

**Development Server** (outside Docker):
```bash
npm run dev
```

**Build**:
```bash
npm run build
```

**Production Server**:
```bash
npm run start
```

**Lint**:
```bash
npm run lint
```

**Format**:
```bash
npm run format
```

## Configuration

### Environment Variables

Copy `.env.example` to `.env` and configure:

```bash
# PostgreSQL
POSTGRES_DB=dateclick
POSTGRES_USER=dateclick
POSTGRES_PASSWORD=dateclick

# Backend
DB_USERNAME=dateclick
DB_PASSWORD=dateclick
DATABASE_URL=jdbc:postgresql://postgres:5432/dateclick

# Spring Profile
SPRING_PROFILES_ACTIVE=local
```

### Spring Profiles

- **local**: Development profile (default)
- Configure in `backend/src/main/resources/application.yml`

### Database Initialization

- Database schemas are managed by JPA (`ddl-auto: validate`)
- Initialization scripts: `backend/init-scripts/` (mounted to PostgreSQL container)

### Git Worktree Environment

The project supports multiple git worktrees with shared PostgreSQL database.

**Setup (One-time)**:
```bash
# Create shared Docker network (required once)
docker network create dateclick-shared-network
```

**Main Worktree (default ports)**:
```bash
# Start shared PostgreSQL
docker compose --profile db up -d postgres

# Start backend (port 8080) and frontend (port 3000)
docker compose up -d backend frontend
```

**Additional Worktrees (different ports)**:
```bash
# In worktree directory, create .env with different ports
echo "BACKEND_PORT=8081" >> .env
echo "FRONTEND_PORT=3001" >> .env

# Start only backend/frontend (shares main worktree's PostgreSQL)
docker compose up -d backend frontend
# Or use project name to avoid conflicts:
# docker compose -p lian-date-app-feature-x up -d backend
```

**Benefits**:
- ✅ Single shared PostgreSQL database across all worktrees
- ✅ No data duplication or sync issues
- ✅ Independent backend/frontend per worktree
- ✅ Different ports avoid conflicts (8080, 8081, 8082, ...)

**Network Architecture**:
- All containers connect to `dateclick-shared-network` (external network)
- PostgreSQL runs once with fixed name `dateclick-postgres`
- Backend/frontend containers auto-named by project (no conflicts)

## Key Business Domains

### Course (데이트 코스)
- AI-generated date course recommendations
- Composed of 3-4 places (restaurant, cafe, activity, etc.)
- Includes route information and estimated costs

### Place (장소)
- Individual locations within a course
- Fetched from Kakao Maps API
- Includes address, coordinates, operating hours, etc.

### Region (지역)
- Geographic regions for course filtering
- Hierarchical structure (city → district)

### Rating (평가)
- User ratings for generated courses
- 1-5 star rating system

## External Integrations

### OpenAI API
- Used for AI-powered course generation
- Configuration: `OpenAiConfig.kt`
- Client: `infrastructure/external/openai/`

### Kakao Maps API
- Used for place search and details
- Client: `infrastructure/external/kakao/`

## Database Schema

- **PostgreSQL 15** with JPA/Hibernate
- Entities in `infrastructure/persistence/entity/`
- Domain entities in `domain/*/entity/`
- Mapping between JPA and domain entities via `infrastructure/persistence/mapper/`

## API Documentation

Swagger/OpenAPI available at: http://localhost:8080/swagger-ui.html

API endpoints:
- `/v1/api-docs` - OpenAPI JSON spec
- `/health` - Health check endpoint

## Code Style

### Backend (Kotlin)
- JVM Target: Java 21
- Kotlin 1.9.25 with Spring plugin
- JPA all-open plugin for entities
- Format: Standard Kotlin conventions

### Frontend (TypeScript)
- ESLint with Next.js config
- Prettier for formatting
- TypeScript strict mode

## Testing

### Backend Tests
- JUnit 5 Platform
- Located in `backend/src/test/kotlin/`
- Run with: `./gradlew test`

### Frontend Tests
- (To be configured)

## Project Context

See `docs/prd.md` for detailed Product Requirements Document including:
- Target personas (27세 직장인 "김도윤", 23세 대학생 "이서아")
- MVP goals and success metrics (HEART framework)
- Feature specifications and user flows

## Hot Reload / Development Workflow

**Frontend**:
- Docker volume mounts `./frontend:/app` for hot reload
- Changes to `frontend/src/` are immediately reflected

**Backend**:
- Rebuild required: `docker-compose restart backend`
- Or use `./gradlew bootRun` outside Docker for faster iteration
