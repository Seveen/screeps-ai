package creature

import screeps.api.*
import screeps.utils.unsafe.jsObject
import memory.*
import task.withdrawFromStructureInRoom

object Upgrader : Essence {
    override fun act(creep: Creep, room: Room) {
        room.controller?.let { controller ->
            creep.updateBistableWorkMemory()

            if (creep.memory.working) {
                if (creep.upgradeController(controller) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(controller.pos)
                }
            } else {
                creep.withdrawFromStructureInRoom(room,
                        listOf(STRUCTURE_TOWER,
                                STRUCTURE_STORAGE))
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