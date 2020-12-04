package zozzamas

import scala.collection.Set
import scala.collection.mutable.Map

type Storage[X] = Map[Entity, X]

type ComponentOf = [X] =>> X match {
    case Storage[a] => a
  }

class View[T <: Tuple, I <: Tuple.InverseMap[T, Storage]](private val source: T)(using Tuple.IsMappedBy[Storage][T]) {
  def entities: Set[Entity] =
    source.productIterator
      .map(_.asInstanceOf[Map[Entity, ?]].keySet)
      .reduce(_ & _)

  def extract(i: Entity): I =
    source.map {
      [M] => (m: M) => m.asInstanceOf[Storage[?]](i).asInstanceOf[ComponentOf[M]]
    }.asInstanceOf[I]

  def components: Set[I] =
    entities.map(extract)

  def get[K <: Tuple.Union[Tuple.InverseMap[T, Storage]]](i: Entity)(using storage: Storage[K]): K =
    storage(i)
}
