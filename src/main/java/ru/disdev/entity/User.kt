package ru.disdev.entity

import javafx.beans.property.*
import ru.disdev.entity.input.TextField

class User {

    constructor(login: String, role: Role) {
        this.login.value = login
        this.role.value = role
    }

    constructor()

    val login: StringProperty = SimpleStringProperty("")
    val password: StringProperty = SimpleStringProperty("")
    val firstName: StringProperty = SimpleStringProperty("")
    val lastName: StringProperty = SimpleStringProperty("")
    val blocked: BooleanProperty = SimpleBooleanProperty(false)
    val checkPassword: BooleanProperty = SimpleBooleanProperty(true)
    val role: ObjectProperty<Role> = SimpleObjectProperty(Role.EMPTY)

}

enum class Role {
    ADMIN, USER, EMPTY
}

class LoginRequest {
    @TextField(name = "Логин")
    val login: StringProperty = SimpleStringProperty("")
    @TextField(name = "Пароль", type = Type.PASSWORD, isRequired = false)
    val password: StringProperty = SimpleStringProperty("")
}