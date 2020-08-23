package starter

import creature.Role
import creature.roleToEssence
import memory.numberOfCreeps
import memory.role
import memory.roomToNest
import memory.spawnProtocolDone
import nest.PrimalNest
import procedure.locateUnpairedHauler
import procedure.wipePairing
import screeps.api.*
import screeps.api.structures.StructureSpawn
import screeps.api.structures.StructureTower
import screeps.utils.isEmpty
import screeps.utils.unsafe.delete

fun gameLoop() {
    val mainSpawn: StructureSpawn = Game.spawns.values.firstOrNull() ?: return
    //delete memories of creeps that have passed away
    houseKeeping(Game.creeps)

    // FIXME debug
    val nest = PrimalNest(mainSpawn.room)
    if (Memory.roomToNest.containsKey(mainSpawn.room.name).not()) nest.birth()
    nest.live()

    // just an example of how to use room memory
    mainSpawn.room.memory.numberOfCreeps = mainSpawn.room.find(FIND_CREEPS).count()
}

private fun houseKeeping(creeps: Record<String, Creep>) {
    if (Game.creeps.isEmpty()) return  // this is needed because Memory.creeps is undefined

    for ((creepName, _) in Memory.creeps) {
        if (creeps[creepName] == null) {
            console.log("deleting obsolete memory entry for creep $creepName")
            wipePairing(creepName)
            delete(Memory.creeps[creepName])
        }
    }
}
