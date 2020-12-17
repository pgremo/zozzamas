package zozzamas.lanterna

import com.googlecode.lanterna.graphics.PropertyTheme
import com.googlecode.lanterna.gui2.AbstractTextGUI

import java.util.Properties

object PropertyTheme {
  def apply(name: String): PropertyTheme = {
    val stream = classOf[AbstractTextGUI].getClassLoader.getResourceAsStream(name).nn

    try {
      val properties = Properties()
      properties.load(stream)
      new PropertyTheme(properties)
    } finally {
      stream.close()
    }
  }
}
