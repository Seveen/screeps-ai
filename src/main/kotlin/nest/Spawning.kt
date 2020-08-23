package nest

import creature.Role
import creature.roleToEssence
import screeps.api.*
import screeps.api.structures.StructureSpawn


fun spawn(spawn: StructureSpawn, role: Role, energy: Int) {
    val essence = roleToEssence(role)

    val body: Array<BodyPartConstant> = essence.createBody(energy)

    val newName = "${role.name}_${Game.time}"
    val code = spawn.spawnCreep(
            body,
            newName, options {
        memory = essence.createMemory(spawn.room)
    })

    when (code) {
        OK -> console.log("spawning $newName with body $body")
        ERR_BUSY, ERR_NOT_ENOUGH_ENERGY -> run { }
        else -> console.log("unhandled error code $code")
    }
}

