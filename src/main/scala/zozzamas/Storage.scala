package zozzamas

import java.util.NoSuchElementException

import scala.Array.copyOf
import scala.collection.{StrictOptimizedIterableOps, mutable}
import scala.collection.mutable.ArrayBuffer

class Storage[T]
  extends mutable.Map[Int, T]
    with mutable.MapOps[Int, T, mutable.Map, Storage[T]]
    with StrictOptimizedIterableOps[(Int, T), mutable.Iterable, Storage[T]] :

  private val store = ArrayBuffer[T]()
  private val index = SparseSet()

  def get(key: Int): Option[T] = index.index(key).map(store)

  def iterator: Iterator[(Int, T)] = index.zip(store).iterator

  def addOne(entry: (Int, T)) = entry match {
    case (key, value) =>
      store.append(value)
      index.add(key)
      this
  }

  def subtractOne(key: Int) =
    index.index(key).map(entry => {
      store(entry) = store.last
      store.trimEnd(1)
      index.remove(key)
    })
    this
