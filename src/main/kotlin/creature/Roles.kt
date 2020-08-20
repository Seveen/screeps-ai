package creature

import starter.pause
import starter.role
import screeps.api.*

enum class Role {
    UNASSIGNED,
    HARVESTER,
    BUILDER,
    UPGRADER,
    HAULER
}

fun roleToEssence(role: Enum<Role>): Essence {
    return when (role) {
        Role.HAULER -> Hauler
        Role.HARVESTER -> Miner
        Role.BUILDER -> Builder
        Role.UPGRADER -> Upgrader
        else -> Homunculus
    }
}

fun Creep.pause() {
    if (memory.pause < 10) {
        //blink slowly
        if (memory.pause % 3 != 0) say("\uD83D\uDEAC")
        memory.pause++
    } else {
        memory.pause = 0
        memory.role = Role.HARVESTER
    }
}


fun Creep.harvest(fromRoom: Room = this.room, toRoom: Room = this.room) {
    if (store[RESOURCE_ENERGY] < store.getCapacity(RESOURCE_ENERGY)) {
        val ruins = fromRoom.find(FIND_RUINS)
                .filter { it.store[RESOURCE_ENERGY] > 0}
        if (ruins.isNotEmpty()) {
            if (withdraw(ruins[0], RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                moveTo(ruins[0].pos)
            }
        } else {
            harvestSourcesInRoom(fromRoom)
        }
    } else {
        val targets = toRoom.find(FIND_MY_STRUCTURES)
                .filter { (it.structureType == STRUCTURE_EXTENSION || it.structureType == STRUCTURE_SPAWN) }
                .map { it.unsafeCast<StoreOwner>() }
                .filter { it.store[RESOURCE_ENERGY] < it.store.getCapacity(RESOURCE_ENERGY) }

        if (targets.isNotEmpty()) {
            if (transfer(targets[0], RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                moveTo(targets[0].pos)
            }
        }
    }
}
