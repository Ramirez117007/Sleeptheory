package com.example.sleeptheory01

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object TaskStorage {

    private const val PREFS = "bitacora_prefs"
    private const val KEY_TASKS = "tasks"

    fun load(context: Context): MutableList<Task> {
        val json = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_TASKS, null) ?: return mutableListOf()

        val list = mutableListOf<Task>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Task(
                        name = obj.getString("name"),
                        category = categoryFromKey(obj.getString("category")),
                        date = obj.getString("date"),
                        isSelected = obj.optBoolean("done", false)
                    )
                )
            }
        } catch (e: JSONException) {
            // Datos dañados: se empieza con la lista vacía
        }
        return list
    }

    fun save(context: Context, tasks: List<Task>) {
        val array = JSONArray()
        tasks.forEach { task ->
            array.put(
                JSONObject()
                    .put("name", task.name)
                    .put("category", categoryToKey(task.category))
                    .put("date", task.date)
                    .put("done", task.isSelected)
            )
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TASKS, array.toString())
            .apply()
    }

    private fun categoryToKey(category: TaskCategory): String = when (category) {
        TaskCategory.Bussiness -> "Bussiness"
        TaskCategory.Personal -> "Personal"
        TaskCategory.Other -> "Other"
    }

    private fun categoryFromKey(key: String): TaskCategory = when (key) {
        "Bussiness" -> TaskCategory.Bussiness
        "Personal" -> TaskCategory.Personal
        else -> TaskCategory.Other
    }
}