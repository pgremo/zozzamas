package zozzamas

import java.util.NoSuchElementException

import scala.Array.copyOf
import scala.collection.{IterableFactoryDefaults, StrictOptimizedIterableOps, mutable}
import scala.collection.mutable.{AbstractSet, ArrayBuffer}

class SparseSet
  extends AbstractSet[Int]
    with mutable.SetOps[Int, Set, SparseSet]
    with StrictOptimizedIterableOps[Int, Set, SparseSet] :

  private var packed = ArrayBuffer[Int]()
  private var sparse = Array[Int | Null]()

  override def addOne(key: Int) =
    sparse = key + 1 match {
      case index if index > sparse.length => copyOf(sparse, index)
      case _ => sparse
    }
    sparse(key) match {
      case null => {
        packed.append(key)
        sparse(key) = packed.size - 1
        this
      }
      case _ => this
    }

  override def contains(key: Int) = sparse.length match {
    case x if key > x => false
    case _ => sparse(key).isInstanceOf[Int]
  }

  override def subtractOne(key: Int) =
    sparse(key) match {
      case index: Int =>
        packed(index) = packed.last
        sparse(packed.last) = index
        sparse(key) = null
        packed.trimEnd(1)
        this
      case _ => this
    }

  override def clear(): Unit =
    packed = ArrayBuffer[Int]()
    sparse = Array[Int | Null]()

  override def iterator = packed.iterator

  def index(key: Int): Int = key + 1 match {
    case x if x > sparse.length => -1
    case _ => sparse(key) match {
      case null => -1
      case k:Int => k
    }
  }

