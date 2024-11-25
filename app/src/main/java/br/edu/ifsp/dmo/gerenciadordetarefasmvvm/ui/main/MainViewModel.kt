package br.edu.ifsp.dmo.gerenciadordetarefasmvvm.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.edu.ifsp.dmo.gerenciadordetarefasmvvm.R
import br.edu.ifsp.dmo.gerenciadordetarefasmvvm.data.dao.TaskDao
import br.edu.ifsp.dmo.gerenciadordetarefasmvvm.data.model.Task

class MainViewModel : ViewModel() {
    private val dao = TaskDao

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>>
        get() {
            return _tasks
        }

    private val _insertTask = MutableLiveData<Boolean>()
    val insertTask: LiveData<Boolean> = _insertTask

    private val _updateTask = MutableLiveData<Boolean>()
    val updateTask: LiveData<Boolean>
        get() = _updateTask

    private val _filter = MutableLiveData<Int>()
    val filter: LiveData<Int> = _filter

    init {
        mock()
        load()
        _filter.value = 1
    }

    fun insertTask(description: String) {
        val task = Task(description, false)
        dao.add(task)
        _insertTask.value = true
        load()
    }

    fun updateTask(position: Int) {
        val task = dao.getAll()[position]
        task.isCompleted = !task.isCompleted
        _updateTask.value = true
        load()
    }

    fun updateFilter(newFilter: Int) {
        _filter.value = newFilter
        load()
    }

    private fun mock() {
        dao.add(Task("Arrumar a Cama", false))
        dao.add(Task("Retirar o lixo", false))
        dao.add(Task("Fazer trabalho de DMO1", true))
    }

    private fun load() {
        val filteredTasks = applyFilter()
        _tasks.value = filteredTasks
    }

    private fun applyFilter(): List<Task> {
        /*
        * filtro:
        * 0 = todos
        * 1 = completed
        * 2 = not completed
        */
        return when (_filter.value) {
            1 -> dao.getAll().filter { it.isCompleted }
            2 -> dao.getAll().filter { !it.isCompleted }
            else -> dao.getAll()
        }
    }
}