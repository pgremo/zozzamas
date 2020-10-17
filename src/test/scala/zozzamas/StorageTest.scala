package zozzamas

import org.junit.{Assert, Test}

class StorageTest:
  @Test def `gets associated value`() =
    val storage = Storage[String]()
    storage.put(13, "hello")
    Assert.assertEquals(Some("hello"), storage.get(13))
  
  @Test def `removes associated value`() =
    val storage = Storage[String]()
    storage.put(13, "hello")
    storage.put(10, "ahoy")
    storage.remove(13)
    Assert.assertEquals(Some("ahoy"), storage.get(10))
