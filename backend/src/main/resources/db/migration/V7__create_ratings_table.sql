-- Ratings 테이블 생성
CREATE TABLE IF NOT EXISTS ratings
(
    id         VARCHAR(50) PRIMARY KEY,
    course_id  VARCHAR(50)  NOT NULL,
    session_id VARCHAR(100) NOT NULL,
    score      INTEGER      NOT NULL CHECK (score >= 1 AND score <= 5),
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ratings_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT uk_ratings_course_session UNIQUE (course_id, session_id)
);

-- 인덱스 생성
CREATE INDEX idx_ratings_course_id ON ratings (course_id);
CREATE INDEX idx_ratings_session_id ON ratings (session_id);
CREATE INDEX idx_ratings_created_at ON ratings (created_at DESC);

-- 코스별 평균 평점 조회를 위한 인덱스
CREATE INDEX idx_ratings_score ON ratings (score);
