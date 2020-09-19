import com.googlecode.lanterna.gui2.{AbstractInteractableComponent, InteractableRenderer, TextGUIGraphics}
import com.googlecode.lanterna.{TerminalPosition, TerminalSize, TextCharacter}

class Viewport extends AbstractInteractableComponent[Viewport] {
  override def createDefaultRenderer: InteractableRenderer[Viewport] = ViewportRenderer()

  class ViewportRenderer extends InteractableRenderer[Viewport] {
    override def getCursorLocation(component: Viewport | UncheckedNull): TerminalPosition = new TerminalPosition(0, 0)

    override def getPreferredSize(component: Viewport | UncheckedNull): TerminalSize = TerminalSize(3, 3)

    override def drawComponent(graphics: TextGUIGraphics | UncheckedNull, component: Viewport | UncheckedNull): Unit = {
      graphics.getSize() match {
        case area: TerminalSize => {
          if (area.getRows > 0 && area.getColumns > 0) graphics.fillRectangle(TerminalPosition.TOP_LEFT_CORNER, graphics.getSize, new TextCharacter('X'))
        }
        case _ => throw NullPointerException()
      }
    }
  }

}
