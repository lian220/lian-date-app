package com.dateclick.api.infrastructure.config

import com.dateclick.api.infrastructure.persistence.region.RegionEntity
import com.dateclick.api.infrastructure.persistence.region.RegionJpaRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

/**
 * Local-only data loader for quick local testing.
 * Production environment uses Flyway migrations (V2__insert_regions_data.sql) instead.
 *
 * This loader is restricted to local profile only to avoid:
 * - Race conditions on concurrent application startups in production
 * - Duplicate key violations from non-atomic count-then-insert pattern
 *
 * @see db.migration.V2__insert_regions_data.sql for production-safe idempotent inserts with ON CONFLICT DO NOTHING
 */
@Component
class RegionDataLoader(
    private val regionJpaRepository: RegionJpaRepository,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        if (regionJpaRepository.count() > 0) {
            return
        }

        val regions =
            listOf(
                // 서울 권역
                RegionEntity(
                    id = "gangnam",
                    name = "강남",
                    city = "seoul",
                    description = "트렌디하고 세련된 분위기의 강남 지역. 고급 레스토랑, 카페, 쇼핑몰이 밀집해 있어 데이트 코스로 인기가 많습니다.",
                    keywords = listOf("트렌디", "쇼핑", "고급", "세련됨", "핫플레이스"),
                    centerLat = 37.4979,
                    centerLng = 127.0276,
                ),
                RegionEntity(
                    id = "hongdae",
                    name = "홍대",
                    city = "seoul",
                    description = "젊음과 예술이 공존하는 홍대 지역. 독특한 카페, 공연장, 클럽 등 다양한 문화 공간이 있어 활기찬 데이트를 즐길 수 있습니다.",
                    keywords = listOf("젊음", "예술", "문화", "활기", "인디"),
                    centerLat = 37.5563,
                    centerLng = 126.9236,
                ),
                RegionEntity(
                    id = "itaewon",
                    name = "이태원",
                    city = "seoul",
                    description = "다국적 문화가 어우러진 이태원. 세계 각국의 음식과 독특한 바, 루프탑 등 이국적인 분위기를 느낄 수 있습니다.",
                    keywords = listOf("이국적", "다문화", "루프탑", "세계음식", "나이트"),
                    centerLat = 37.5344,
                    centerLng = 126.9944,
                ),
                RegionEntity(
                    id = "jamsil",
                    name = "잠실",
                    city = "seoul",
                    description = "한강과 롯데월드가 있는 잠실. 쇼핑과 엔터테인먼트, 한강 산책을 함께 즐길 수 있는 복합 데이트 명소입니다.",
                    keywords = listOf("한강", "쇼핑", "놀이공원", "산책", "복합문화"),
                    centerLat = 37.5133,
                    centerLng = 127.1000,
                ),
                RegionEntity(
                    id = "yeonnam",
                    name = "연남동",
                    city = "seoul",
                    description = "감각적인 카페와 레스토랑이 즐비한 연남동. 경의선숲길을 따라 여유로운 산책과 맛집 탐방을 즐길 수 있습니다.",
                    keywords = listOf("감각적", "카페", "맛집", "산책로", "힐링"),
                    centerLat = 37.5664,
                    centerLng = 126.9258,
                ),
                RegionEntity(
                    id = "seongsu",
                    name = "성수",
                    city = "seoul",
                    description = "공장지대에서 힙한 문화공간으로 변모한 성수동. 독특한 카페와 편집샵, 갤러리가 모여있는 감성 데이트 명소입니다.",
                    keywords = listOf("힙", "감성", "공간", "브루클린", "편집샵"),
                    centerLat = 37.5447,
                    centerLng = 127.0557,
                ),
                RegionEntity(
                    id = "hannam",
                    name = "한남동",
                    city = "seoul",
                    description = "한강뷰와 고급스러운 분위기의 한남동. 프라이빗한 레스토랑과 루프탑 바가 많아 특별한 데이트에 제격입니다.",
                    keywords = listOf("고급", "한강뷰", "프라이빗", "루프탑", "특별함"),
                    centerLat = 37.5349,
                    centerLng = 127.0026,
                ),
                RegionEntity(
                    id = "bukchon",
                    name = "북촌/삼청동",
                    city = "seoul",
                    description = "한옥마을과 갤러리가 있는 전통과 현대가 공존하는 지역. 문화적 분위기에서 로맨틱한 데이트를 즐길 수 있습니다.",
                    keywords = listOf("한옥", "전통", "갤러리", "문화", "로맨틱"),
                    centerLat = 37.5825,
                    centerLng = 126.9833,
                ),
                RegionEntity(
                    id = "mapo",
                    name = "마포/망원",
                    city = "seoul",
                    description = "망원시장과 한강공원이 어우러진 마포. 레트로 감성의 카페와 맛집이 가득한 데이트 핫플레이스입니다.",
                    keywords = listOf("레트로", "시장", "한강", "맛집", "감성"),
                    centerLat = 37.5560,
                    centerLng = 126.9099,
                ),
                RegionEntity(
                    id = "garosu",
                    name = "가로수길/신사",
                    city = "seoul",
                    description = "플라타너스 가로수가 아름다운 가로수길. 개성 있는 부티크와 카페, 레스토랑이 즐비한 패션 데이트 명소입니다.",
                    keywords = listOf("패션", "부티크", "카페", "세련됨", "트렌디"),
                    centerLat = 37.5197,
                    centerLng = 127.0229,
                ),
                RegionEntity(
                    id = "yeouido",
                    name = "여의도",
                    city = "seoul",
                    description = "한강공원과 벚꽃축제로 유명한 여의도. 넓은 공원과 한강뷰 레스토랑에서 여유로운 데이트를 즐길 수 있습니다.",
                    keywords = listOf("한강", "공원", "벚꽃", "자전거", "여유"),
                    centerLat = 37.5219,
                    centerLng = 126.9245,
                ),
                RegionEntity(
                    id = "gwanghwamun",
                    name = "광화문/종로",
                    city = "seoul",
                    description = "역사와 문화의 중심 광화문. 고궁과 박물관, 전통찻집이 어우러진 문화 데이트 코스입니다.",
                    keywords = listOf("역사", "고궁", "문화", "박물관", "전통"),
                    centerLat = 37.5720,
                    centerLng = 126.9769,
                ),
                RegionEntity(
                    id = "dongdaemun",
                    name = "동대문/DDP",
                    city = "seoul",
                    description = "24시간 쇼핑과 DDP 디자인공간이 공존하는 동대문. 쇼핑과 문화를 함께 즐기는 활기찬 데이트가 가능합니다.",
                    keywords = listOf("쇼핑", "디자인", "야경", "문화", "활기"),
                    centerLat = 37.5665,
                    centerLng = 127.0098,
                ),
                RegionEntity(
                    id = "yongsan",
                    name = "용산",
                    city = "seoul",
                    description = "용산공원과 아이파크몰이 있는 용산. 한강뷰와 쇼핑, 문화공간이 어우러진 복합 데이트 지역입니다.",
                    keywords = listOf("공원", "쇼핑몰", "한강뷰", "복합문화", "현대적"),
                    centerLat = 37.5311,
                    centerLng = 126.9644,
                ),
                RegionEntity(
                    id = "kondae",
                    name = "건대",
                    city = "seoul",
                    description = "대학가 특유의 활기와 젊음이 넘치는 건대. 저렴하고 다양한 맛집과 카페, 클럽이 모여있는 청춘 데이트 명소입니다.",
                    keywords = listOf("대학가", "젊음", "클럽", "저렴", "활기"),
                    centerLat = 37.5403,
                    centerLng = 127.0695,
                ),
                RegionEntity(
                    id = "wangsimni",
                    name = "왕십리/성동",
                    city = "seoul",
                    description = "전통시장과 신개발이 공존하는 왕십리. 로컬 맛집과 새로운 카페가 조화를 이루는 데이트 지역입니다.",
                    keywords = listOf("로컬", "맛집", "전통시장", "신개발", "조화"),
                    centerLat = 37.5610,
                    centerLng = 127.0377,
                ),
                RegionEntity(
                    id = "myeongdong",
                    name = "명동/을지로",
                    city = "seoul",
                    description = "쇼핑의 메카 명동과 레트로 감성의 을지로. 낮에는 쇼핑, 밤에는 루프탑 바에서 특별한 데이트를 즐길 수 있습니다.",
                    keywords = listOf("쇼핑", "루프탑", "레트로", "야경", "관광"),
                    centerLat = 37.5636,
                    centerLng = 126.9838,
                ),
                RegionEntity(
                    id = "apgujeong",
                    name = "압구정/청담",
                    city = "seoul",
                    description = "럭셔리와 트렌디가 공존하는 압구정/청담. 고급 레스토랑과 갤러리, 편집샵이 즐비한 프리미엄 데이트 지역입니다.",
                    keywords = listOf("럭셔리", "고급", "갤러리", "편집샵", "트렌디"),
                    centerLat = 37.5274,
                    centerLng = 127.0280,
                ),
                RegionEntity(
                    id = "sillim",
                    name = "신림",
                    city = "seoul",
                    description = "대학가 주변의 활기찬 신림. 저렴한 맛집과 카페가 많아 부담 없는 데이트를 즐길 수 있습니다.",
                    keywords = listOf("대학가", "저렴", "맛집", "편안함", "활기"),
                    centerLat = 37.4842,
                    centerLng = 126.9295,
                ),
                RegionEntity(
                    id = "nowon",
                    name = "노원",
                    city = "seoul",
                    description = "불암산과 수락산이 가까운 노원. 자연 친화적인 데이트와 카페거리를 함께 즐길 수 있는 북부의 데이트 명소입니다.",
                    keywords = listOf("자연", "등산", "카페거리", "편안함", "힐링"),
                    centerLat = 37.6542,
                    centerLng = 127.0568,
                ),
                // 경기 권역
                RegionEntity(
                    id = "bundang",
                    name = "분당",
                    city = "gyeonggi",
                    description = "깔끔하고 계획적으로 조성된 분당 신도시. 넓은 공원과 쇼핑몰, 카페거리가 잘 갖춰져 있어 편안한 데이트가 가능합니다.",
                    keywords = listOf("신도시", "공원", "쾌적", "쇼핑", "편안함"),
                    centerLat = 37.3836,
                    centerLng = 127.1217,
                ),
                RegionEntity(
                    id = "suwon",
                    name = "수원",
                    city = "gyeonggi",
                    description = "수원화성과 행궁동 벽화마을이 있는 수원. 역사와 문화, 맛집이 어우러진 다채로운 데이트를 즐길 수 있습니다.",
                    keywords = listOf("역사", "화성", "전통시장", "문화", "맛집"),
                    centerLat = 37.2636,
                    centerLng = 127.0286,
                ),
                RegionEntity(
                    id = "ilsan",
                    name = "일산",
                    city = "gyeonggi",
                    description = "호수공원과 라페스타가 있는 일산. 자연과 쇼핑, 문화시설이 조화를 이루어 다양한 데이트 코스를 즐길 수 있습니다.",
                    keywords = listOf("호수", "공원", "자연", "쇼핑", "여유"),
                    centerLat = 37.6583,
                    centerLng = 126.7732,
                ),
                RegionEntity(
                    id = "pangyo",
                    name = "판교",
                    city = "gyeonggi",
                    description = "IT 기업과 현대적 건물들이 모인 판교. 세련된 카페와 레스토랑, 문화공간이 많아 모던한 데이트를 즐길 수 있습니다.",
                    keywords = listOf("모던", "IT", "세련됨", "카페", "현대적"),
                    centerLat = 37.3951,
                    centerLng = 127.1106,
                ),
                RegionEntity(
                    id = "gwacheon",
                    name = "과천",
                    city = "gyeonggi",
                    description = "서울대공원과 국립과천과학관이 있는 과천. 자연과 문화를 함께 즐길 수 있는 힐링 데이트 코스입니다.",
                    keywords = listOf("자연", "공원", "동물원", "과학관", "힐링"),
                    centerLat = 37.4292,
                    centerLng = 127.0028,
                ),
                RegionEntity(
                    id = "namyangju",
                    name = "남양주",
                    city = "gyeonggi",
                    description = "북한강과 다산생태공원이 있는 남양주. 자연 속에서 여유로운 시간을 보내기 좋은 데이트 명소입니다.",
                    keywords = listOf("강", "자연", "카페촌", "드라이브", "여유"),
                    centerLat = 37.6361,
                    centerLng = 127.2164,
                ),
                RegionEntity(
                    id = "yongin",
                    name = "용인",
                    city = "gyeonggi",
                    description = "에버랜드와 한국민속촌이 있는 용인. 테마파크와 문화체험을 함께 즐길 수 있는 종합 데이트 지역입니다.",
                    keywords = listOf("테마파크", "놀이공원", "문화체험", "즐거움", "활동적"),
                    centerLat = 37.2411,
                    centerLng = 127.1776,
                ),
                RegionEntity(
                    id = "anyang",
                    name = "안양",
                    city = "gyeonggi",
                    description = "안양예술공원과 안양천이 있는 안양. 예술과 자연이 어우러진 문화 데이트를 즐길 수 있습니다.",
                    keywords = listOf("예술", "공원", "자연", "문화", "힐링"),
                    centerLat = 37.3943,
                    centerLng = 126.9568,
                ),
                RegionEntity(
                    id = "goyang",
                    name = "고양/킨텍스",
                    city = "gyeonggi",
                    description = "킨텍스 전시장과 호수공원이 있는 고양. 전시/공연과 자연을 함께 즐기는 복합 데이트 지역입니다.",
                    keywords = listOf("전시", "공연", "호수", "쇼핑", "복합문화"),
                    centerLat = 37.6688,
                    centerLng = 126.7458,
                ),
                RegionEntity(
                    id = "seongnam",
                    name = "성남",
                    city = "gyeonggi",
                    description = "분당과 판교를 포함한 성남. 신구도시가 공존하며 다양한 문화와 쇼핑을 즐길 수 있는 데이트 지역입니다.",
                    keywords = listOf("복합도시", "쇼핑", "문화", "카페", "편리함"),
                    centerLat = 37.4201,
                    centerLng = 127.1262,
                ),
            )

        regionJpaRepository.saveAll(regions)
    }
}
