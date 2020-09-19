import java.beans.{PropertyChangeListener, PropertyChangeSupport}

import HelloWorldReactive.{init, update}
import com.googlecode.lanterna.gui2.Button.Listener
import com.googlecode.lanterna.gui2._
import com.googlecode.lanterna.screen.{Screen, TerminalScreen}
import com.googlecode.lanterna.terminal.{DefaultTerminalFactory, Terminal}
import com.googlecode.lanterna.{TerminalSize, TextColor}
import scala.language.implicitConversions

import scala.collection.mutable
import scala.util.Random

object HelloWorldReactive {

  given Conversion[() => Unit, Runnable] = (f) => new Runnable {
    override def run(): Unit = f()
  }

  def[T] (x: T | Null) nn: T =
    if (x == null) throw new NullPointerException("tried to cast away nullability, but value is null")
    else x.asInstanceOf[T]

  trait EventEmitter[T] {
    def on(f: T => Unit): Unit

    def emit(e: T): Unit
  }

  enum Msg {

    case None

    case Loading

    case NameCreated(forename: String, surname: String)

  }

  enum Cmd[Msg](val f: () => Msg) {

    case None extends Cmd(() => Msg.None)

    case GenerateName(forename: String, surname: String) extends Cmd(() => Msg.NameCreated(forename, surname))

    def apply(): Msg = f()
  }

  class TextGuiThreadEventEmitter[T](using processor: TextGUIThread) extends EventEmitter[T] {
    private var listeners: List[T => Unit] = Nil

    override def on(f: T => Unit): Unit = listeners ::= f

    override def emit(e: T): Unit = {
      for (l <- listeners) processor.invokeLater(() => l(e))
    }
  }

  val terminal = DefaultTerminalFactory().createTerminal

  val screen = TerminalScreen(terminal)
  screen.startScreen()

  val gui = MultiWindowTextGUI(screen, new DefaultWindowManager, new EmptySpace(TextColor.ANSI.BLUE))

  given processor as TextGUIThread = gui.getGUIThread().nn

  given emitter as EventEmitter[Msg] = TextGuiThreadEventEmitter[Msg]()

  def initialize(
                  init: () => (Model, Cmd[Msg]),
                  update: (Msg, Model) => (Model, Cmd[Msg]),
                  view: (Model) => Unit
                )(using emitter: EventEmitter[Msg]): Unit = {
    var (model, command) = init()

    emitter.on(msg => {
      msg match {
        case Msg.None => {
        }
        case x => {
          val (a, b) = update(x, model)
          model = a
          command = b
          view(model)
          emitter.emit(command())
        }
      }
    })

    view(model)
    emitter.emit(command())
  }


  case class Model(forename: String, surname: String)

  def update(msg: Msg, model: Model): (Model, Cmd[Msg]) = {
    msg match
      case Msg.NameCreated(forename, surname) => (Model(surname, forename), Cmd.None)
      case _ => (model, Cmd.None)
  }

  class View()(using emitter: EventEmitter[Msg]) {
    val panel = Panel()
    panel.setLayoutManager(new GridLayout(2))

    panel.addComponent(Label("Forename"))
    val forename = TextBox()
    panel.addComponent(forename)

    panel.addComponent(Label("Surname"))
    val surname = TextBox()
    panel.addComponent(surname)

    panel.addComponent(new EmptySpace(new TerminalSize(0, 0)))

    val submit = Button("Submit", () => {
      val a = forename.getText.nn
      val b = surname.getText.nn
      emitter.emit(Msg.NameCreated(a, b))
    })
    panel.addComponent(submit)

    def view(model: Model): Unit = {
      forename.setText(model.forename)
      surname.setText(model.surname)
    }
  }

  def generateName: Cmd[Msg] = Cmd.GenerateName("bob", "smith")

  def init(): (Model, Cmd[Msg]) = {
    (Model("", ""), generateName)
  }

  @main def helloWorld: Unit = {
    val component = View()

    initialize(init, update, component.view)

    val window = BasicWindow()
    window.setComponent(component.panel)
    window.setCloseWindowWithEscape(true)

    gui.addWindowAndWait(window)
  }
}