-- Insert Seoul regions (20 regions)
-- Using ON CONFLICT DO NOTHING for idempotent inserts
INSERT INTO regions (id, name, city, description, center_lat, center_lng) VALUES
('gangnam', '강남', 'seoul', '트렌디하고 세련된 분위기의 강남 지역. 고급 레스토랑, 카페, 쇼핑몰이 밀집해 있어 데이트 코스로 인기가 많습니다.', 37.4979, 127.0276),
('hongdae', '홍대', 'seoul', '젊음과 예술이 공존하는 홍대 지역. 독특한 카페, 공연장, 클럽 등 다양한 문화 공간이 있어 활기찬 데이트를 즐길 수 있습니다.', 37.5563, 126.9236),
('itaewon', '이태원', 'seoul', '다국적 문화가 어우러진 이태원. 세계 각국의 음식과 독특한 바, 루프탑 등 이국적인 분위기를 느낄 수 있습니다.', 37.5344, 126.9944),
('jamsil', '잠실', 'seoul', '한강과 롯데월드가 있는 잠실. 쇼핑과 엔터테인먼트, 한강 산책을 함께 즐길 수 있는 복합 데이트 명소입니다.', 37.5133, 127.1000),
('yeonnam', '연남동', 'seoul', '감각적인 카페와 레스토랑이 즐비한 연남동. 경의선숲길을 따라 여유로운 산책과 맛집 탐방을 즐길 수 있습니다.', 37.5664, 126.9258),
('seongsu', '성수', 'seoul', '공장지대에서 힙한 문화공간으로 변모한 성수동. 독특한 카페와 편집샵, 갤러리가 모여있는 감성 데이트 명소입니다.', 37.5447, 127.0557),
('hannam', '한남동', 'seoul', '한강뷰와 고급스러운 분위기의 한남동. 프라이빗한 레스토랑과 루프탑 바가 많아 특별한 데이트에 제격입니다.', 37.5349, 127.0026),
('bukchon', '북촌/삼청동', 'seoul', '한옥마을과 갤러리가 있는 전통과 현대가 공존하는 지역. 문화적 분위기에서 로맨틱한 데이트를 즐길 수 있습니다.', 37.5825, 126.9833),
('mapo', '마포/망원', 'seoul', '망원시장과 한강공원이 어우러진 마포. 레트로 감성의 카페와 맛집이 가득한 데이트 핫플레이스입니다.', 37.5560, 126.9099),
('garosu', '가로수길/신사', 'seoul', '플라타너스 가로수가 아름다운 가로수길. 개성 있는 부티크와 카페, 레스토랑이 즐비한 패션 데이트 명소입니다.', 37.5197, 127.0229),
('yeouido', '여의도', 'seoul', '한강공원과 벚꽃축제로 유명한 여의도. 넓은 공원과 한강뷰 레스토랑에서 여유로운 데이트를 즐길 수 있습니다.', 37.5219, 126.9245),
('gwanghwamun', '광화문/종로', 'seoul', '역사와 문화의 중심 광화문. 고궁과 박물관, 전통찻집이 어우러진 문화 데이트 코스입니다.', 37.5720, 126.9769),
('dongdaemun', '동대문/DDP', 'seoul', '24시간 쇼핑과 DDP 디자인공간이 공존하는 동대문. 쇼핑과 문화를 함께 즐기는 활기찬 데이트가 가능합니다.', 37.5665, 127.0098),
('yongsan', '용산', 'seoul', '용산공원과 아이파크몰이 있는 용산. 한강뷰와 쇼핑, 문화공간이 어우러진 복합 데이트 지역입니다.', 37.5311, 126.9644),
('kondae', '건대', 'seoul', '대학가 특유의 활기와 젊음이 넘치는 건대. 저렴하고 다양한 맛집과 카페, 클럽이 모여있는 청춘 데이트 명소입니다.', 37.5403, 127.0695),
('wangsimni', '왕십리/성동', 'seoul', '전통시장과 신개발이 공존하는 왕십리. 로컬 맛집과 새로운 카페가 조화를 이루는 데이트 지역입니다.', 37.5610, 127.0377),
('myeongdong', '명동/을지로', 'seoul', '쇼핑의 메카 명동과 레트로 감성의 을지로. 낮에는 쇼핑, 밤에는 루프탑 바에서 특별한 데이트를 즐길 수 있습니다.', 37.5636, 126.9838),
('apgujeong', '압구정/청담', 'seoul', '럭셔리와 트렌디가 공존하는 압구정/청담. 고급 레스토랑과 갤러리, 편집샵이 즐비한 프리미엄 데이트 지역입니다.', 37.5274, 127.0280),
('sillim', '신림', 'seoul', '대학가 주변의 활기찬 신림. 저렴한 맛집과 카페가 많아 부담 없는 데이트를 즐길 수 있습니다.', 37.4842, 126.9295),
('nowon', '노원', 'seoul', '불암산과 수락산이 가까운 노원. 자연 친화적인 데이트와 카페거리를 함께 즐길 수 있는 북부의 데이트 명소입니다.', 37.6542, 127.0568)
ON CONFLICT (id) DO NOTHING;

