-- Fix course_places column size issues
-- recommended_time VARCHAR(10) -> VARCHAR(50) to support time ranges like "14:00-16:00"
ALTER TABLE course_places
    ALTER COLUMN recommended_time TYPE VARCHAR(50);

-- phone VARCHAR(20) -> VARCHAR(30) to support longer international formats
ALTER TABLE course_places
    ALTER COLUMN phone TYPE VARCHAR(30);
