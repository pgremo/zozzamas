package zozzamas

import com.googlecode.lanterna.graphics.{Theme, ThemeDefinition}
import com.googlecode.lanterna.gui2._
import com.googlecode.lanterna.{Symbols, TerminalPosition, TerminalSize, TerminalTextUtils}

class DynamicTitleBorder(var title: String) extends AbstractBorder {
  override protected def createDefaultRenderer = DynamicTitleBorderRenderer()
}

private class DynamicTitleBorderRenderer extends Border.BorderRenderer {
  val TWO = TerminalSize(2, 2)

  override def getPreferredSize(component: Border | UncheckedNull): TerminalSize | UncheckedNull = {
    val border = component.asInstanceOf[DynamicTitleBorder]
    val wrapped = border.getComponent
    val title = border.title

    val preferredSize = (
      if (wrapped == null) TWO
      else wrapped.getPreferredSize.withRelative(TWO)
      ).nn
    if (title.isEmpty) preferredSize
    else preferredSize.max(TerminalSize(TerminalTextUtils.getColumnWidth(title) + 4, 2))
  }

  override def getWrappedComponentTopLeftOffset: TerminalPosition | UncheckedNull = TerminalPosition.OFFSET_1x1

  override def getWrappedComponentSize(borderSize: TerminalSize | UncheckedNull): TerminalSize | UncheckedNull = borderSize.withRelative(-Math.min(2, borderSize.getColumns), -Math.min(2, borderSize.getRows));

  override def drawComponent(graphics: TextGUIGraphics | UncheckedNull, component: Border | UncheckedNull): Unit = {
    val border = component.asInstanceOf[DynamicTitleBorder]
    val wrapped = border.getComponent
    if (wrapped == null) return

    val area = graphics.getSize.nn
    val theme = component.getTheme.nn.getDefinition(classOf[AbstractBorder]).nn
    graphics.applyThemeStyle(theme.getNormal)

    if (area.getRows > 2) {
      val verticalLine = theme.getVerticalLine
      graphics.drawLine(new TerminalPosition(0, area.getRows - 2), new TerminalPosition(0, 1), verticalLine)
      graphics.drawLine(new TerminalPosition(area.getColumns - 1, 1), new TerminalPosition(area.getColumns - 1, area.getRows - 2), verticalLine)
    }

    if (area.getColumns > 2) {
      val horizontalLine = theme.getHorizontalLine
      graphics.drawLine(new TerminalPosition(1, 0), new TerminalPosition(area.getColumns - 2, 0), horizontalLine)
      graphics.drawLine(new TerminalPosition(1, area.getRows - 1), new TerminalPosition(area.getColumns - 2, area.getRows - 1), horizontalLine)
    }

    graphics.setCharacter(0, area.getRows - 1, theme.getBottomLeftCorner)
    graphics.setCharacter(0, 0, theme.getTopLeftCorner)
    graphics.setCharacter(area.getColumns - 1, 0, theme.getTopRightCorner)
    graphics.setCharacter(area.getColumns - 1, area.getRows - 1, theme.getBottomRightCorner)

    if (area.getColumns >= TerminalTextUtils.getColumnWidth(border.title) + 4) {
      graphics.applyThemeStyle(theme.getActive)
      graphics.putString(2, 0, border.title)
      graphics.applyThemeStyle(theme.getNormal)
      graphics.setCharacter(1, 0, theme.getTitleLeft)
      graphics.setCharacter(2 + TerminalTextUtils.getColumnWidth(border.title), 0, theme.getTitleRight)
    }

    wrapped.draw(graphics.newTextGraphics(getWrappedComponentTopLeftOffset, getWrappedComponentSize(area)))
  }
}

extension (t: ThemeDefinition) {
  private def getTopRightCorner: Char = t.getCharacter("TOP_RIGHT_CORNER", Symbols.SINGLE_LINE_TOP_RIGHT_CORNER)

  private def getBottomRightCorner: Char = t.getCharacter("BOTTOM_RIGHT_CORNER", Symbols.SINGLE_LINE_BOTTOM_RIGHT_CORNER)

  private def getTopLeftCorner: Char = t.getCharacter("TOP_LEFT_CORNER", Symbols.SINGLE_LINE_TOP_LEFT_CORNER)

  private def getBottomLeftCorner: Char = t.getCharacter("BOTTOM_LEFT_CORNER", Symbols.SINGLE_LINE_BOTTOM_LEFT_CORNER)

  private def getVerticalLine: Char = t.getCharacter("VERTICAL_LINE", Symbols.SINGLE_LINE_VERTICAL)

  private def getHorizontalLine: Char = t.getCharacter("HORIZONTAL_LINE", Symbols.SINGLE_LINE_HORIZONTAL)

  private def getTitleLeft: Char = t.getCharacter("TITLE_LEFT", Symbols.SINGLE_LINE_HORIZONTAL)

  private def getTitleRight: Char = t.getCharacter("TITLE_RIGHT", Symbols.SINGLE_LINE_HORIZONTAL)
}
