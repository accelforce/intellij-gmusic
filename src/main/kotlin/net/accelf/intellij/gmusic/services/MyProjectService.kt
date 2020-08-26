package net.accelf.intellij.gmusic.services

import com.intellij.openapi.project.Project
import net.accelf.intellij.gmusic.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
