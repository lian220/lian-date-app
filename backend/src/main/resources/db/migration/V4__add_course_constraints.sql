-- Add budget range check constraint
ALTER TABLE courses ADD CONSTRAINT chk_budget_range CHECK (budget_min <= budget_max);

-- Add unique constraint for course places order
ALTER TABLE course_places ADD CONSTRAINT uq_course_places_order UNIQUE (course_id, place_order);

-- Add unique constraint for routes order
ALTER TABLE routes ADD CONSTRAINT uq_routes_order UNIQUE (course_id, from_order, to_order);

-- Drop redundant index (composite index covers it)
DROP INDEX IF EXISTS idx_course_places_course_id;
