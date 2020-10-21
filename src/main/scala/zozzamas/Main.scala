package zozzamas

import java.beans.{PropertyChangeListener, PropertyChangeSupport}

import scala.collection.mutable
import scala.util.Random
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.{BasicWindow, Button, DefaultWindowManager, EmptySpace, GridLayout, Label, MultiWindowTextGUI, Panel, TextBox}
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal


class View:
  val panel = new Panel()
  panel.setLayoutManager(new GridLayout(2))

  panel.addComponent(new Label("Forename"))
  val forename = new TextBox
  panel.addComponent(forename)

  panel.addComponent(new Label("Surname"))
  val surname = new TextBox
  panel.addComponent(surname)

  panel.addComponent(new EmptySpace(new TerminalSize(0, 0))) // Empty space underneath labels

  val submit = new Button("Submit")
  panel.addComponent(submit)
  submit.addListener(SubmitListener())

object Option:
  def apply[T](x: T | UncheckedNull): Option[T] = if (x.isInstanceOf[UncheckedNull]) None else Some(x.asInstanceOf[T])

class SubmitListener(using namings: Storage[Naming])extends Button.Listener :
  override def onTriggered(button: Button | UncheckedNull): Unit =
    namings(user) = Naming(Option(view.forename.getText), Option(view.surname.getText))
    namingsSystem()

given view as View = View()

val user = Entities.Entity()

case class Naming(foreName: Option[String], surName: Option[String])

given Namings as Storage[Naming]

def namingsSystem()(using namings: Storage[Naming])(using view: View): Unit =
  val component = namings(user)
  view.forename.setText(component.surName.orNull)
  view.surname.setText(component.foreName.orNull)

@main def start() = {
  val terminal = DefaultTerminalFactory().createTerminal
  val screen = TerminalScreen(terminal)
  screen.startScreen()

  val packageObject: Package = view.getClass().getPackage().nn

  // Create window to hold the panel
  val window = BasicWindow(s"${packageObject.getImplementationTitle()} ${packageObject.getImplementationVersion()}")
  window.setCloseWindowWithEscape(true)
  window.setComponent(view.panel)

  // Create gui and start gui
  val gui = MultiWindowTextGUI(screen, new DefaultWindowManager, new EmptySpace(TextColor.ANSI.BLUE))
  gui.addWindowAndWait(window)
}
