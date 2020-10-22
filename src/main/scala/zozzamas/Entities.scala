package zozzamas

import scala.collection.mutable.ArrayBuffer


type Entity = Int

private val idMask = 0x000FFFFF
private val versionMask = 0x7FF00000
private val idShift = Integer.bitCount(idMask)

private var released: Int = 0
private val entities = ArrayBuffer[Int](0)

object Entity:
  def apply(): Entity = released match {
    case 0 => entities.size match {
      case index if index < idMask =>
        entities.append(index)
        index
      case _ => throw IndexOutOfBoundsException()
    }
    case _ =>
      val entry = entities(released)
      val result = released | entry & versionMask
      entities.update(released, result)
      released = entry & idMask
      result
  }

  def unapply(x: Entity): (Int, Int) = (x & idMask, (x & versionMask) >> idShift)

extension (entity: Entity):
  def release() =
    val id = entity & idMask
    val version = (entities(id) & versionMask) | (1 << idShift)
    entities.update(id, released | version)
    released = id

