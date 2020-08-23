package nest

import screeps.api.RoomMemory

interface Nest {
    var RoomMemory.nestType: Enum<NestType>
    var RoomMemory.spawnId: String

    fun birth()
    fun live()
}