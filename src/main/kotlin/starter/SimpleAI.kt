package starter

import creature.Role
import creature.roleToEssence
import memory.numberOfCreeps
import memory.role
import screeps.api.*
import screeps.api.structures.StructureSpawn
import screeps.api.structures.StructureTower
import screeps.utils.isEmpty
import screeps.utils.unsafe.delete

fun gameLoop() {
    val mainSpawn: StructureSpawn = Game.spawns.values.firstOrNull() ?: return
    //delete memories of creeps that have passed away
    houseKeeping(Game.creeps)
    // just an example of how to use room memory
    mainSpawn.room.memory.numberOfCreeps = mainSpawn.room.find(FIND_CREEPS).count()

    if (mainSpawn.room.controller?.level >= 4) {
        mainSpawn.room.createConstructionSite(46, 19, STRUCTURE_STORAGE)
    }

    mainSpawn.room.find(FIND_MY_STRUCTURES)
            .filter { it.structureType == STRUCTURE_TOWER }
            .map { it.unsafeCast<StructureTower>() }
            .forEach { tower ->
                mainSpawn.room.find(FIND_STRUCTURES)
                        .filter { it.hits < it.hitsMax }
                        .minBy { it.hits }?.let {
                            tower.repair(it)
                        }
            }

    //make sure we have at least some creeps
    spawnCreeps(Game.creeps.values, mainSpawn)

    for ((_, creep) in Game.creeps) {
        val essence = roleToEssence(creep.memory.role)
        essence.act(creep, mainSpawn.room)
    }
}

private fun spawnCreeps(
        creeps: Array<Creep>,
        spawn: StructureSpawn
) {

    val extensionEnergy = spawn.room.find(FIND_MY_STRUCTURES).filter { it.structureType == STRUCTURE_EXTENSION }.count() * 50

    val maxEnergy = when {
        creeps.count { it.memory.role == Role.HARVESTER } == 0 -> 300
        creeps.count { it.memory.role == Role.HAULER } == 0 -> 300
        else -> 300 + extensionEnergy
    }

    if (spawn.room.energyAvailable < maxEnergy) {
        return
    }

    val role: Role = when {
        creeps.count { it.memory.role == Role.HARVESTER } == 0 -> Role.HARVESTER
        creeps.count { it.memory.role == Role.HAULER } == 0 -> Role.HAULER

        creeps.count { it.memory.role == Role.HARVESTER } < 4 -> Role.HARVESTER
        creeps.count { it.memory.role == Role.HAULER } < 6 -> Role.HAULER

        spawn.room.find(FIND_DROPPED_RESOURCES).isNotEmpty() &&
                creeps.count { it.memory.role == Role.CLEANER } < 2 -> Role.CLEANER

        creeps.count { it.memory.role == Role.UPGRADER } < 4 -> Role.UPGRADER
        creeps.count { it.memory.role == Role.RAIDER } < 4 -> Role.RAIDER

        spawn.room.find(FIND_MY_STRUCTURES).none { it.structureType == STRUCTURE_TOWER }
                && creeps.count { it.memory.role == Role.MAINTAINER } < 3 -> Role.MAINTAINER

        spawn.room.find(FIND_MY_CONSTRUCTION_SITES).isNotEmpty() &&
                creeps.count { it.memory.role == Role.BUILDER } < 2 -> Role.BUILDER

        else -> return
    }

    val essence = roleToEssence(role)

    val body: Array<BodyPartConstant> = essence.createBody(spawn.room.energyAvailable)

    val newName = "${role.name}_${Game.time}"
    val code = spawn.spawnCreep(
            body,
            newName, options {
        memory = essence.createMemory(spawn.room)
    })

    when (code) {
        OK -> console.log("spawning $newName with body $body")
        ERR_BUSY, ERR_NOT_ENOUGH_ENERGY -> run { } // do nothing
        else -> console.log("unhandled error code $code")
    }
}

private fun houseKeeping(creeps: Record<String, Creep>) {
    if (Game.creeps.isEmpty()) return  // this is needed because Memory.creeps is undefined

    for ((creepName, _) in Memory.creeps) {
        if (creeps[creepName] == null) {
            console.log("deleting obsolete memory entry for creep $creepName")
            delete(Memory.creeps[creepName])
        }
    }
}
