import java.beans.{PropertyChangeListener, PropertyChangeSupport}

import com.googlecode.lanterna.{TerminalSize, TextColor}
import com.googlecode.lanterna.gui2._
import com.googlecode.lanterna.screen.{Screen, TerminalScreen}
import com.googlecode.lanterna.terminal.{DefaultTerminalFactory, Terminal}

import scala.collection.mutable
import scala.util.Random

trait Actor {
  def name: String

  def when: Double

  def cost: Double
}

case class Creature(val name: String, val when: Double, val cost: Double) extends Actor

def activityLoop: Unit = {
  var random = Random(3)
  val queue = mutable.PriorityQueue[Actor]()(Ordering.by(-_.when))
  queue.enqueue(Creature("goblin", random.nextDouble(), 3.0))
  queue.enqueue(Creature("ogre", random.nextDouble(), 20.0))
  queue.enqueue(Creature("orc", random.nextDouble(), 10.0))

  for (i <- 1 to 10) {
    val actor = queue.dequeue()
    println(actor)
    queue.enqueue(Creature(actor.name, actor.when + actor.cost, actor.cost))
  }
}
