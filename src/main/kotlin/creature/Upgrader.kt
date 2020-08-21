package creature

import starter.role
import screeps.api.*
import screeps.utils.unsafe.jsObject
import starter.upgrading

object Upgrader : Essence {
    override fun act(creep: Creep, room: Room) {
        room.controller?.let { controller ->
            if (creep.memory.upgrading && creep.store[RESOURCE_ENERGY] == 0) {
                creep.memory.upgrading = false
            }
            if (!creep.memory.upgrading && creep.store[RESOURCE_ENERGY] == creep.store.getCapacity()) {
                creep.memory.upgrading = true
            }

            if (creep.memory.upgrading) {
                if (creep.upgradeController(controller) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(controller.pos)
                }
            } else {
                creep.withdrawFromStructureInRoom(room,
                        listOf(STRUCTURE_TOWER,
                                STRUCTURE_STORAGE,
                                STRUCTURE_CONTAINER))
            }
        }
    }

    override fun createBody(energy: Int): Array<BodyPartConstant> {
        return when {
            energy >= 650 -> arrayOf(WORK, WORK, WORK, CARRY, CARRY, CARRY, MOVE, MOVE, MOVE, MOVE)
            energy >= 550 -> arrayOf(WORK, WORK, WORK, CARRY, CARRY, CARRY, MOVE, MOVE)
            else -> arrayOf(WORK, CARRY, CARRY, MOVE, MOVE)
        }
    }

    override fun createMemory(room: Room) = jsObject<CreepMemory> {
        this.role = Role.UPGRADER
    }

}