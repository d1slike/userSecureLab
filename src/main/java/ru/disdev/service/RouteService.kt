package ru.disdev.service

import javafx.fxml.FXMLLoader
import javafx.scene.layout.Pane
import ru.disdev.MainApplication
import ru.disdev.controller.Controller
import ru.disdev.entity.User
import ru.disdev.utils.DaemonThreadPool
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

private var routers: MutableMap<String, Pane> = ConcurrentHashMap()

const val LOGOUT = "Logout"
const val USERS = "Users"
const val CHANGE_PASSWORD = "Change Password"
const val ABOUT = "About"

fun loadRouters(user: User): CompletableFuture<Unit> {
    val future: CompletableFuture<Unit> = CompletableFuture()
    DaemonThreadPool.execute {
        listOf(USERS to "main.fxml", ABOUT to "about.fxml").forEach { (route, file) ->
            val loader = FXMLLoader(MainApplication.getFXMLUrl(file))
            val pane: Pane = loader.load()
            val controller: Controller? = loader.getController()
            controller?.acceptData(user)
            routers[route] = pane
        }
        future.complete(Unit)
    }
    return future
}

fun getRoute(path: String) = routers[path]