package zozzamas

import org.junit.{Assert, Test}

class SparseMapTest:
  @Test def `gets associated value`() =
    val storage = SparseMap[String]()
    storage(13) = "hello"
    Assert.assertEquals("hello", storage(13))

  @Test def `removes associated value`() =
    val storage = SparseMap[String]()
    storage(13) = "hello"
    storage.remove(13)
    Assert.assertEquals(None, storage.get(13))

  @Test def `iterates`() =
    val storage = SparseMap[String]()
    storage(13) = "hello"
    storage(10) = "ahoy"
    val iterator = storage.iterator
    Assert.assertEquals((13 -> "hello"), iterator.next())
    Assert.assertEquals((10 -> "ahoy"), iterator.next())
    Assert.assertFalse(iterator.hasNext)

  @Test def `replaces existing value`() =
    val storage = SparseMap[String]()
    storage(13) = "hello"
    Assert.assertEquals("hello", storage(13))
    storage(13) = "ahoy"
    Assert.assertEquals("ahoy", storage(13))
