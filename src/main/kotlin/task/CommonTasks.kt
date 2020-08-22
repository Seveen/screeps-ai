package task

import screeps.api.*

fun Creep.withdrawFromNamedStructureInRoom(id: String) {
    Game.getObjectById<StoreOwner>(id)
            ?.let {
                if (withdraw(it, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    moveTo(it.pos)
                }
            }
}

fun Creep.withdrawFromStructureInRoom(room: Room, structureTypes: List<StructureConstant>) {
    room.find(FIND_STRUCTURES)
            .filter {
                structureTypes.contains(it.structureType)
            }
            .map { it.unsafeCast<StoreOwner>() }
            .filter { it.store[RESOURCE_ENERGY] > 0 }
            .maxBy { it.store[RESOURCE_ENERGY] ?: 0 }?.let {
                if (withdraw(it, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    moveTo(it.pos)
                }
            }

}

fun Creep.withdrawFromRuinsInRoom(room: Room) {
    room.find(FIND_RUINS)
            .maxBy { it.store[RESOURCE_ENERGY] ?: 0 }?.let {
                if (withdraw(it, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    moveTo(it.pos)
                }
            }
}

fun Creep.withdrawFromTombsInRoom(room: Room) {
    room.find(FIND_TOMBSTONES)
            .maxBy { it.store[RESOURCE_ENERGY] ?: 0 }?.let {
                if (withdraw(it, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    moveTo(it.pos)
                }
            }
}

fun Creep.withdrawFromSpawnInRoom(room: Room) {
    room.find(FIND_STRUCTURES)
            .filter { it.structureType == STRUCTURE_EXTENSION || it.structureType == STRUCTURE_SPAWN }
            .map { it.unsafeCast<StoreOwner>() }
            .firstOrNull { it.store[RESOURCE_ENERGY] > 45}?.let {
                if (withdraw(it, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    moveTo(it.pos)
                }
            }
}

fun Creep.pickEnergyOnTheGround(room: Room) {
    room.find(FIND_DROPPED_RESOURCES)
        .filter { it.resourceType == RESOURCE_ENERGY }
        .minBy { pos.getRangeTo(it.pos) }?.let {
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
    room.find(FIND_MY_STRUCTURES)
            .filter {
                (it.structureType == STRUCTURE_EXTENSION
                        || it.structureType == STRUCTURE_TOWER
                        || it.structureType == STRUCTURE_SPAWN
                        || it.structureType == STRUCTURE_STORAGE)
            }
            .sortedBy {
                when (it.structureType) {
                    STRUCTURE_EXTENSION -> -1
                    STRUCTURE_SPAWN -> -1
                    STRUCTURE_TOWER -> 5
                    STRUCTURE_STORAGE -> 10
                    else -> 100
                }
            }
            .map { it.unsafeCast<StoreOwner>() }
            .firstOrNull { it.store[RESOURCE_ENERGY] < it.store.getCapacity(RESOURCE_ENERGY) }?.let {
                if (transfer(it, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    moveTo(it.pos)
                }
            }
}