package zozzamas


def register[C](entity: Entity, value: C)(using storage: Storage[C]): Unit = storage(entity) = value

def get[C](entity: Entity)(using storage: Storage[C]): C = storage(entity)

