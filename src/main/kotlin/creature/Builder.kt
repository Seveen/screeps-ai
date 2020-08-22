package creature

import screeps.api.*
import screeps.utils.unsafe.jsObject
import memory.role
import memory.updateBistableWorkMemory
import memory.working
import task.withdrawFromStructureInRoom

object Builder: Essence {
    override fun act(creep: Creep, room: Room) {
        creep.updateBistableWorkMemory()

        if (creep.memory.working) {
            room.find(FIND_MY_CONSTRUCTION_SITES)
                .maxBy {
                    when (it.structureType) {
                        STRUCTURE_EXTENSION -> 2
                        STRUCTURE_TOWER -> 1
                        else -> -1
                    }
                }?.let {
                    if (creep.build(it) == ERR_NOT_IN_RANGE) {
                        creep.moveTo(it.pos)
                    }
                }
        } else {
            creep.withdrawFromStructureInRoom(room,
                    listOf(STRUCTURE_TOWER,
                            STRUCTURE_STORAGE))
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
        this.role = Role.BUILDER
    }

    override fun executeSpawnProtocol(creep: Creep) = Unit

}