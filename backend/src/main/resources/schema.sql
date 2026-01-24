-- Regions table
CREATE TABLE IF NOT EXISTS regions (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    center_lat DOUBLE PRECISION NOT NULL,
    center_lng DOUBLE PRECISION NOT NULL
);

-- Region keywords table (ElementCollection)
CREATE TABLE IF NOT EXISTS region_keywords (
    region_id VARCHAR(50) NOT NULL,
    keyword VARCHAR(255) NOT NULL,
    FOREIGN KEY (region_id) REFERENCES regions(id) ON DELETE CASCADE
);

-- Index for city filtering
CREATE INDEX IF NOT EXISTS idx_regions_city ON regions(city);
