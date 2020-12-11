package zozzamas

import com.googlecode.lanterna.graphics.{Theme, ThemeDefinition}
import com.googlecode.lanterna.gui2._
import com.googlecode.lanterna.{Symbols, TerminalPosition, TerminalSize, TerminalTextUtils}

class DynamicTitleBorder(private val f: () => String) extends AbstractBorder {
  def getTitle: String = f()

  override protected def createDefaultRenderer = DynamicTitleBorderRenderer()

  override def toString: String = s"${getClass.getSimpleName} {${getTitle}"

}

private class DynamicTitleBorderRenderer extends Border.BorderRenderer {
  override def getPreferredSize(component: Border | UncheckedNull): TerminalSize = {
    val border = component.asInstanceOf[DynamicTitleBorder]
    val wrappedComponent = border.getComponent
    var preferredSize = (
      if (wrappedComponent == null)
        TerminalSize(2, 2)
      else
        wrappedComponent.getPreferredSize.withRelative(TerminalSize(2, 2))
      ).nn
    val borderTitle = border.getTitle
    if (borderTitle.isEmpty)
      preferredSize
    else
      preferredSize.max(TerminalSize(TerminalTextUtils.getColumnWidth(borderTitle) + 4, 2)).nn
  }

  override def getWrappedComponentTopLeftOffset: TerminalPosition = TerminalPosition.OFFSET_1x1.nn

  override def getWrappedComponentSize(borderSize: TerminalSize | UncheckedNull): TerminalSize = borderSize.withRelative(-Math.min(2, borderSize.getColumns), -Math.min(2, borderSize.getRows)).nn;

  override def drawComponent(graphics: TextGUIGraphics | UncheckedNull, component: Border | UncheckedNull): Unit = {
    val border = component.asInstanceOf[DynamicTitleBorder]
    val wrappedComponent = border.getComponent
    if (wrappedComponent == null) return

    val drawableArea = graphics.getSize.nn
    val theme = component.getTheme.nn
    val themeDefinition = theme.getDefinition(classOf[AbstractBorder]).nn
    graphics.applyThemeStyle(themeDefinition.getNormal)

    if (drawableArea.getRows > 2) {
      val verticalLine = getVerticalLine(theme)
      graphics.drawLine(new TerminalPosition(0, drawableArea.getRows - 2), new TerminalPosition(0, 1), verticalLine)
      graphics.drawLine(new TerminalPosition(drawableArea.getColumns - 1, 1), new TerminalPosition(drawableArea.getColumns - 1, drawableArea.getRows - 2), verticalLine)
    }

    if (drawableArea.getColumns > 2) {
      val horizontalLine = getHorizontalLine(theme)
      graphics.drawLine(new TerminalPosition(1, 0), new TerminalPosition(drawableArea.getColumns - 2, 0), horizontalLine)
      graphics.drawLine(new TerminalPosition(1, drawableArea.getRows - 1), new TerminalPosition(drawableArea.getColumns - 2, drawableArea.getRows - 1), horizontalLine)
    }

    graphics.setCharacter(0, drawableArea.getRows - 1, getBottomLeftCorner(theme))
    graphics.setCharacter(0, 0, getTopLeftCorner(theme))
    graphics.setCharacter(drawableArea.getColumns - 1, 0, getTopRightCorner(theme))
    graphics.setCharacter(drawableArea.getColumns - 1, drawableArea.getRows - 1, getBottomRightCorner(theme))

    if (drawableArea.getColumns >= TerminalTextUtils.getColumnWidth(border.getTitle) + 4) {
      graphics.applyThemeStyle(themeDefinition.getActive)
      graphics.putString(2, 0, border.getTitle)
      graphics.applyThemeStyle(themeDefinition.getNormal)
      graphics.setCharacter(1, 0, getTitleLeft(theme))
      graphics.setCharacter(2 + TerminalTextUtils.getColumnWidth(border.getTitle), 0, getTitleRight(theme))
    }

    wrappedComponent.draw(graphics.newTextGraphics(getWrappedComponentTopLeftOffset, getWrappedComponentSize(drawableArea)))
  }

  private def getTopRightCorner(theme: Theme): Char = theme.getDefinition(classOf[DynamicTitleBorder]).getCharacter("TOP_RIGHT_CORNER", Symbols.SINGLE_LINE_TOP_RIGHT_CORNER)

  private def getBottomRightCorner(theme: Theme): Char = theme.getDefinition(classOf[DynamicTitleBorder]).getCharacter("BOTTOM_RIGHT_CORNER", Symbols.SINGLE_LINE_BOTTOM_RIGHT_CORNER)

  private def getTopLeftCorner(theme: Theme): Char = theme.getDefinition(classOf[DynamicTitleBorder]).getCharacter("TOP_LEFT_CORNER", Symbols.SINGLE_LINE_TOP_LEFT_CORNER)

  private def getBottomLeftCorner(theme: Theme): Char = theme.getDefinition(classOf[DynamicTitleBorder]).getCharacter("BOTTOM_LEFT_CORNER", Symbols.SINGLE_LINE_BOTTOM_LEFT_CORNER)

  private def getVerticalLine(theme: Theme): Char = theme.getDefinition(classOf[DynamicTitleBorder]).getCharacter("VERTICAL_LINE", Symbols.SINGLE_LINE_VERTICAL)

  private def getHorizontalLine(theme: Theme): Char = theme.getDefinition(classOf[DynamicTitleBorder]).getCharacter("HORIZONTAL_LINE", Symbols.SINGLE_LINE_HORIZONTAL)

  private def getTitleLeft(theme: Theme): Char = theme.getDefinition(classOf[DynamicTitleBorder]).getCharacter("TITLE_LEFT", Symbols.SINGLE_LINE_HORIZONTAL)

  private def getTitleRight(theme: Theme): Char = theme.getDefinition(classOf[DynamicTitleBorder]).getCharacter("TITLE_RIGHT", Symbols.SINGLE_LINE_HORIZONTAL)
}
