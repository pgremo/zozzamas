package zozzamas

import scala.compiletime._

object Registry {
  def update[C](entity: Entity, value: C)(using storage: Storage[C]): Unit = storage(entity) = value

  def appply[C](entity: Entity)(using storage: Storage[C]): C = storage(entity)

  inline def view[T <: Tuple]: Tuple.Map[T, Storage] = {
    inline erasedValue[T] match {
      case _: EmptyTuple => EmptyTuple
      case _: (head *: tail) => summonInline[Storage[head]] *: view[tail]
    }
  }
}
