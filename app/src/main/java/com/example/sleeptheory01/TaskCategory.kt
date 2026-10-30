package com.example.sleeptheory01

sealed class TaskCategory(var isSelected:Boolean = true) {
    object Personal : TaskCategory()
    object Bussiness : TaskCategory()
    object Other : TaskCategory()
}