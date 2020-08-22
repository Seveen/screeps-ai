package creature

import screeps.api.*
import screeps.utils.unsafe.jsObject
import memory.role
import memory.updateBistableWorkMemory
import memory.working
import task.fillStorageInRoom
import task.pickEnergyOnTheGround

object Cleaner: Essence {

    override fun act(creep: Creep, room: Room) {
        creep.updateBistableWorkMemory()

        if (creep.memory.working) {
            creep.fillStorageInRoom(room)
        } else {
            creep.pickEnergyOnTheGround(room)
            creep.memory.working = true
        }
    }

    override fun createBody(energy: Int) = arrayOf<BodyPartConstant>(CARRY, CARRY, CARRY, MOVE, MOVE, MOVE)

    override fun createMemory(room: Room)= jsObject<CreepMemory> {
        this.role = Role.CLEANER
    }

    override fun executeSpawnProtocol(creep: Creep) = Unit
}