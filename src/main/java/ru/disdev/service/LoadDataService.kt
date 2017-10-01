package ru.disdev.service

import javafx.concurrent.Service
import javafx.concurrent.Task
import org.mindrot.jbcrypt.BCrypt
import ru.disdev.entity.Role
import ru.disdev.entity.User
import java.io.File


class LoadTask : Task<Unit>() {
    override fun call() {
        val file = File(FILE_NAME)
        if (!file.exists()) {
            saveUser(User("ADMIN", Role.ADMIN).apply {
                password.value = BCrypt.hashpw("", BCrypt.gensalt(13))
                firstName.value = "Admin"
            })
        } else {
            loadData()
        }
        Thread.sleep(3000)
    }
}

class LoadDataService : Service<Unit>() {
    override fun createTask(): Task<Unit> = LoadTask()
}