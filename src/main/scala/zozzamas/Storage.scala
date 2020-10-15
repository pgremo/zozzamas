package zozzamas

import scala.collection.mutable.ArrayBuffer

class Storage[T]:
  private val packed = ArrayBuffer[T]()
  private var sparse = Array[Int]()

  def put(key: Int, value: T) =
    packed.append(value)
    sparse = sparse.length match {
      case x if key + 1 > x => Array.copyOf(sparse, key + 1)
      case _ => sparse
    }
    sparse.updated(key, packed.size)

  def get(key: Int): T = packed(sparse(key))

  def remove(key: Int) = sparse.update(key, 0)
