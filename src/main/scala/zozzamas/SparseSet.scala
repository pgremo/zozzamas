package zozzamas

import java.util.NoSuchElementException

import scala.Array.copyOf
import scala.collection.{IterableFactoryDefaults, StrictOptimizedIterableOps, mutable}
import scala.collection.mutable.{AbstractSet, ArrayBuffer}

class SparseSet
  extends AbstractSet[Entity]
    with mutable.SetOps[Entity, Set, SparseSet]
    with StrictOptimizedIterableOps[Entity, Set, SparseSet] :

  private var packed = ArrayBuffer[Entity]()
  private var sparse = Array[Entity | Null]()

  override def addOne(key: Entity) =
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

  override def contains(key: Entity) = key match {
    case x if x > sparse.length => false
    case _ => sparse(key).isInstanceOf[Entity]
  }

  override def subtractOne(key: Entity) =
    sparse(key) match {
      case index: Entity =>
        packed(index) = packed.last
        sparse(packed.last) = index
        sparse(key) = null
        packed.trimEnd(1)
        this
      case _ => this
    }

  override def clear(): Unit =
    packed = ArrayBuffer[Entity]()
    sparse = Array[Entity | Null]()

  override def iterator = packed.iterator

  def index(key: Entity): Entity = key match {
    case x if x > sparse.length => -1
    case _ => sparse(key) match {
      case null => -1
      case k:Entity => k
    }
  }

