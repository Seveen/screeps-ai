package creature

import memory.updateBistableWorkMemory
import screeps.api.*
import screeps.utils.unsafe.jsObject
import memory.role
import memory.working
import task.withdrawFromStructureInRoom

object Maintainer: Essence {
    override fun act(creep: Creep, room: Room) {
        creep.updateBistableWorkMemory()

        if (creep.memory.working) {
            room.find(FIND_STRUCTURES)
                    .filter { it.hits < it.hitsMax }
                    .minBy { it.hits }?.let {
                        if (creep.repair(it) == ERR_NOT_IN_RANGE) {
                            creep.moveTo(it.pos)
                        }
                    }
        } else {
            creep.withdrawFromStructureInRoom(room,
                    listOf(STRUCTURE_TOWER,
                            STRUCTURE_STORAGE,
                            STRUCTURE_CONTAINER))
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
        this.role = Role.MAINTAINER
    }

}