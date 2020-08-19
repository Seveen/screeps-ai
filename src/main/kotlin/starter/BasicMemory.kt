package starter

import screeps.api.*
import screeps.utils.memory.MemoryMappingDelegate
import screeps.utils.memory.memory
import screeps.utils.memory.memoryWithSerializer
import kotlin.Unit.toString
import kotlin.properties.ReadWriteProperty

/* Add the variables that you want to store to the persistent memory for each object type.
* They can be accessed by using the .memory attribute of any of the instances of that class
* i.e. creep.memory.building = true */

/* Creep.memory */
var CreepMemory.building: Boolean by memory { false }
var CreepMemory.upgrading: Boolean by memory { false }
var CreepMemory.pause: Int by memory { 0 }
var CreepMemory.role by memory(Role.UNASSIGNED)

var CreepMemory.assignedSource by memory { "" }

/* Rest of the persistent memory structures.
* These set an unused test variable to 0. This is done to illustrate the how to add variables to
* the memory. Change or remove it at your convenience.*/

/* Power creep is a late game hero unit that is spawned from a Power Spawn
   see https://docs.screeps.com/power.html for more details.
   This set sets up the memory for the PowerCreep.memory class.
 */
var PowerCreepMemory.test: Int by memory { 0 }

/* flag.memory */
var FlagMemory.test: Int by memory { 0 }

/* room.memory */
var RoomMemory.numberOfCreeps: Int by memory { 0 }
var RoomMemory.minerBySource: Map<String, String>
        by memoryMap(mapOf("" to ""))

/* spawn.memory */
var SpawnMemory.test: Int by memory { 0 }


/* Memory types */
fun memoryMap(defaultValue: Map<String, String>) = memoryWithSerializer({ defaultValue },
        Map<String, String>::toString)
{ s ->
    s.replace("{", "").replace("}", "")
            .split(", ")
            .associate {
                val (left, right) = it.split("=")
                left to right
            }
}
