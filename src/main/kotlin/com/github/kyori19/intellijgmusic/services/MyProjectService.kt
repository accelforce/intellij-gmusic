package com.github.kyori19.intellijgmusic.services

import com.intellij.openapi.project.Project
import com.github.kyori19.intellijgmusic.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
