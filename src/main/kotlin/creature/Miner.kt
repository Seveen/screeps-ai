package creature

import screeps.api.*
import screeps.utils.*
import screeps.utils.unsafe.jsObject
import starter.assignedSource
import starter.role

object Miner : Essence {
    override fun act(creep: Creep, room: Room) {
        val maybeSource = room.find(FIND_SOURCES).firstOrNull { it.id == creep.memory.assignedSource }
        val source = maybeSource ?: room.find(FIND_SOURCES).first()

        if (creep.harvest(source) == ERR_NOT_IN_RANGE) {
            creep.moveTo(source.pos)
        }
    }

    override fun createBody(energy: Int): Array<BodyPartConstant> {
        return when {
            energy >= 550 -> arrayOf(WORK, WORK, WORK, WORK, WORK, MOVE)
            else -> arrayOf(WORK, WORK, MOVE, MOVE)
        }
    }

    override fun createMemory(room: Room) = jsObject<CreepMemory> {
        this.role = Role.HARVESTER
        this.assignedSource = room.find(FIND_SOURCES).map { source ->
            source.id to room.find(FIND_MY_CREEPS)
                    .count { it.memory.assignedSource == source.id }
        }.toList().minBy { it.second }?.first ?: ""
    }
}