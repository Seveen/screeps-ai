package creature

import memory.*
import procedure.minerAttribution
import screeps.api.*
import screeps.utils.unsafe.jsObject
import task.*

object Hauler : Essence {

    override fun act(creep: Creep, room: Room) {
        creep.timedTask(100) {
            refreshAssignedContainer(creep, room)
        }

        creep.updateBistableWorkMemory()

        if (creep.memory.working) {
            creep.fillStorageInRoom(room)
        } else {
            creep.withdrawFromNamedStructureInRoom(creep.memory.assignedContainer)
        }
    }

    override fun createBody(energy: Int): Array<BodyPartConstant> {
        return when {
            energy >= 650 -> arrayOf(CARRY, CARRY, CARRY, CARRY, CARRY, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE)
            energy >= 550 -> arrayOf(CARRY, CARRY, CARRY, CARRY, CARRY, MOVE, MOVE, MOVE, MOVE, MOVE)
            else -> arrayOf(CARRY, CARRY, CARRY, MOVE, MOVE, MOVE)
        }
    }

    override fun createMemory(room: Room) = jsObject<CreepMemory> {
        this.role = Role.HAULER
        this.assignedMiner = minerAttribution(room)
        this.timer = 0
    }

    override fun executeSpawnProtocol(creep: Creep){
        refreshAssignedContainer(creep, creep.room)
    }

    private fun refreshAssignedContainer(creep: Creep, room: Room) {
        room.find(FIND_STRUCTURES)
                .filter { it.structureType == STRUCTURE_CONTAINER }
                .firstOrNull { container ->
                    room.find(FIND_MY_CREEPS)
                            .firstOrNull { it.name == creep.memory.assignedMiner }
                            ?.pos
                            ?.let {
                                container.pos.x == it.x && container.pos.y == it.y
                            } ?: false
                }?.let {
                    creep.memory.assignedContainer = it.id
                }
    }
}