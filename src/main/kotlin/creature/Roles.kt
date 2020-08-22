package creature

import memory.pause
import memory.role
import screeps.api.*
import task.harvestSourcesInRoom

enum class Role {
    UNASSIGNED,
    MINER,
    BUILDER,
    UPGRADER,
    HAULER,
    MAINTAINER,
    CLEANER,
    RAIDER
}

fun roleToEssence(role: Enum<Role>): Essence {
    return when (role) {
        Role.HAULER -> Hauler
        Role.MINER -> Miner
        Role.BUILDER -> Builder
        Role.UPGRADER -> Upgrader
        Role.MAINTAINER -> Maintainer
        Role.CLEANER -> Cleaner
        Role.RAIDER -> EnergyRaider
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
        memory.role = Role.MINER
    }
}
