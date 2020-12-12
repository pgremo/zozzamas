package zozzamas

import com.googlecode.lanterna.bundle.LanternaThemes
import com.googlecode.lanterna.graphics.PropertyTheme
import com.googlecode.lanterna.gui2.Button.Listener
import com.googlecode.lanterna.gui2._
import com.googlecode.lanterna.screen.{Screen, TerminalScreen}
import com.googlecode.lanterna.terminal.{DefaultTerminalFactory, Terminal}
import com.googlecode.lanterna.{TerminalSize, TextColor}
import zozzamas.App.{init, update}

import java.beans.{PropertyChangeListener, PropertyChangeSupport}
import java.io.{FileInputStream, IOException, InputStream}
import java.util.Properties
import java.util.concurrent.Flow.{Publisher, Subscriber}
import java.util.concurrent.{Executor, Flow, SubmissionPublisher}
import scala.collection.mutable
import scala.language.implicitConversions

object PropertyTheme {
  def apply(name: String): PropertyTheme = {
    var stream = classOf[AbstractTextGUI].getClassLoader.getResourceAsStream(name).nn

    val properties = Properties()
    properties.load(stream)

    stream.close()

    new PropertyTheme(properties)
  }
}

object App {
  LanternaThemes.registerTheme("zozzamas", PropertyTheme("zozzamas-theme.properties"))

  val screen = TerminalScreen(DefaultTerminalFactory().createTerminal())
  screen.startScreen()

  val gui = MultiWindowTextGUI(screen, DefaultWindowManager(), EmptySpace(TextColor.ANSI.BLACK))
  gui.setTheme(LanternaThemes.getRegisteredTheme("zozzamas"))

  given publisher as SubmissionPublisher[Generator[Msg]] = SubmissionPublisher[Generator[Msg]](c => gui.getGUIThread().nn.invokeAndWait(c), Flow.defaultBufferSize())

  case class Model(forename: String, surname: String)

  enum Msg {

    case NameCreated(forename: String, surname: String)

  }

  def init(): (Model, Cmd[Msg]) = (Model("bob", "smith"), Cmd.None)

  def update(msg: Msg, model: Model): (Model, Cmd[Msg]) = {
    msg match {
      case Msg.NameCreated(forename, surname) => (Model(surname, forename), Cmd.None)
      case _ => (model, Cmd.None)
    }
  }

  class View()(using publisher: SubmissionPublisher[Generator[Msg]]) {
    val panel = Panel()
    panel.setLayoutManager(new GridLayout(2))

    val border: DynamicTitleBorder = DynamicTitleBorder("")
    val component = panel.withBorder(border)

    panel.addComponent(Label("Forename"))
    val forename = TextBox()
    panel.addComponent(forename)

    panel.addComponent(Label("Surname"))
    val surname = TextBox()
    panel.addComponent(surname)

    panel.addComponent(EmptySpace(TerminalSize.ZERO))

    val submit = Button("Submit", () => publisher.submit(() => Msg.NameCreated(forename.getText.nn, surname.getText.nn)))
    panel.addComponent(submit)

    def view(model: Model): Unit = {
      border.setTitle(model.toString)
      forename.setText(model.forename)
      surname.setText(model.surname)
    }
  }

  @main def start: Unit = {
    val view = View()

    initialize(init, update, view.view)

    val packageObject: Package = view.getClass().getPackage().nn

    val window = BasicWindow(s"${packageObject.getImplementationTitle()} ${packageObject.getImplementationVersion()}")
    window.setComponent(view.component)
    window.setCloseWindowWithEscape(true)

    gui.addWindowAndWait(window)
  }
}