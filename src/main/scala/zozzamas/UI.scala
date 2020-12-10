package zozzamas

import java.util.concurrent.Flow.Subscriber
import java.util.concurrent.{Flow, SubmissionPublisher}

type Generator[M] = () => M

type Cmd[M] = Seq[Generator[M]]

object Cmd {
  val None: Cmd[Nothing] = Seq.empty[Generator[Nothing]]
}

def initialize[M, D](
                      init: () => (D, Cmd[M]),
                      update: (M, D) => (D, Cmd[M]),
                      view: (D) => Unit
                    )(using emitter: SubmissionPublisher[Generator[M]]): Unit = {
  var (model, command) = init()

  emitter.subscribe(new Subscriber[Generator[M]] {
    var sub: Flow.Subscription | Null = null

    def onSubscribe(subscription: Flow.Subscription | UncheckedNull): Unit = {
      sub = subscription
      sub.nn.request(1)
    }

    def onNext(item: Generator[M] | UncheckedNull): Unit = {
      sub.nn.request(1)
      val (a, b) = update(item(), model)
      model = a
      command = b
      view(model)
      command.foreach(emitter.submit)
    }

    def onError(throwable: Throwable | UncheckedNull): Unit = {}

    def onComplete(): Unit = {}
  })

  view(model)
  command.foreach(emitter.submit)
}

