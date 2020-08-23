package nest

import creature.Bootstrapper
import creature.Role
import creature.roleToEssence
import memory.role
import memory.roomToNest
import memory.spawnProtocolDone
import screeps.api.*
import screeps.api.structures.StructureSpawn
import screeps.api.structures.StructureTower
import screeps.utils.memory.memory

class PrimalNest(val room: Room): Nest {
    override var RoomMemory.nestType by memory(NestType.PRIMAL)
    override var RoomMemory.spawnId by memory { "" }

    override fun birth() {
        if (Memory.roomToNest.isEmpty()) Memory.roomToNest = mapOf(room.name to NestType.PRIMAL)
        else Memory.roomToNest = (Memory.roomToNest as MutableMap<String, NestType>).also { it[room.name] = NestType.PRIMAL }

        room.memory.spawnId = room.find(FIND_MY_SPAWNS).firstOrNull()?.id ?: ""
    }

    override fun live() {
        when (room.controller?.level) {
            1 -> rushToTwo()
            else -> tempLoop()
        }
    }

    private fun creaturesAct() {
        room.find(FIND_MY_CREEPS).forEach {
            roleToEssence(it.memory.role).act(it, room)
        }
    }

    private fun newbornsFirstBreath() {
        room.find(FIND_MY_CREEPS)
            .filter { it.memory.spawnProtocolDone.not() }
            .forEach {
                val essence = roleToEssence(it.memory.role)
                essence.executeSpawnProtocol(it)
            }
    }

    private fun towersAct() {
        room.find(FIND_MY_STRUCTURES)
            .filter { it.structureType == STRUCTURE_TOWER }
            .map { it.unsafeCast<StructureTower>() }
            .forEach { tower ->
                room.find(FIND_HOSTILE_CREEPS)
                        .firstOrNull()?.let {
                            tower.attack(it)
                        }
                        ?: run {
                            room.find(FIND_STRUCTURES)
                                    .filter { it.hits < it.hitsMax }
                                    .minBy { it.hits }?.let {
                                        tower.repair(it)
                                    }
                        }

            }
    }

    // TODO: Optimise 1 -> 2
    private fun rushToTwo() {
        if (room.energyAvailable >= 250) {
            Game.getObjectById<StructureSpawn>(room.memory.spawnId)?.let {
                spawn(it, Role.BOOTSTRAPPER, 250)
            }
        }

        creaturesAct()
    }

    // TODO: Do 2->3 and 3->4

    // FIXME: Temp function
    private fun tempLoop() {
        newbornsFirstBreath()
        towersAct()
        creaturesAct()
        spawnCreeps()
    }

    private fun spawnCreeps() {
        val creeps = room.find(FIND_MY_CREEPS)
        val extensionEnergy = room.find(FIND_MY_STRUCTURES).filter { it.structureType == STRUCTURE_EXTENSION }.count() * 50

        val maxEnergy = when {
            creeps.count { it.memory.role == Role.MINER } == 0 -> 300
            creeps.count { it.memory.role == Role.HAULER } == 0 -> 300
            else -> 300 + extensionEnergy
        }

        if (room.energyAvailable < maxEnergy) {
            return
        }

        val role: Role = when {
            creeps.count { it.memory.role == Role.MINER } == 0 -> Role.MINER
            creeps.count { it.memory.role == Role.HAULER } == 0 -> Role.HAULER

            creeps.count { it.memory.role == Role.MINER } < 2 -> Role.MINER
            creeps.count { it.memory.role == Role.HAULER } < 4 -> Role.HAULER

            room.find(FIND_DROPPED_RESOURCES).isNotEmpty()
                    && creeps.count { it.memory.role == Role.CLEANER } < 1 -> Role.CLEANER

            creeps.count { it.memory.role == Role.UPGRADER } < 3 -> Role.UPGRADER
            creeps.count { it.memory.role == Role.RAIDER } < 3 -> Role.RAIDER

            room.find(FIND_MY_STRUCTURES).none { it.structureType == STRUCTURE_TOWER }
                    && creeps.count { it.memory.role == Role.MAINTAINER } < 3 -> Role.MAINTAINER

            room.find(FIND_MY_CONSTRUCTION_SITES).isNotEmpty()
                    && creeps.count { it.memory.role == Role.BUILDER } < 2 -> Role.BUILDER

            else -> return
        }

        Game.getObjectById<StructureSpawn>(room.memory.spawnId)?.let {
            spawn(it, role, maxEnergy)
        }
    }
}