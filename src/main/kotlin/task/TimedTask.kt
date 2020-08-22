package task

import memory.timer
import screeps.api.Creep

fun Creep.timedTask(period: Int, task: () -> Unit) {
    memory.timer++
    if (memory.timer >= period) {
        memory.timer = 0
        task()
    }
}