package com.example.sleeptheory01

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sleeptheory01.TaskCategory.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date

class BitacoraFragment : Fragment() {

    private val categories = listOf(
        Bussiness,
        Personal,
        Other
    )

    private lateinit var tasks: MutableList<Task>

    private lateinit var rvCategories: RecyclerView
    private lateinit var categoriesAdapter: CategoriesAdapter

    private lateinit var rvTasks: RecyclerView
    private lateinit var tasksAdapter: TasksAdapter

    private lateinit var fabAddTask: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bitacora, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tasks = TaskStorage.load(requireContext())
        initComponent(view)
        initUI()
        initListeners()
    }

    private fun initListeners() {
        fabAddTask.setOnClickListener { showDialog() }
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_task)

        val btnAddTask: Button = dialog.findViewById(R.id.btnAddTask)
        val etTask: EditText = dialog.findViewById(R.id.etTask)
        val rgCategories: RadioGroup = dialog.findViewById(R.id.rgCategories)

        btnAddTask.setOnClickListener {
            val currentTask = etTask.text.toString().trim()
            if (currentTask.isNotEmpty()) {
                val selectedId = rgCategories.checkedRadioButtonId
                val currentCategory: TaskCategory = if (selectedId == -1) {
                    Other
                } else {
                    val selectedRadioButton: RadioButton = rgCategories.findViewById(selectedId)
                    when (selectedRadioButton.text.toString()) {
                        getString(R.string.todo_dialog_category_business) -> Bussiness
                        getString(R.string.todo_dialog_category_personal) -> Personal
                        else -> Other
                    }
                }

                val formattedDate = SimpleDateFormat("EEEE, MMMM dd, yyyy").format(Date())
                tasks.add(Task(currentTask, currentCategory, formattedDate))
                TaskStorage.save(requireContext(), tasks)
                updateTasks()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun initComponent(view: View) {
        rvCategories = view.findViewById(R.id.rvCategories)
        rvTasks = view.findViewById(R.id.rvTasks)
        fabAddTask = view.findViewById(R.id.fabAddTask)
    }

    private fun initUI() {
        categoriesAdapter = CategoriesAdapter(categories) { position -> updateCategories(position) }
        rvCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvCategories.adapter = categoriesAdapter

        tasksAdapter = TasksAdapter(tasks) { position -> onItemSelected(position) }
        rvTasks.layoutManager = LinearLayoutManager(requireContext())
        rvTasks.adapter = tasksAdapter

        updateTasks()
    }

    private fun onItemSelected(position: Int) {
        // La posición es de la lista que se está mostrando (ya filtrada)
        val task = tasksAdapter.tasks[position]
        task.isSelected = !task.isSelected
        TaskStorage.save(requireContext(), tasks)
        updateTasks()
    }

    private fun updateCategories(position: Int) {
        categories[position].isSelected = !categories[position].isSelected
        categoriesAdapter.notifyItemChanged(position)
        updateTasks()
    }

    private fun updateTasks() {
        val selectedCategories: List<TaskCategory> = categories.filter { it.isSelected }
        val newTasks = tasks.filter { selectedCategories.contains(it.category) }
        tasksAdapter.tasks = newTasks
        tasksAdapter.notifyDataSetChanged()
    }
}