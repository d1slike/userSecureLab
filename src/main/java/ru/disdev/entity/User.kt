package ru.disdev.entity

import javafx.beans.property.*
import ru.disdev.entity.input.CheckBox
import ru.disdev.entity.input.ComboBox
import ru.disdev.entity.input.TextField

class User {

    constructor(login: String, role: Role) {
        this.login.value = login
        this.role.value = role
    }

    constructor()

    @TextField(name = "Login")
    @Column(name = "Login")
    val login: StringProperty = SimpleStringProperty("")
    @TextField(name = "Password", isRequired = false, type = Type.PASSWORD)
    val password: StringProperty = SimpleStringProperty("")
    @TextField(name = "First name")
    @Column(name = "First name")
    val firstName: StringProperty = SimpleStringProperty("")
    @TextField(name = "Last name", isRequired = false)
    @Column(name = "Last name")
    val lastName: StringProperty = SimpleStringProperty("")
    @CheckBox(name = "Blocked")
    @Column(name = "Blocked", type = Type.BOOLEAN)
    val blocked: BooleanProperty = SimpleBooleanProperty(false)
    @CheckBox(name = "Validate password")
    @Column(name = "Blocked", type = Type.BOOLEAN)
    val checkPassword: BooleanProperty = SimpleBooleanProperty(true)
    @ComboBox(name = "Role")
    @Column(name = "Role", type = Type.OBJECT)
    @Enum(Role::class)
    val role: ObjectProperty<Role> = SimpleObjectProperty(Role.USER)


    fun copy(): User {
        val new = User()
        new.login.value = login.value
        new.password.value = password.value
        new.firstName.value = firstName.value
        new.lastName.value = lastName.value
        new.blocked.value = blocked.value
        new.checkPassword.value = checkPassword.value
        new.role.value = role.value
        return new
    }
}

enum class Role {
    ADMIN, USER
}

class LoginRequest {
    @TextField(name = "Login")
    val login: StringProperty = SimpleStringProperty("")
    @TextField(name = "Password", type = Type.PASSWORD, isRequired = false)
    val password: StringProperty = SimpleStringProperty("")
}

class ChangePasswordRequest {
    @TextField(name = "Old password", type = Type.PASSWORD, isRequired = false)
    val oldPassword: StringProperty = SimpleStringProperty("")
    @TextField(name = "Password", type = Type.PASSWORD)
    val password: StringProperty = SimpleStringProperty("")
    @TextField(name = "Password once again", type = Type.PASSWORD)
    val passwordConfirm: StringProperty = SimpleStringProperty("")
}