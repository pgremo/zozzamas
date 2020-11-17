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
given longItems as Storage[Long] = SparseMap[Long]()

type ComponentOf = [V] =>> V match {
    case Storage[a] => a
  }

def register[C](entity: Entity, value: C)(using storage: Storage[C]): Unit = storage(entity) = value

def get[C](entity: Entity)(using storage: Storage[C]): C = storage(entity)

inline def makeView[T <: Tuple]: Tuple = {
  inline erasedValue[T] match {
    case _: EmptyTuple => EmptyTuple
    case _: (head *: tail) => summonInline[Storage[head]] *: makeView[tail]
  }
}

type Homogenous[T <: Tuple, H] = T match {
  case EmptyTuple => DummyImplicit
  case H *: t => Homogenous[t, H]
  case ? => Nothing
}

class View[T <: Tuple, I <: Tuple.InverseMap[T, Storage]](private val source: T)(using Homogenous[T, Storage[?]]){
  def entities: Set[Entity] =
    source.productIterator
      .map(_.asInstanceOf[Map[Entity, ?]].keySet)
      .reduce(_ intersect _)

  def extract(i: Entity)(using Tuple.IsMappedBy[Storage][T]): I =
    source.map {
      [M] => (m: M) => m.asInstanceOf[Storage[?]](i).asInstanceOf[ComponentOf[M]]
    }.asInstanceOf[I]
    
  def components(using Tuple.IsMappedBy[Storage][T]): Set[I] =
    entities.map(extract)

  def get[K <: Tuple.Union[Tuple.InverseMap[T, Storage]]](i: Entity)(using storage: Storage[K]): K =
    storage(i)
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