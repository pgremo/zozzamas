package zozzamas

import com.googlecode.lanterna.gui2.{AbstractInteractableComponent, InteractableRenderer, TextGUIGraphics}
import com.googlecode.lanterna.{TerminalPosition, TerminalSize, TextCharacter}

class Viewport extends AbstractInteractableComponent[Viewport] {
  override def createDefaultRenderer: InteractableRenderer[Viewport] = new InteractableRenderer[Viewport] {
    override def getCursorLocation(component: Viewport | UncheckedNull): TerminalPosition | Null = null

    override def getPreferredSize(component: Viewport | UncheckedNull): TerminalSize = TerminalSize(3, 3)

    override def drawComponent(graphics: TextGUIGraphics | UncheckedNull, component: Viewport | UncheckedNull): Unit = {
      graphics.getSize() match {
        case _: TerminalSize => graphics.fillRectangle(TerminalPosition.TOP_LEFT_CORNER, graphics.getSize(), TextCharacter('X'))
        case _ => throw NullPointerException()
      }
    }
  }
}