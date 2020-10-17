package zozzamas

import java.util.NoSuchElementException
import scala.Array.copyOf
import scala.collection.mutable.ArrayBuffer

class Storage[T]:
  private val values = ArrayBuffer[T]()
  private val packed = ArrayBuffer[Int]()
  private var sparse = Array[Int | Null]()

  def put(key: Int, value: T) =
    values.append(value)
    sparse = sparse.length match {
      case x if key + 1 > x => copyOf(sparse, key + 1)
      case _ => sparse
    }
    packed.append(key)
    sparse.update(key, packed.size - 1)

  def get(key: Int): Option[T] =
    sparse(key) match {
      case index: Int => Some(values(index))
      case _ => None
    }

  def remove(key: Int) =
    sparse(key) match {
      case index: Int =>
        values(index) = values.last
        values.remove(values.length - 1)
        packed(index) = packed.last
        sparse.update(packed.last, index)
        sparse.update(key, null)
        packed.remove(packed.length - 1)
      case _ => throw NoSuchElementException()
    }
