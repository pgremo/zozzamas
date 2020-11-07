package zozzamas

import java.beans.{PropertyChangeListener, PropertyChangeSupport}
import java.util.concurrent.Executor
import scala.language.implicitConversions

import scala.collection.mutable
import scala.util.Random
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.{BasicWindow, Button, DefaultWindowManager, EmptySpace, GridLayout, Label, MultiWindowTextGUI, Panel, TextBox}
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal

object App {
  val terminal = DefaultTerminalFactory().createTerminal()
  val screen = TerminalScreen(terminal)
  screen.startScreen()
  val gui = MultiWindowTextGUI(screen, DefaultWindowManager(), EmptySpace(TextColor.ANSI.BLUE))

  class View(using namings: mutable.Map[Entity, Naming]) {
    val panel = new Panel()
    panel.setLayoutManager(new GridLayout(2))

    panel.addComponent(new Label("Forename"))
    val forename = new TextBox
    panel.addComponent(forename)

    panel.addComponent(new Label("Surname"))
    val surname = new TextBox
    panel.addComponent(surname)

    panel.addComponent(new EmptySpace(new TerminalSize(0, 0)))

    val submit = new Button("Submit", () => {
      namings(user) = Naming(Option(view.forename.getText), Option(view.surname.getText))
      gui.getGUIThread().nn.invokeAndWait(() => namingsSystem())
    })
    panel.addComponent(submit)
  }

  given view as View = View()

  val user = Entity()

  case class Naming(foreName: Option[String], surName: Option[String])

  given namings as mutable.Map[Entity, Naming] = SparseMap[Naming]()

  def namingsSystem()(using namings: mutable.Map[Entity, Naming])(using view: View): Unit = {
    val component = namings(user)
    view.forename.setText(component.surName.orNull)
    view.surname.setText(component.foreName.orNull)
  }

  @main def start() = {

    val packageObject: Package = view.getClass().getPackage().nn

    val window = BasicWindow(s"${packageObject.getImplementationTitle()} ${packageObject.getImplementationVersion()}")
    window.setCloseWindowWithEscape(true)
    window.setComponent(view.panel)

    gui.addWindowAndWait(window)
  }
}