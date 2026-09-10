package Practice.Day72

// 응용 과제: 모바일 미디어 썸네일 병렬 처리 파이프라인 (Thumbnail Processing Pipeline)
// 어제 다룬 단일 async/await 패턴에서 한 단계 확장하여, 오늘은 요청하셨던 "고차 함수(람다 전달) + map + awaitAll()" 조합을 집중 실습합니다.
// 임의의 데이터 리스트를 받아 외부에서 주입한 비동기 변환 로직(람다)을 각 요소에 병렬 적용하고
// 결과를 한 번에 회수하는 모바일 비동기 배치 변환 파이프라인(체감 난이도 중하) 과제입니다.

// 테스트 케이스

import Practice.Day72.engine.ParallelTransformEngine
import Practice.Day72.model.*
import kotlinx.coroutines.*

fun main() = runBlocking {
    val engine = ParallelTransformEngine()

    // 4개의 미디어 파일 준비
    val mediaList = listOf(
        MediaFile("M-01", "intro.mp4", 1200),
        MediaFile("M-02", "profile.jpg", 300),
        MediaFile("M-03", "corrupted.png", 0), // 용량이 0이라 에러 발생 예정
        MediaFile("M-04", "ending.mp4", 1500)
    )

    println("=== 고차 함수 + awaitAll 기반 병렬 변환 파이프라인 가동 ===")

    // 고차 함수(람다)를 넘겨서 실행: 각 파일마다 100ms 걸리는 비동기 압축 작업
    val report: BatchReport<String> = engine.transformAll(mediaList) { file ->
        println("-> [작업 시작] ${file.fileName} 처리 중...")
        delay(100) // 각 작업마다 100ms 소요

        // 용량이 0 이하이면 예외 발생 테스트
        if (file.fileSizeKb <= 0) {
            throw IllegalArgumentException("손상된 미디어 파일입니다: ${file.fileName}")
        }

        // 압축 성공 결과 문자열 반환
        "COMPRESSED_${file.fileName} (${file.fileSizeKb / 2}KB)"
    }

    println("\n=== 파이프라인 실행 결과 리포트 ===")
    println("총 파일 수: ${report.totalCount}")
    println("총 소요 시간: ${report.totalElapsedMs}ms (4개 작업이 병렬이므로 100ms 초반대 기대)")
    println("상세 결과 목록:")
    report.results.forEach { result ->
        when (result) {
            is ProcessResult.Success -> println("  [성공] ${result.data}")
            is ProcessResult.Error -> println("  [실패] 사유: ${result.message}")
        }
    }
}