package creature

import screeps.api.*
import memory.*
import screeps.api.structures.StructureController
import screeps.utils.unsafe.jsObject

object EnergyRaider : Essence {
    override fun act(creep: Creep, room: Room) {
        creep.updateBistableWorkMemory()

        if (creep.memory.working) {
            Game.structures[creep.memory.assignedController]
                    ?.unsafeCast<StructureController>()
                    ?.let {
                        if (creep.upgradeController(it) == ERR_NOT_IN_RANGE) {
                            creep.moveTo(it.pos)
                        }
                    }
        } else {
            val targetRoom = creep.memory.assignedRoom

            if (creep.room.name != targetRoom) {
                creep.moveTo(RoomPosition(42, 8, targetRoom))
            } else {
                val source = creep.room.find(FIND_SOURCES).first()

                if (creep.harvest(source) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(source.pos)
                }
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
        role = Role.RAIDER
        assignedRoom = "W42N44"
        assignedController = room.controller?.id ?: ""
    }
}