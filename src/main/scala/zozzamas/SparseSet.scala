package zozzamas

import java.util.NoSuchElementException

import scala.Array.copyOf
import scala.collection.{IterableFactoryDefaults, StrictOptimizedIterableOps, mutable}
import scala.collection.mutable.{AbstractSet, ArrayBuffer}

class SparseSet
  extends AbstractSet[Int]
    with mutable.SetOps[Int, Set, SparseSet]
    with StrictOptimizedIterableOps[Int, Set, SparseSet] {

  private var packed = ArrayBuffer[Int]()
  private var sparse = Array[Integer]()

  override def addOne(key: Int) = {
    sparse = key match {
      case index if index >= sparse.length => copyOf(sparse, index + 1)
      case _ => sparse
    }
    sparse(key) match {
      case _: Int => this
      case _ =>
        packed.append(key)
        sparse(key) = packed.size - 1
        this
    }
  }

  override def contains(key: Int) = key match {
    case x if x >= sparse.length => false
    case _ => sparse(key).isInstanceOf[Int]
  }

  override def subtractOne(key: Int) =
    sparse(key) match {
      case index: Int =>
        packed(index) = packed.last
        sparse(packed.last) = index
        sparse(key) = null
        packed.dropRightInPlace(1)
        this
      case _ => this
    }

  override def clear(): Unit = {
    packed = ArrayBuffer[Int]()
    sparse = Array[Integer]()
  }

  override def iterator = packed.iterator

  def index(key: Int): Int = key match {
    case x if x >= sparse.length => -1
    case _ => sparse(key) match {
      case k: Int => k
      case _ => -1
    }
  }
}
