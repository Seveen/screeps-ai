package creature

import screeps.api.*

interface Essence {
    fun act(creep: Creep, room: Room)
    fun createBody(energy: Int): Array<BodyPartConstant>
    fun createMemory(room: Room): CreepMemory
    fun executeSpawnProtocol(creep: Creep)
}