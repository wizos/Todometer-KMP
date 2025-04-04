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

package dev.sergiobelda.todometer.wearapp.wearos.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sergiobelda.todometer.common.domain.doIfError
import dev.sergiobelda.todometer.common.domain.doIfSuccess
import dev.sergiobelda.todometer.common.domain.usecase.tasklist.GetTaskListsUseCase
import dev.sergiobelda.todometer.common.domain.usecase.tasklist.InsertTaskListUseCase
import dev.sergiobelda.todometer.common.ui.error.mapToErrorUi
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getTaskListsUseCase: GetTaskListsUseCase,
    private val insertTaskListUseCase: InsertTaskListUseCase,
) : ViewModel() {

    var state by mutableStateOf(HomeState(isLoading = true))
        private set

    init {
        getTaskLists()
    }

    private fun getTaskLists() = viewModelScope.launch {
        getTaskListsUseCase().collect { result ->
            result.doIfSuccess { taskLists ->
                state = state.copy(
                    isLoading = false,
                    taskLists = taskLists.toPersistentList(),
                    errorUi = null,
                )
            }.doIfError { error ->
                state = state.copy(
                    isLoading = false,
                    taskLists = persistentListOf(),
                    errorUi = error.mapToErrorUi(),
                )
            }
        }
    }

    fun insertTaskList(name: String) = viewModelScope.launch {
        insertTaskListUseCase.invoke(name)
    }
}
