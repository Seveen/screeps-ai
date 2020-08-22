package creature

import memory.role
import screeps.api.*
import screeps.utils.unsafe.jsObject

object Homunculus: Essence {
    override fun act(creep: Creep, room: Room) {}

    override fun createBody(energy: Int): Array<BodyPartConstant> {
        return arrayOf(MOVE)
    }

    override fun createMemory(room: Room) = jsObject<CreepMemory> {
        this.role = Role.HARVESTER
    }
}