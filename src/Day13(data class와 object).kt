// data class, 기본 메서드(toString, equals 등)이 자동으로 내장된 클래스.
data class Product(val id: Int, val name: String, val price: Int) {
    // 데이터를 갖고 있는 객체에 불과하므로 내부에 구현한 것 없음.
}

// object, 언어 차원에서 완벽한 싱글톤 객체를 생성해 주는 키워드.
// 싱글톤 객체: 프로그램 전체에서 하나만 생성되어 여러 곳에서 공유하여 사용하는 객체.
// 생성자 직접 호출 불가. 처음 접근할 때 생성됨.
object ProductRepository {
    val products = mutableListOf<Product>()

    // 객체 추가 메서드
    // 외부에서 접근 시 static 멤버에 접근하듯이 바로 호출함.
    fun addProduct(product: Product) {
        products.add(product)
    }
}

fun main() {
    val macBook = Product(1, "맥북", 2190000)
    val iPhone = Product(2, "아이폰", 1790000)

    // 싱글톤 객체 ProductRepository에 바로 접근, 객체 추가.
    ProductRepository.addProduct(macBook) // 이 때 ProductRepository 객체가 생성됨.
    ProductRepository.addProduct(iPhone)

    // 순회하며 출력. data class의 자동 포맷팅을 확인해 보자.
    for (p in ProductRepository.products) println(p);
}