package Practice.Day51

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*

// 문제: 스레드 안전한 실시간 은행 계좌 (Thread-Safe Bank Account)
// 100개의 코루틴이 동시에 접근하여 출금 및 입금을 수행하더라도 잔액(Balance) 데이터가 정확하게 유지되는 BankAccount 클래스와 비동기 처리 로직을 구현하세요.

class BankAccount(initialBalance: Int) {
    private var balance = initialBalance
    private val mutex = Mutex() // 동시성 제어를 위한 Mutex 객체

    // 입금 함수: balance에 amount를 더함 (Thread-Safe)
    suspend fun deposit(amount: Int) {
        // Mutex: 공유 자원에 한 번에 하나의 실행 흐름만 접근하도록 막는 자물쇠
        // mutex.withLock 블록을 활용해 동시 접근을 제어.
        // withLock 블록 내부에는 동시에 한 코루틴만 들어갈 수 있고,
        // 락을 획득할 수 없을 때 스레드를 차단(Block)하지 않고 코루틴을 일시 중단(Suspend)시켰다가 락이 해제되면 다시 깨어난다.
        mutex.withLock { balance += amount }
    }

    // 출금 함수: balance가 amount 이상일 때만 차감하고 true 반환, 부족하면 false 반환 (Thread-Safe)
    suspend fun withdraw(amount: Int): Boolean {
        mutex.withLock {
            return if (balance >= amount) {
                balance -= amount
                true
            }
            else false
        }
    }

    // 잔액 조회 함수
    suspend fun getBalance(): Int = mutex.withLock { return balance }
}

// 테스트 케이스
fun main() = runBlocking {
    val account = BankAccount(10000)

    println("=== 동시성 입출금 테스트 시작 ===")

    // 100개의 코루틴이 동시에 입출금 수행
    coroutineScope {
        repeat(100) {
            launch(Dispatchers.Default) { // 멀티스레드 환경에서 실행
                account.deposit(100)
                account.withdraw(50)
            }
        }
    }

    println("최종 잔액: ${account.getBalance()}원")
    // 기대 출력: 최종 잔액: 15000원
}