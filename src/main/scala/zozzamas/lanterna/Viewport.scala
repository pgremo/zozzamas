package zozzamas.lanterna

import com.googlecode.lanterna.gui2.{AbstractInteractableComponent, InteractableRenderer, TextGUIGraphics}
import com.googlecode.lanterna.{TerminalPosition, TerminalSize, TextCharacter}

class Viewport extends AbstractInteractableComponent[Viewport] {
  override def createDefaultRenderer: InteractableRenderer[Viewport] = new InteractableRenderer[Viewport] {
    override def getCursorLocation(component: Viewport): TerminalPosition | Null = null

    override def getPreferredSize(component: Viewport): TerminalSize = TerminalSize(3, 3)

    override def drawComponent(graphics: TextGUIGraphics, component: Viewport): Unit =
      graphics.fillRectangle(TerminalPosition.TOP_LEFT_CORNER, graphics.getSize(), 'X')
  }
}