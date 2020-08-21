package creature

import starter.role
import screeps.api.*
import screeps.utils.unsafe.jsObject
import starter.hauling

object Hauler : Essence {

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
            creep.withdrawFromRuinsInRoom(room)
            creep.withdrawFromTombsInRoom(room)
            creep.withdrawFromStructureInRoom(room, listOf(STRUCTURE_CONTAINER))
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
        this.role = Role.HAULER
    }
}