package zozzamas

import java.util.NoSuchElementException

import scala.Array.copyOf
import scala.collection.{StrictOptimizedIterableOps, mutable}
import scala.collection.mutable.ArrayBuffer

class Storage[T]
  extends mutable.Map[Entity, T]
    with mutable.MapOps[Entity, T, mutable.Map, Storage[T]]
    with StrictOptimizedIterableOps[(Entity, T), mutable.Iterable, Storage[T]] :

  private val store = ArrayBuffer[T]()
  private val index = SparseSet()

  override def get(key: Entity): Option[T] = index.index(key) match {
    case -1 => None
    case x => Some(store(x))
  }

  override def iterator: Iterator[(Entity, T)] = index.zip(store).iterator

  override def addOne(entry: (Entity, T)) = entry match {
    case (key, value) =>
      index.index(key) match {
        case -1 =>
          store.append(value)
          index.add(key)
          this
        case l =>
          store(l) = value
          this
      }
  }

  override def subtractOne(key: Entity) =
    index.index(key) match{
      case -1 => this
      case x =>
        store(x) = store.last
        store.trimEnd(1)
        index.remove(key)
        this
    }
