package zozzamas

import java.util.concurrent.Executor

given Conversion[() => Any, Runnable] = f => new Runnable {
  override def run(): Unit = f()
}

given Conversion[Runnable => Unit, Executor] = f => new Executor {
  override def execute(command: Runnable | UncheckedNull): Unit = f(command.nn)
}

