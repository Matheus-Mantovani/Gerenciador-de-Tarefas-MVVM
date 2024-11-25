package br.edu.ifsp.dmo.gerenciadordetarefasmvvm.ui.main
/*
    Atividades Obrigatórias:

    - Versão 1: Aplicativo desenvolvido de acordo com este roteiro.
    - Versão 2: Atividades pendentes devem ser apresentadas no topo da lista, seguidas pelas atividades concluídas.
    - Versão 3: Incluir na atividade algum mecanismo de filtro que apresente:
        - “Todas as atividades“ (classificadas conforme versão 2)
        - Atividades concluídas
        - Atividades pendentes
*/
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.edu.ifsp.dmo.gerenciadordetarefasmvvm.R
import br.edu.ifsp.dmo.gerenciadordetarefasmvvm.databinding.ActivityMainBinding
import br.edu.ifsp.dmo.gerenciadordetarefasmvvm.databinding.DialogNewTaskBinding
import br.edu.ifsp.dmo.gerenciadordetarefasmvvm.ui.adapter.TaskAdapter
import br.edu.ifsp.dmo.gerenciadordetarefasmvvm.ui.listener.TaskClickListener

class MainActivity : AppCompatActivity(), TaskClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        configListview()
        configSpinner()
        configOnClickListener()
        configObservers()
    }

    override fun clickDone(position: Int) {
        val task = taskAdapter.getItem(position)
        if(task != null) {
            viewModel.updateTask(task.id)
        }
    }

    private fun configListview() {
        taskAdapter = TaskAdapter(this, mutableListOf(), this)
        binding.listTasks.adapter = taskAdapter
    }

    private fun configObservers() {
        viewModel.tasks.observe(this, Observer {
            taskAdapter.updateTasks(it)
        })

        viewModel.insertTask.observe(this, Observer {
            val str: String = if (it) {
                getString(R.string.task_inserted_success)
            } else {
                getString(R.string.task_inserted_error)
            }
            Toast.makeText(this, str, Toast.LENGTH_LONG).show()
        })

        viewModel.updateTask.observe(this, Observer {
            if (it) {
                Toast.makeText(
                    this,
                    getString(R.string.task_updated_success),
                    Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun configOnClickListener() {
        binding.buttonAddTask.setOnClickListener {
            openDialogNewTask()
        }
    }

    private fun openDialogNewTask() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_task, null)
        val bindingDialog = DialogNewTaskBinding.bind(dialogView)

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(getString(R.string.add_new_task))
            .setPositiveButton(
                getString(R.string.save),
                DialogInterface.OnClickListener { dialog, which ->
                    val description = bindingDialog.editDescription.text.toString()
                    viewModel.insertTask(description)
                    dialog.dismiss()
                })
            .setNegativeButton(
                getString(R.string.cancel),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })

        val dialog = builder.create()
        dialog.show()
    }

    private fun configSpinner() {
        val filters = listOf(
            getString(R.string.filter_all),
            getString(R.string.filter_completed),
            getString(R.string.filter_not_completed)
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filters)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilter.adapter = adapter

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.updateFilter(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }
}