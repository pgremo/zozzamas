package zozzamas

import java.util.Date

import org.junit.Test

import scala.collection.Set
import scala.collection.mutable.Map
import scala.collection.mutable.HashMap


given intItems as Storage[Int] = SparseMap[Int]()
given stringItems as Storage[String] = SparseMap[String]()
given dateItems as Storage[Date] = SparseMap[Date]()
given longItems as Storage[Long] = SparseMap[Long]()

class RegistryTest {
  @Test def query(): Unit = {
    val entity = Entity()

    Registry(entity) = 44
    Registry(entity) = "hello"
    Registry(entity) = Date()

    val lookup = Registry.view[(Int, String, Date)]
    println(lookup)

    var exec = (n: Int, s: String, d: Date) => println(s"${n}, ${s}, ${d}")

    val view = View(lookup)
    view.components.foreach(exec.tupled)
    println(view.get[Date](view.entities.head))
  }
}