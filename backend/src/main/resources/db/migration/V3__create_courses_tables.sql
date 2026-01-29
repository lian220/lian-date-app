-- Course 테이블 생성
CREATE TABLE IF NOT EXISTS courses
(
    id          VARCHAR(50) PRIMARY KEY,
    region_id   VARCHAR(50)  NOT NULL,
    region_name VARCHAR(100) NOT NULL,
    date_type   VARCHAR(20)  NOT NULL,
    budget_min  INTEGER      NOT NULL,
    budget_max  INTEGER      NOT NULL,
    session_id  VARCHAR(100),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_courses_region FOREIGN KEY (region_id) REFERENCES regions (id)
);

-- Course Places 테이블 생성
CREATE TABLE IF NOT EXISTS course_places
(
    id                 BIGSERIAL PRIMARY KEY,
    course_id          VARCHAR(50)  NOT NULL,
    place_order        INTEGER      NOT NULL,
    place_id           VARCHAR(50)  NOT NULL,
    name               VARCHAR(200) NOT NULL,
    category           VARCHAR(50)  NOT NULL,
    category_detail    VARCHAR(100),
    address            TEXT         NOT NULL,
    road_address       TEXT,
    lat                DOUBLE PRECISION NOT NULL,
    lng                DOUBLE PRECISION NOT NULL,
    phone              VARCHAR(20),
    estimated_cost     INTEGER      NOT NULL,
    estimated_duration INTEGER      NOT NULL,
    recommended_time   VARCHAR(10),
    recommend_reason   TEXT         NOT NULL,
    image_url          TEXT,
    kakao_place_url    TEXT         NOT NULL,
    CONSTRAINT fk_course_places_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE
);

-- Routes 테이블 생성
CREATE TABLE IF NOT EXISTS routes
(
    id             BIGSERIAL PRIMARY KEY,
    course_id      VARCHAR(50)  NOT NULL,
    from_order     INTEGER      NOT NULL,
    to_order       INTEGER      NOT NULL,
    distance       INTEGER      NOT NULL,
    duration       INTEGER      NOT NULL,
    transport_type VARCHAR(20)  NOT NULL,
    description    VARCHAR(500) NOT NULL,
    CONSTRAINT fk_routes_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE
);

-- 인덱스 생성
CREATE INDEX idx_courses_session_id ON courses (session_id);
CREATE INDEX idx_courses_region_id ON courses (region_id);
CREATE INDEX idx_courses_created_at ON courses (created_at DESC);
CREATE INDEX idx_course_places_course_id ON course_places (course_id);
CREATE INDEX idx_course_places_order ON course_places (course_id, place_order);
CREATE INDEX idx_routes_course_id ON routes (course_id);
