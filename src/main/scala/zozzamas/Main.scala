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

object App {

  private def loadPropTheme(resourceFileName: String) = {
    val properties = new Properties
    try {
      val classLoader = classOf[AbstractTextGUI].getClassLoader.nn
      var resourceAsStream = classLoader.getResourceAsStream(resourceFileName)
      if (resourceAsStream == null) resourceAsStream = new FileInputStream("src/main/resources/" + resourceFileName)
      properties.load(resourceAsStream)
      resourceAsStream.close()
      properties
    } catch {
      case e: IOException =>
        null
    }
  }

  LanternaThemes.registerTheme("zozzamas", PropertyTheme(loadPropTheme("zozzamas-theme.properties")))

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
    private var model = Model("", "")
    
    val panel = Panel()
    panel.setLayoutManager(new GridLayout(2))
    
    val component = panel.withBorder(DynamicTitleBorder(() => model.toString))

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
      this.model = model
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