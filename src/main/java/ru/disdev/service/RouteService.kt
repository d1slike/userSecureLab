package ru.disdev.service

import javafx.fxml.FXMLLoader
import javafx.scene.layout.Pane
import ru.disdev.MainApplication
import ru.disdev.controller.Controller
import ru.disdev.controller.getUser
import ru.disdev.utils.DaemonThreadPool
import java.util.concurrent.ConcurrentHashMap

private var routers: MutableMap<String, Pane> = ConcurrentHashMap()

const val EXIT = "Exit"
const val USERS = "Users"
const val PROFILE = "My profile"
const val ABOUT = "About"

fun loadRouters() {
    DaemonThreadPool.execute {
        listOf(USERS to "users.fxml", PROFILE to "profile.fxml",
                ABOUT to "about.fxml").forEach { (route, file) ->
            val loader = FXMLLoader(MainApplication.getFXMLUrl(file))
            val pane: Pane = loader.load()
            val controller: Controller? = loader.getController()
            controller?.acceptData(getUser())
            routers[route] = pane
        }
    }
}

fun getRoute(path: String) = routers[path]