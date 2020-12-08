package zozzamas

import org.junit.{BeforeClass, Test}

import java.util.Date
import scala.collection.Set
import scala.collection.mutable.{HashMap, Map}

type Storage[X] = Map[Entity, X]

given intItems as Storage[Int] = SparseMap[Int]()
given stringItems as Storage[String] = SparseMap[String]()
given dateItems as Storage[Date] = SparseMap[Date]()
given longItems as Storage[Long] = SparseMap[Long]()

def printThem()(using ints: Storage[Int], strings: Storage[String], dates: Storage[Date]) = {
  val count = (for (
    (k, i) <- ints;
    s <- strings.get(k);
    d <- dates.get(k)
  ) yield (i, s, d)).size
  println(count)
}

object RegistryTest {
  @BeforeClass def setup(): Unit = {
    (0 to 100000).foreach(_ => {
      val entity = Entity()

      intItems(entity) = 44
      stringItems(entity) = "hello"
      dateItems(entity) = Date()
    })
  }
}

class RegistryTest {
  @Test def comp(): Unit = {
    printThem()
  }
}