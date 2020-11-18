package zozzamas

import java.util.Date

import org.junit.Test

import scala.collection.Set
import scala.collection.mutable.Map
import scala.collection.mutable.HashMap
import scala.compiletime._


given intItems as Storage[Int] = SparseMap[Int]()
given stringItems as Storage[String] = SparseMap[String]()
given dateItems as Storage[Date] = SparseMap[Date]()
given longItems as Storage[Long] = SparseMap[Long]()

inline def makeView[T <: Tuple]: Tuple = {
  inline erasedValue[T] match {
    case _: EmptyTuple => EmptyTuple
    case _: (head *: tail) => summonInline[Storage[head]] *: makeView[tail]
  }
}

class RegistryTest {
  @Test def query(): Unit = {
    val entity = Entity()

    register(entity, 44)
    register(entity, "hello")
    register(entity, Date())
    
    val lookup = makeView[(Int, String, Date)]
    println(lookup)

    val items = (intItems, stringItems, dateItems)

    var exec = (n: Int, s: String, d: Date) => println(s"${n}, ${s}, ${d}")

    val view = View(items)
    view.components.foreach(exec.tupled)
    println(view.get[Date](entity))
  }
}