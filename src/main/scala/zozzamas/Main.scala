package zozzamas

import com.googlecode.lanterna.gui2.Button.Listener
import com.googlecode.lanterna.gui2._
import com.googlecode.lanterna.screen.{Screen, TerminalScreen}
import com.googlecode.lanterna.terminal.{DefaultTerminalFactory, Terminal}
import com.googlecode.lanterna.{TerminalSize, TextColor}
import zozzamas.App.{init, update}

import java.beans.{PropertyChangeListener, PropertyChangeSupport}
import java.util.concurrent.Flow.{Publisher, Subscriber}
import java.util.concurrent.{Executor, Flow, SubmissionPublisher}
import scala.collection.mutable
import scala.language.implicitConversions
import scala.util.{Failure, Random, Success, Try}

object App {

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

  @main def start: Unit = {
    val component = View()

    initialize(init, update, component.view)

    val packageObject: Package = component.getClass().getPackage().nn

    val window = BasicWindow(s"${packageObject.getImplementationTitle()} ${packageObject.getImplementationVersion()}")
    window.setComponent(component.panel)
    window.setCloseWindowWithEscape(true)

    gui.addWindowAndWait(window)
  }
}