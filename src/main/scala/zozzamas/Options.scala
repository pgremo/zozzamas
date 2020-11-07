package zozzamas

object Option {
  def apply[T](x: T | UncheckedNull): Option[T] =
    if (x.isInstanceOf[UncheckedNull]) None else Some(x.asInstanceOf[T])
}
