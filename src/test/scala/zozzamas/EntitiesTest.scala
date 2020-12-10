package zozzamas

import org.junit.Assert._
import org.junit.{Assert, Test}
import zozzamas._
import scala.language.implicitConversions

class EntitiesTest {
  @Test def `reuses released ids`() = {
    val entity = Entity()
    val Entity(i1, v1) = entity
    Assert.assertEquals(0, v1)
    entity.release()
    val Entity(i2, v2) = Entity()
    Assert.assertEquals(i1, i2)
    Assert.assertEquals(1, v2)
  }
}