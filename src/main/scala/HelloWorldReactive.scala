import java.beans.{PropertyChangeListener, PropertyChangeSupport}
import java.util.concurrent.Flow.{Publisher, Subscriber}
import java.util.concurrent.{Executor, Flow, SubmissionPublisher}

import zozzamas.{given Conversion[() => Unit, Runnable], given Conversion[Runnable => Unit, Executor]}
import HelloWorldReactive.{init, update}
import com.googlecode.lanterna.gui2.Button.Listener
import com.googlecode.lanterna.gui2._
import com.googlecode.lanterna.screen.{Screen, TerminalScreen}
import com.googlecode.lanterna.terminal.{DefaultTerminalFactory, Terminal}
import com.googlecode.lanterna.{TerminalSize, TextColor}

import scala.language.implicitConversions
import scala.collection.mutable
import scala.util.{Failure, Random, Success, Try}

object HelloWorldReactive {

  val terminal = DefaultTerminalFactory().createTerminal()

  val screen = TerminalScreen(terminal)
  screen.startScreen()

  val gui = MultiWindowTextGUI(screen, DefaultWindowManager(), EmptySpace(TextColor.ANSI.BLUE))

  given publisher as SubmissionPublisher[Generator[Msg]] = SubmissionPublisher[Generator[Msg]](c => gui.getGUIThread().nn.invokeLater(c), Flow.defaultBufferSize())

  type Generator[M] = () => M

  type Cmd[M] = Seq[Generator[M]]

  object Cmd {
    val None: Cmd[Nothing] = Seq.empty[Generator[Nothing]]
  }

  class Monitor[M](private val f: Try[Generator[M]] => Unit) extends Subscriber[Generator[M]] {
    private var sub: Flow.Subscription | Null = null

    override def onSubscribe(subscription: Flow.Subscription | UncheckedNull): Unit = {
      sub = subscription
      sub.nn.request(1)
    }

    override def onNext(item: Generator[M] | UncheckedNull): Unit = {
      sub.nn.request(1)
      f(Success(item.nn))
    }

    override def onError(throwable: Throwable | UncheckedNull): Unit = f(Failure(throwable.nn))

    override def onComplete(): Unit = println("completed")
  }

  def initialize[M, D](
                  init: () => (D, Cmd[M]),
                  update: (M, D) => (D, Cmd[M]),
                  view: (D) => Unit
                )(using emitter: SubmissionPublisher[Generator[M]]): Unit = {
    var (model, command) = init()

    def proc(x: Try[Generator[M]]): Unit = {
      x match {
        case Success(item) => {
          val (a, b) = update(item(), model)
          model = a
          command = b
          view(model)
          command.foreach(emitter.submit)
        }
        case Failure(throwable) => println(throwable)
      }
    }

    emitter.subscribe(Monitor(proc))

    view(model)
    command.foreach(emitter.submit)
  }

  case class Model(forename: String, surname: String)

  enum Msg {
    case NameCreated(forename: String, surname: String)
  }

  def update(msg: Msg, model: Model): (Model, Cmd[Msg]) = {
    msg match {
      case Msg.NameCreated(forename, surname) => (Model(surname, forename), Cmd.None)
      case _ => (model, Cmd.None)
    }
  }

  class View()(using publisher: SubmissionPublisher[Generator[Msg]]) {
    val panel = Panel()
    panel.setLayoutManager(new GridLayout(2))

    panel.addComponent(Label("Forename"))
    val forename = TextBox()
    panel.addComponent(forename)

    panel.addComponent(Label("Surname"))
    val surname = TextBox()
    panel.addComponent(surname)

    panel.addComponent(EmptySpace(new TerminalSize(0, 0)))

    val submit = Button("Submit", () => {
      publisher.submit(() => Msg.NameCreated(forename.getText.nn, surname.getText.nn))
      ()
    })
    panel.addComponent(submit)

    def view(model: Model): Unit = {
      forename.setText(model.forename)
      surname.setText(model.surname)
    }
  }

  def init(): (Model, Cmd[Msg]) = (Model("bob", "smith"), Cmd.None)

  def helloWorld: Unit = {
    val component = View()

    initialize(init, update, component.view)

    val window = BasicWindow()
    window.setComponent(component.panel)
    window.setCloseWindowWithEscape(true)

    gui.addWindowAndWait(window)
  }
}