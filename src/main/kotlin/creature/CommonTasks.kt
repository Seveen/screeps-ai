package creature

import screeps.api.*

fun Creep.withdrawFromContainersInRoom(room: Room) {
    val containers = room.find(FIND_STRUCTURES)
            .filter { it.structureType == STRUCTURE_CONTAINER }
            .map { it.unsafeCast<StoreOwner>() }
            .filter {  it.store[RESOURCE_ENERGY] > 45 }
    if (containers.isNotEmpty()) {
        if (withdraw(containers[0], RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
            moveTo(containers[0].pos)
        }
    }
}

fun Creep.withdrawFromSpawnInRoom(room: Room) {
    val spawns = room.find(FIND_STRUCTURES)
            .filter { it.structureType == STRUCTURE_EXTENSION || it.structureType == STRUCTURE_SPAWN }
            .map { it.unsafeCast<StoreOwner>() }
            .filter { it.store[RESOURCE_ENERGY] > 45}
    if (spawns.isNotEmpty()) {
        if (withdraw(spawns[0], RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
            moveTo(spawns[0].pos)
        }
    }
}

fun Creep.pickEnergyOnTheGround(room: Room) {
    room.find(FIND_DROPPED_RESOURCES)
        .filter { it.resourceType == RESOURCE_ENERGY }
        .maxBy { it.amount }?.let {
            if (pickup(it) == ERR_NOT_IN_RANGE) {
                moveTo(it.pos)
            }
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