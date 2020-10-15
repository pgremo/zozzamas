package zozzamas

import org.junit.{Assert, Test}

class StorageTest:
  @Test def `gets associated value`() =
    val storage = Storage[String]()
    storage.put(13, "hello")
    Assert.assertEquals("hello", storage.get(13))
