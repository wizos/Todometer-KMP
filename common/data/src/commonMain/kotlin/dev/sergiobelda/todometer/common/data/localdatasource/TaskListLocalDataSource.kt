/*
 * Copyright 2021 Sergio Belda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.sergiobelda.todometer.common.data.localdatasource

import dev.sergiobelda.todometer.common.database.dao.ITaskListDao
import dev.sergiobelda.todometer.common.database.mapper.asTaskList
import dev.sergiobelda.todometer.common.database.mapper.asTaskListEntity
import dev.sergiobelda.todometer.common.database.mapper.asTaskLists
import dev.sergiobelda.todometer.common.domain.Result
import dev.sergiobelda.todometer.common.domain.model.TaskList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// TODO Catch errors and return Result.Error
class TaskListLocalDataSource(
    private val taskListDao: ITaskListDao,
) : ITaskListLocalDataSource {

    override fun getTaskLists(): Flow<Result<List<TaskList>>> =
        taskListDao.getTaskLists().map { list ->
            Result.Success(list.asTaskLists())
        }

    override fun getTaskList(id: String): Flow<Result<TaskList>> =
        taskListDao.getTaskList(id).map { taskListEntity ->
            taskListEntity?.let { Result.Success(it.asTaskList()) } ?: Result.Error()
        }

    override suspend fun insertTaskList(taskList: TaskList): Result<String> {
        val taskListId = taskListDao.insertTaskList(taskList.asTaskListEntity())
        return Result.Success(taskListId)
    }

    override suspend fun insertTaskLists(taskLists: List<TaskList>) =
        taskListDao.insertTaskLists(taskLists.map { it.asTaskListEntity() })

    override suspend fun updateTaskList(taskList: TaskList) =
        taskListDao.updateTaskList(taskList.asTaskListEntity())

    override suspend fun updateTaskListName(id: String, name: String) =
        taskListDao.updateTaskListName(id, name)

    override suspend fun deleteTaskList(id: String) = taskListDao.deleteTaskList(id)
}
