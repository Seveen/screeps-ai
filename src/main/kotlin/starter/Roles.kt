package starter

import screeps.api.*
import screeps.api.structures.StructureController


enum class Role {
    UNASSIGNED,
    HARVESTER,
    BUILDER,
    UPGRADER,
    HAULER
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
        } else {
            withdrawFromContainersInRoom(controller.room)
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
        withdrawFromContainersInRoom(assignedRoom)
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

fun Creep.mine(assignedRoom: Room = this.room) {
    val maybeSource = assignedRoom.find(FIND_SOURCES).firstOrNull { it.id == memory.assignedSource }
    val source = maybeSource ?: assignedRoom.find(FIND_SOURCES).first()

    if (harvest(source) == ERR_NOT_IN_RANGE) {
        moveTo(source.pos)
    }
}

fun Creep.haul(assignedRoom: Room = this.room) {
    if (store[RESOURCE_ENERGY] < store.getCapacity(RESOURCE_ENERGY)) {
        val ruins = assignedRoom.find(FIND_RUINS)
                .filter { it.store[RESOURCE_ENERGY] > 0 }
        if (ruins.isNotEmpty()) {
            if (withdraw(ruins[0], RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                moveTo(ruins[0].pos)
            }
        } else {
            val sources = assignedRoom.find(FIND_DROPPED_RESOURCES)
            if (sources.isNotEmpty()) {
                if (pickup(sources[0]) == ERR_NOT_IN_RANGE) {
                    moveTo(sources[0].pos)
                }
            } else {
                withdrawFromContainersInRoom(assignedRoom)
            }
        }
    } else {
        fillStorageInRoom(assignedRoom)
    }
}

fun Creep.harvestSourcesInRoom(room: Room) {
    val sources = room.find(FIND_SOURCES)
    if (harvest(sources[0]) == ERR_NOT_IN_RANGE) {
        moveTo(sources[0].pos)
    }
}

fun Creep.fillStorageInRoom(room: Room) {
    val targets = room.find(FIND_MY_STRUCTURES)
            .filter { (it.structureType == STRUCTURE_EXTENSION || it.structureType == STRUCTURE_SPAWN) }
            .map { it.unsafeCast<StoreOwner>() }
            .filter { it.store[RESOURCE_ENERGY] < it.store.getCapacity(RESOURCE_ENERGY) }

    if (targets.isNotEmpty()) {
        if (transfer(targets[0], RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
            moveTo(targets[0].pos)
        }
    }
}

fun Creep.withdrawFromContainersInRoom(room: Room) {
    val containers = room.find(FIND_STRUCTURES)
            .filter { it.structureType == STRUCTURE_CONTAINER }
            .map { it.unsafeCast<StoreOwner>() }
            .filter {  it.store[RESOURCE_ENERGY] > 0 }
    if (containers.isNotEmpty()) {
        if (withdraw(containers[0], RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
            moveTo(containers[0].pos)
        }
    }
}
