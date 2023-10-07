package com.github.kelinzhou.uikittemplates.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.github.kelinzhou.uikittemplates.MyBundle

@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
        println(project)
    }

    fun getRandomNumber() = (1..100).random()
}
