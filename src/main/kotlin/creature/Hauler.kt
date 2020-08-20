package creature

import starter.role
import screeps.api.*
import screeps.utils.unsafe.jsObject

object Hauler : Essence {

    override fun act(creep: Creep, room: Room) {
        if (creep.store[RESOURCE_ENERGY] < creep.store.getCapacity(RESOURCE_ENERGY)) {
            val ruins = room.find(FIND_RUINS)
                    .filter { it.store[RESOURCE_ENERGY] > 0 }
            if (ruins.isNotEmpty()) {
                if (creep.withdraw(ruins[0], RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(ruins[0].pos)
                }
            } else {
                creep.pickEnergyOnTheGround(room)
                creep.withdrawFromContainersInRoom(room)
            }
        } else {
            creep.fillStorageInRoom(room)
        }
    }

    override fun createBody(energy: Int): Array<BodyPartConstant>{
        return when {
            energy >= 550 -> arrayOf(CARRY, CARRY, CARRY, CARRY, CARRY, MOVE, MOVE, MOVE, MOVE, MOVE)
            else -> arrayOf(CARRY, CARRY, CARRY, MOVE, MOVE, MOVE)
        }
    }

    override fun createMemory(room: Room)= jsObject<CreepMemory> {
        this.role = Role.HAULER
    }
}