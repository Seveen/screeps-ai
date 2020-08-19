package starter

import screeps.api.*
import screeps.api.structures.StructureController


enum class Role {
    UNASSIGNED,
    HARVESTER,
    BUILDER,
    UPGRADER
}

fun Creep.upgrade(controller: StructureController) {
    if (memory.upgrading && store[RESOURCE_ENERGY] == 0) {
        memory.upgrading = false
        say("🔄 harvest")
    }
    if (!memory.upgrading && store[RESOURCE_ENERGY] == store.getCapacity()) {
        memory.upgrading = true
        say("🚧 upgrade")
    }

    if (memory.upgrading) {
        if (upgradeController(controller) == ERR_NOT_IN_RANGE) {
            moveTo(controller.pos)
        }
    } else {
        val onTheGround = room.find(FIND_DROPPED_RESOURCES)
        if (onTheGround.isNotEmpty()) {
            if (pickup(onTheGround[0]) == ERR_NOT_IN_RANGE) {
                moveTo(onTheGround[0].pos)
            }
        }
        val sources = room.find(FIND_SOURCES)
        if (harvest(sources[0]) == ERR_NOT_IN_RANGE) {
            moveTo(sources[0].pos)
        }
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

fun Creep.build(assignedRoom: Room = this.room) {
    if (memory.building && store[RESOURCE_ENERGY] == 0) {
        memory.building = false
        say("🔄 harvest")
    }
    if (!memory.building && store[RESOURCE_ENERGY] == store.getCapacity()) {
        memory.building = true
        say("🚧 build")
    }

    if (memory.building) {
        val targets = assignedRoom.find(FIND_MY_CONSTRUCTION_SITES)
        if (targets.isNotEmpty()) {
            if (build(targets[0]) == ERR_NOT_IN_RANGE) {
                moveTo(targets[0].pos)
            }
        }
    } else {
        val ruins = assignedRoom.find(FIND_RUINS)
                .filter { it.store[RESOURCE_ENERGY] > 0}
        if (ruins.isNotEmpty()) {
            if (withdraw(ruins[0], RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                moveTo(ruins[0].pos)
            }
        } else {
            val sources = assignedRoom.find(FIND_SOURCES)
            if (harvest(sources[0]) == ERR_NOT_IN_RANGE) {
                moveTo(sources[0].pos)
            }
        }
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
            val sources = fromRoom.find(FIND_SOURCES)
            if (harvest(sources[0]) == ERR_NOT_IN_RANGE) {
                moveTo(sources[0].pos)
            }
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
