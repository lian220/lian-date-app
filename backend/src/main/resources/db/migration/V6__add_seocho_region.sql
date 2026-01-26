-- Add Seocho region
INSERT INTO regions (id, name, city, description, center_lat, center_lng) VALUES
('seocho', '서초', 'seoul', '예술의전당과 법원이 위치한 서초. 고급스러운 분위기의 레스토랑과 카페, 문화시설이 어우러진 세련된 데이트 명소입니다.', 37.4916, 127.0078)
ON CONFLICT (id) DO NOTHING;

-- Insert keywords for Seocho region
INSERT INTO region_keywords (region_id, keyword) VALUES
('seocho', '예술'),
('seocho', '문화'),
('seocho', '고급'),
('seocho', '세련됨'),
('seocho', '카페')
ON CONFLICT DO NOTHING;