-- Insert Gyeonggi regions (10 regions)
INSERT INTO regions (id, name, city, description, center_lat, center_lng) VALUES
('bundang', '분당', 'gyeonggi', '깔끔하고 계획적으로 조성된 분당 신도시. 넓은 공원과 쇼핑몰, 카페거리가 잘 갖춰져 있어 편안한 데이트가 가능합니다.', 37.3836, 127.1217),
('suwon', '수원', 'gyeonggi', '수원화성과 행궁동 벽화마을이 있는 수원. 역사와 문화, 맛집이 어우러진 다채로운 데이트를 즐길 수 있습니다.', 37.2636, 127.0286),
('ilsan', '일산', 'gyeonggi', '호수공원과 라페스타가 있는 일산. 자연과 쇼핑, 문화시설이 조화를 이루어 다양한 데이트 코스를 즐길 수 있습니다.', 37.6583, 126.7732),
('pangyo', '판교', 'gyeonggi', 'IT 기업과 현대적 건물들이 모인 판교. 세련된 카페와 레스토랑, 문화공간이 많아 모던한 데이트를 즐길 수 있습니다.', 37.3951, 127.1106),
('gwacheon', '과천', 'gyeonggi', '서울대공원과 국립과천과학관이 있는 과천. 자연과 문화를 함께 즐길 수 있는 힐링 데이트 코스입니다.', 37.4292, 127.0028),
('namyangju', '남양주', 'gyeonggi', '북한강과 다산생태공원이 있는 남양주. 자연 속에서 여유로운 시간을 보내기 좋은 데이트 명소입니다.', 37.6361, 127.2164),
('yongin', '용인', 'gyeonggi', '에버랜드와 한국민속촌이 있는 용인. 테마파크와 문화체험을 함께 즐길 수 있는 종합 데이트 지역입니다.', 37.2411, 127.1776),
('anyang', '안양', 'gyeonggi', '안양예술공원과 안양천이 있는 안양. 예술과 자연이 어우러진 문화 데이트를 즐길 수 있습니다.', 37.3943, 126.9568),
('goyang', '고양/킨텍스', 'gyeonggi', '킨텍스 전시장과 호수공원이 있는 고양. 전시/공연과 자연을 함께 즐기는 복합 데이트 지역입니다.', 37.6688, 126.7458),
('seongnam', '성남', 'gyeonggi', '분당과 판교를 포함한 성남. 신구도시가 공존하며 다양한 문화와 쇼핑을 즐길 수 있는 데이트 지역입니다.', 37.4201, 127.1262)
ON CONFLICT (id) DO NOTHING;

