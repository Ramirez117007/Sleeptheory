package com.example.sleeptheory01

import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TasksViewHolder (view: View) : RecyclerView.ViewHolder(view){

    private  val tvTask: TextView = view.findViewById(R.id.tvTask)
    private  val cbTask: CheckBox = view.findViewById(R.id.cbTask)

    private val tvDate: TextView = view.findViewById(R.id.tvDate)
    fun render(task: Task){

        if (task.isSelected){
            tvTask.paintFlags = tvTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else{
            tvTask.paintFlags = tvTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        tvTask.text = task.name
        cbTask.isChecked = task.isSelected

        tvDate.text = task.date

        val color = when(task.category){
            TaskCategory.Bussiness -> R.color.todo_business_category
            TaskCategory.Other -> R.color.todo_other_category
            TaskCategory.Personal -> R.color.todo_personal_category
        }
        cbTask.buttonTintList = ColorStateList.valueOf(
            ContextCompat.getColor(cbTask.context, color)
        )

    }
}