package zozzamas

import java.util.NoSuchElementException

import scala.Array.copyOf
import scala.collection.{StrictOptimizedIterableOps, mutable}
import scala.collection.mutable.ArrayBuffer

class SparseMap[T]
  extends mutable.Map[Int, T]
    with mutable.MapOps[Int, T, mutable.Map, SparseMap[T]]
    with StrictOptimizedIterableOps[(Int, T), mutable.Iterable, SparseMap[T]] {

  private val store = ArrayBuffer[T]()
  private val index = SparseSet()

  override def get(key: Int): Option[T] = index.index(key) match {
    case -1 => None
    case x => Some(store(x))
  }

  override def iterator: Iterator[(Int, T)] = index.zip(store).iterator

  override def addOne(entry: (Int, T)) = entry match {
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

  override def subtractOne(key: Int) =
    index.index(key) match {
      case -1 => this
      case x =>
        store(x) = store.last
        store.dropRightInPlace(1)
        index.remove(key)
        this
    }
}