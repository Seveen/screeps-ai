package creature

import screeps.api.*
import screeps.utils.unsafe.jsObject
import starter.hauling
import starter.role

object Cleaner: Essence {

    override fun act(creep: Creep, room: Room) {
        if (creep.memory.hauling && creep.store[RESOURCE_ENERGY] == 0) {
            creep.memory.hauling = false
        }
        if (!creep.memory.hauling && creep.store[RESOURCE_ENERGY] == creep.store.getCapacity()) {
            creep.memory.hauling = true
        }

        if (creep.memory.hauling) {
            creep.fillStorageInRoom(room)
        } else {
            creep.pickEnergyOnTheGround(room)
        }
    }

    override fun createBody(energy: Int): Array<BodyPartConstant>{
        return when {
            energy >= 650 -> arrayOf(CARRY, CARRY, CARRY, CARRY, CARRY, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE)
            energy >= 550 -> arrayOf(CARRY, CARRY, CARRY, CARRY, CARRY, MOVE, MOVE, MOVE, MOVE, MOVE)
            else -> arrayOf(CARRY, CARRY, CARRY, MOVE, MOVE, MOVE)
        }
    }

    override fun createMemory(room: Room)= jsObject<CreepMemory> {
        this.role = Role.CLEANER
    }
}