package procedure

import creature.Role
import memory.assignedMiner
import memory.role
import screeps.api.*

fun minerAttribution(room: Room): String {
    val creeps = room.find(FIND_MY_CREEPS)

    return creeps.filter { it.memory.role == Role.MINER }
            .map { miner ->
                miner.name to creeps.count { it.memory.assignedMiner == miner.name }
            }.toList().minBy { it.second }?.first ?: ""
}

fun wipePairing(deadName: String) {
    Game.creeps.values.forEach {
        if (it.memory.assignedMiner == deadName) it.memory.assignedMiner = ""
    }
}

fun locateUnpairedHauler(room: Room): Creep? {
    return room.find(FIND_MY_CREEPS)
            .firstOrNull {
                it.memory.role == Role.HAULER
                        && it.memory.assignedMiner == ""
            }
}