package starter

import creature.Role
import creature.roleToEssence
import screeps.api.*
import screeps.api.structures.StructureSpawn
import screeps.utils.isEmpty
import screeps.utils.unsafe.delete

fun gameLoop() {
    val mainSpawn: StructureSpawn = Game.spawns.values.firstOrNull() ?: return
    //delete memories of creeps that have passed away
    houseKeeping(Game.creeps)
    // just an example of how to use room memory
    mainSpawn.room.memory.numberOfCreeps = mainSpawn.room.find(FIND_CREEPS).count()

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

    //TODO adapter l'energie dispo en fonction du niveau ?
    val extensionCount = spawn.room.find(FIND_MY_STRUCTURES).filter { it.structureType == STRUCTURE_EXTENSION }.count() * 50
    val maxEnergy = 300 + extensionCount
    if (spawn.room.energyAvailable < maxEnergy) {
        return
    }

    val role: Role = when {
        creeps.count { it.memory.role == Role.HARVESTER } < 4 -> Role.HARVESTER
        creeps.count { it.memory.role == Role.HAULER } < 4 -> Role.HAULER

        creeps.count { it.memory.role == Role.UPGRADER } < 2 -> Role.UPGRADER

        spawn.room.find(FIND_MY_CONSTRUCTION_SITES).isNotEmpty() &&
                creeps.count { it.memory.role == Role.BUILDER } < 3 -> Role.BUILDER

        else -> return
    }

    val essence = roleToEssence(role)

    val body: Array<BodyPartConstant> = essence.createBody(300)

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
