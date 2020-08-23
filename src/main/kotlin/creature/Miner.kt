package creature

import memory.assignedMiner
import screeps.api.*
import screeps.utils.unsafe.jsObject
import memory.assignedSource
import memory.role
import memory.spawnProtocolDone
import procedure.locateUnpairedHauler

object Miner : Essence {
    override fun act(creep: Creep, room: Room) {
        val maybeSource = Game.getObjectById<Source>(creep.memory.assignedSource)
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
        role = Role.MINER
        assignedSource = room.find(FIND_SOURCES).map { source ->
            source.id to room.find(FIND_MY_CREEPS)
                    .count { it.memory.assignedSource == source.id }
        }.toList().minBy { it.second }?.first ?: ""
    }

    override fun executeSpawnProtocol(creep: Creep) {
        console.log("Spawn protocol")
        creep.memory.spawnProtocolDone = true
        locateUnpairedHauler(creep.room)?.let {
            it.memory.assignedMiner = creep.name
        }
    }
}