package zozzamas

import java.util.Date

import org.junit.Test

import scala.collection.Set
import scala.collection.mutable.Map
import scala.collection.mutable.HashMap
import scala.compiletime._


type Storage[X] = Map[Entity, X]

given intItems as Storage[Int] = SparseMap[Int]()
given stringItems as Storage[String] = SparseMap[String]()
given dateItems as Storage[Date] = SparseMap[Date]()

type ComponentOf = [V] =>> V match {
    case Storage[a] => a
  }

def extract[T <: Tuple, I <: Tuple.InverseMap[T, Storage]](t: T, i: Entity)(using Tuple.IsMappedBy[Storage][T]): I =
  t.map {
    [M] => (m: M) => m.asInstanceOf[Storage[_]](i).asInstanceOf[ComponentOf[M]]
  }.asInstanceOf[I]

def transform[T <: Tuple, I <: Tuple.InverseMap[T, Storage], B](
    t: T,
    f: I => B,
    i: Entity                                                           
  )(using Tuple.IsMappedBy[Storage][T]): B  = f(extract(t, i))

def register[C](entity: Entity, value: C)(using storage: Storage[C]): Unit = storage(entity) = value

def get[C](entity: Entity)(using storage: Storage[C]): C = storage(entity)


class RegistryTest {
  @Test def query(): Unit = {
    val entity1 = Entity()

    register(entity1, 44)
    register(entity1, "hello")
    register(entity1, Date())

    val items = (intItems, stringItems, dateItems)

    var keys = items.productIterator
      .map(_.asInstanceOf[Map[Entity, ?]].keySet)
      .reduce(_ intersect _)
    println(keys)

    var entities = keys.map(transform(items, x => x, _))
    println(entities)

    var exec = (n: Int, s: String, d: Date) => println(s"${n}, ${s}, ${d}")
    entities.foreach(exec.tupled)
  }
}