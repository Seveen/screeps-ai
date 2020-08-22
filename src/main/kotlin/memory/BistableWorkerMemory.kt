package memory

import screeps.api.Creep
import screeps.api.RESOURCE_ENERGY
import screeps.api.get

fun Creep.updateBistableWorkMemory() {
    if (memory.working && store[RESOURCE_ENERGY] == 0) {
        memory.working = false
    }
    if (!memory.working && store[RESOURCE_ENERGY] == store.getCapacity()) {
        memory.working = true
    }
}