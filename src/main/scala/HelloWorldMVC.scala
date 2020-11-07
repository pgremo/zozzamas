import java.beans.{PropertyChangeListener, PropertyChangeSupport}

import com.googlecode.lanterna.gui2.Button.Listener
import com.googlecode.lanterna.{TerminalSize, TextColor}
import com.googlecode.lanterna.gui2._
import com.googlecode.lanterna.screen.{Screen, TerminalScreen}
import com.googlecode.lanterna.terminal.{DefaultTerminalFactory, Terminal}

import scala.collection.mutable
import scala.util.Random

object HelloWorldMVC {

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
    }

    private var _surname = ""

    def surname = _surname

    def surname_=(value: String): Unit = {
      notifier.firePropertyChange("surname", _surname, value);
      _surname = value
    }
  }

  class View(private val model: Model) {
    val panel = new Panel()
    panel.setLayoutManager(new GridLayout(2))

    panel.addComponent(new Label("Forename"))
    val forename = new TextBox
    model.addPropertyChangeListener("forename", (event) => {
      forename.setText(event.getNewValue.asInstanceOf[String])
    })
    panel.addComponent(forename)

    panel.addComponent(new Label("Surname"))
    val surname = new TextBox
    model.addPropertyChangeListener("surname", (event) => {
      surname.setText(event.getNewValue.asInstanceOf[String])
    })
    panel.addComponent(surname)

    panel.addComponent(new EmptySpace(new TerminalSize(0, 0))) // Empty space underneath labels

    val submit = new Button("Submit")
    panel.addComponent(submit)
  }

  class Controller(private val model: Model, private val view: View) {
    view.submit.addListener(((button: Button | UncheckedNull) => {
      model.forname = view.forename.getText match {
        case x: String => x
        case _ => throw NullPointerException()
      }
      model.surname = view.surname.getText match {
        case x: String => x
        case _ => throw NullPointerException()
      }
    }).asInstanceOf[Button.Listener])
  }

  def helloWorld: Unit = {
    val terminal = new DefaultTerminalFactory().createTerminal
    val screen = new TerminalScreen(terminal)
    screen.startScreen()

    val model = new Model
    val view = new View(model)

    val controller = new Controller(model, view)

    // Create window to hold the panel
    val window = new BasicWindow
    window.setComponent(view.panel)

    // Create gui and start gui
    val gui = new MultiWindowTextGUI(screen, new DefaultWindowManager, new EmptySpace(TextColor.ANSI.BLUE))
    gui.addWindowAndWait(window)
  }
}