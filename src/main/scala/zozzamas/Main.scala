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

class Model {
  private lazy val notifier = new PropertyChangeSupport(this)

  def addPropertyChangeListener(property: String, pcl: PropertyChangeListener): Unit = {
    notifier.addPropertyChangeListener(property, pcl)
  }

  def removePropertyChangeListener(pcl: PropertyChangeListener): Unit = {
    notifier.removePropertyChangeListener(pcl)
  }

  private var _forname = ""

  def forname = _forname

  def forname_=(value: String): Unit = {
    notifier.firePropertyChange("forname", _forname, value);
    _forname = value
    println(s"forname is set to $value")
  }

  private var _surname = ""

  def surname = _surname

  def surname_=(value: String): Unit = {
    notifier.firePropertyChange("surname", _surname, value);
    _surname = value
    println(s"surname is set to $value")
  }
}

class View(private val model: Model) {
  val panel = new Panel()
  panel.setLayoutManager(new GridLayout(2))

  panel.addComponent(zozzamas.Viewport(), GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.FILL, true, true, 3, 3))
}

class Controller(private val model: Model, private val view: View) {
}

@main def start() = {

  val terminal = new DefaultTerminalFactory().createTerminal
  val screen = new TerminalScreen(terminal)
  screen.startScreen()

  val model = new Model
  val view = new View(model)

  val controller = new Controller(model, view)

  // Create window to hold the panel
  val packageObject: Package = model.getClass().getPackage().nn

  val window = new BasicWindow(s"${packageObject.getImplementationTitle()} ${packageObject.getImplementationVersion()}")
  window.setCloseWindowWithEscape(true)
  window.setComponent(view.panel)

  // Create gui and start gui
  val gui = new MultiWindowTextGUI(screen, new DefaultWindowManager, new EmptySpace(TextColor.ANSI.BLUE))
  gui.addWindowAndWait(window)
}
