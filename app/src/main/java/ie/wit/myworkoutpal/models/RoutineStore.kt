package ie.wit.myworkoutpal.models

interface RoutineStore {
    fun findAll(): List<RoutineModel>
    fun create(routine: RoutineModel)
    fun update(routine: RoutineModel)
    fun delete(routine: RoutineModel)
}