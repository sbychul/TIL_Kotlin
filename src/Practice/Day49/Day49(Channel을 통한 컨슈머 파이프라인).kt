package Practice.Day49

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

// 문제: 멀티 작업 처리 파이프라인 (Worker Pipeline with Channels)
// 작업 요청(Task)을 생산하는 Producer와, 이 작업들을 가져와 비동기로 나눠서 처리하는 Worker (Consumer) 파이프라인을 작성하세요.

// 기본 데이터 클래스 구조
data class Task(val id: Int, val payload: String)

// produce 빌더를 통해 생산자 코루틴을 만들어 주는 함수.
// produce 블록 내 send()를 통해 데이터를 발행하고, produce 블록이 종료되면 채널을 자동으로 닫아 준다.
// Channel: 각 코루틴 사이에서 안전하게 데이터를 주고받는 비동기 통로(큐)
fun CoroutineScope.produceTasks(count: Int): ReceiveChannel<Task> = produce {
    // send()를 이용하여 1부터 count까지의 Task 객체를 생성하여 발행(채널에 넣음).
    // 각 작업 생산 이후 0.1초간 대기.
    for (id in 1..count) {
        send(Task(id, "Task Payload #$id"))
        delay(100)
    }
}

// 채널이 닫힐 때까지 반복해서 Task를 수신(receive(): 데이터가 올 때까지 대기, 채널에서 데이터를 꺼내는 함수)하는 함수.
suspend fun processTask(workerId: String, channel: ReceiveChannel<Task>) {
    for (task in channel) { // 해당 구문 자체가 내부적으로 receive()를 호출한다. 직접 receive()를 호출하는 것보다 안전한 방법.
        delay(200) // 수신할 때마다 0.2초 대기
        println("[$workerId] 작업 처리 완료: Task #${task.id}") // 로그를 콘솔에 출력
    }
}

// 테스트 케이스
fun main() = runBlocking {
    println("=== 작업 파이프라인 가동 ===")

    val taskChannel = produceTasks(5) // 5개의 작업을 가진 채널을 생성

    // 1개의 채널을 launch를 이용하여 병렬 실행.
    val worker1 = launch { processTask("Worker-1", taskChannel) }
    val worker2 = launch { processTask("Worker-2", taskChannel) }

    joinAll(worker1, worker2) // 2개의 worker 코루틴의 모든 작업이 완료될 때까지 대기.
    println("=== 모든 작업 처리 완료 ===")
}