package creature

import screeps.api.*
import screeps.utils.unsafe.jsObject
import starter.building
import starter.role

object Builder: Essence {
    override fun act(creep: Creep, room: Room) {
        if (creep.memory.building && creep.store[RESOURCE_ENERGY] == 0) {
            creep.memory.building = false
            creep.say("🔄 harvest")
        }
        if (!creep.memory.building && creep.store[RESOURCE_ENERGY] == creep.store.getCapacity()) {
            creep.memory.building = true
            creep.say("🚧 build")
        }

        if (creep.memory.building) {
            room.find(FIND_MY_CONSTRUCTION_SITES)
                .minBy { if (it.structureType == STRUCTURE_EXTENSION) -1 else 1 }?.let {
                    if (creep.build(it) == ERR_NOT_IN_RANGE) {
                        creep.moveTo(it.pos)
                    }
                }
        } else {
            creep.pickEnergyOnTheGround(room)
            creep.withdrawFromSpawnInRoom(room)
        }
    }

    override fun createBody(energy: Int): Array<BodyPartConstant> {
        return when {
            energy >= 550 -> arrayOf(WORK, WORK, WORK, CARRY, CARRY, CARRY, MOVE, MOVE)
            else -> arrayOf(WORK, CARRY, CARRY, MOVE, MOVE)
        }
    }

    override fun createMemory(room: Room) = jsObject<CreepMemory> {
        this.role = Role.BUILDER
    }

}