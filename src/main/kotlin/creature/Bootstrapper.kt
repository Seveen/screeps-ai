package creature

import memory.role
import memory.updateBistableWorkMemory
import memory.working
import screeps.api.*
import screeps.utils.unsafe.jsObject

object Bootstrapper: Essence {
    override fun act(creep: Creep, room: Room) {
        room.controller?.let { controller ->
            creep.updateBistableWorkMemory()

            if (creep.memory.working) {
                if (creep.upgradeController(controller) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(controller.pos)
                } else {}
            } else {
                room.find(FIND_SOURCES)
                        .minBy { creep.pos.getRangeTo(it) }
                        ?.let {
                            if (creep.harvest(it) == ERR_NOT_IN_RANGE) {
                                creep.moveTo(it.pos)
                            }
                        }
            }
        }
    }

    override fun createBody(energy: Int)
            = arrayOf<BodyPartConstant>(WORK, CARRY, MOVE, MOVE)

    override fun createMemory(room: Room) = jsObject<CreepMemory> {
        role = Role.BOOTSTRAPPER
    }

    override fun executeSpawnProtocol(creep: Creep) = Unit
}