-- Insert keywords for Seoul regions
INSERT INTO region_keywords (region_id, keyword) VALUES
('gangnam', '트렌디'), ('gangnam', '쇼핑'), ('gangnam', '고급'), ('gangnam', '세련됨'), ('gangnam', '핫플레이스'),
('hongdae', '젊음'), ('hongdae', '예술'), ('hongdae', '문화'), ('hongdae', '활기'), ('hongdae', '인디'),
('itaewon', '이국적'), ('itaewon', '다문화'), ('itaewon', '루프탑'), ('itaewon', '세계음식'), ('itaewon', '나이트'),
('jamsil', '한강'), ('jamsil', '쇼핑'), ('jamsil', '놀이공원'), ('jamsil', '산책'), ('jamsil', '복합문화'),
('yeonnam', '감각적'), ('yeonnam', '카페'), ('yeonnam', '맛집'), ('yeonnam', '산책로'), ('yeonnam', '힐링'),
('seongsu', '힙'), ('seongsu', '감성'), ('seongsu', '공간'), ('seongsu', '브루클린'), ('seongsu', '편집샵'),
('hannam', '고급'), ('hannam', '한강뷰'), ('hannam', '프라이빗'), ('hannam', '루프탑'), ('hannam', '특별함'),
('bukchon', '한옥'), ('bukchon', '전통'), ('bukchon', '갤러리'), ('bukchon', '문화'), ('bukchon', '로맨틱'),
('mapo', '레트로'), ('mapo', '시장'), ('mapo', '한강'), ('mapo', '맛집'), ('mapo', '감성'),
('garosu', '패션'), ('garosu', '부티크'), ('garosu', '카페'), ('garosu', '세련됨'), ('garosu', '트렌디'),
('yeouido', '한강'), ('yeouido', '공원'), ('yeouido', '벚꽃'), ('yeouido', '자전거'), ('yeouido', '여유'),
('gwanghwamun', '역사'), ('gwanghwamun', '고궁'), ('gwanghwamun', '문화'), ('gwanghwamun', '박물관'), ('gwanghwamun', '전통'),
('dongdaemun', '쇼핑'), ('dongdaemun', '디자인'), ('dongdaemun', '야경'), ('dongdaemun', '문화'), ('dongdaemun', '활기'),
('yongsan', '공원'), ('yongsan', '쇼핑몰'), ('yongsan', '한강뷰'), ('yongsan', '복합문화'), ('yongsan', '현대적'),
('kondae', '대학가'), ('kondae', '젊음'), ('kondae', '클럽'), ('kondae', '저렴'), ('kondae', '활기'),
('wangsimni', '로컬'), ('wangsimni', '맛집'), ('wangsimni', '전통시장'), ('wangsimni', '신개발'), ('wangsimni', '조화'),
('myeongdong', '쇼핑'), ('myeongdong', '루프탑'), ('myeongdong', '레트로'), ('myeongdong', '야경'), ('myeongdong', '관광'),
('apgujeong', '럭셔리'), ('apgujeong', '고급'), ('apgujeong', '갤러리'), ('apgujeong', '편집샵'), ('apgujeong', '트렌디'),
('sillim', '대학가'), ('sillim', '저렴'), ('sillim', '맛집'), ('sillim', '편안함'), ('sillim', '활기'),
('nowon', '자연'), ('nowon', '등산'), ('nowon', '카페거리'), ('nowon', '편안함'), ('nowon', '힐링')
ON CONFLICT DO NOTHING;

-- Insert keywords for Gyeonggi regions
INSERT INTO region_keywords (region_id, keyword) VALUES
('bundang', '신도시'), ('bundang', '공원'), ('bundang', '쾌적'), ('bundang', '쇼핑'), ('bundang', '편안함'),
('suwon', '역사'), ('suwon', '화성'), ('suwon', '전통시장'), ('suwon', '문화'), ('suwon', '맛집'),
('ilsan', '호수'), ('ilsan', '공원'), ('ilsan', '자연'), ('ilsan', '쇼핑'), ('ilsan', '여유'),
('pangyo', '모던'), ('pangyo', 'IT'), ('pangyo', '세련됨'), ('pangyo', '카페'), ('pangyo', '현대적'),
('gwacheon', '자연'), ('gwacheon', '공원'), ('gwacheon', '동물원'), ('gwacheon', '과학관'), ('gwacheon', '힐링'),
('namyangju', '강'), ('namyangju', '자연'), ('namyangju', '카페촌'), ('namyangju', '드라이브'), ('namyangju', '여유'),
('yongin', '테마파크'), ('yongin', '놀이공원'), ('yongin', '문화체험'), ('yongin', '즐거움'), ('yongin', '활동적'),
('anyang', '예술'), ('anyang', '공원'), ('anyang', '자연'), ('anyang', '문화'), ('anyang', '힐링'),
('goyang', '전시'), ('goyang', '공연'), ('goyang', '호수'), ('goyang', '쇼핑'), ('goyang', '복합문화'),
('seongnam', '복합도시'), ('seongnam', '쇼핑'), ('seongnam', '문화'), ('seongnam', '카페'), ('seongnam', '편리함')
ON CONFLICT DO NOTHING;
