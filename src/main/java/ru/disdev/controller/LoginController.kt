package ru.disdev.controller

import com.jfoenix.controls.*
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import org.mindrot.jbcrypt.BCrypt
import ru.disdev.MainApplication
import ru.disdev.entity.ChangePasswordRequest
import ru.disdev.entity.LoginRequest
import ru.disdev.entity.Role
import ru.disdev.entity.User
import ru.disdev.service.*
import ru.disdev.utils.PopupUtils

class LoginController : Controller {

    @FXML
    private lateinit var drawer: JFXDrawer
    @FXML
    private lateinit var titleBurger: JFXHamburger
    @FXML
    private lateinit var titleBurgerContainer: StackPane
    @FXML
    private lateinit var body: JFXScrollPane
    @FXML
    private lateinit var root: StackPane

    private var user: User = User()
    private var loginAttempt: Int = 0
    private var loginForm: StackPane? = null
    private val menu: JFXListView<Label> = JFXListView()
    private var errorDialog: JFXDialog? = null


    override fun initialize() {
        drawer.setOnDrawerOpening {
            val animation = titleBurger.animation
            animation.rate = 1.0
            animation.play()
        }
        drawer.setOnDrawerClosing {
            val animation = titleBurger.animation
            animation.rate = -1.0
            animation.play()
        }
        drawer.setSidePane(menu)
        menu.selectionModel.selectedIndexProperty().addListener { _, _, _ ->
            val item = menu.selectionModel.selectedItem
            val text = item.text
            if (text == LOGOUT) {
                MainApplication.nextState()
            } else if (text == CHANGE_PASSWORD) {
                changePass()
            } else {
                val component = getRoute(text)
                if (component != null) body.content = component
            }
        }
        titleBurgerContainer.setOnMouseClicked({
            if (drawer.isHidden || drawer.isHidding) {
                drawer.open()
            } else {
                drawer.close()
            }
        })
        login()
    }

    private fun login() {
        Platform.runLater {
            loginForm = InputDataController(LoginRequest(), "Sign up", "Sign up", {
                val login = it.login.value
                val password = it.password.value
                val user = findByLogin(login)
                if (user == null) {
                    showError("User not found", "User with login $login not found. Try again")
                    return@InputDataController false
                }
                if (user.blocked.value) {
                    showError("User is blocked")
                    return@InputDataController false
                }
                loginAttempt++
                if (!BCrypt.checkpw(password, user.password.value)) {
                    val lastAttempt = loginAttempt == 3
                    if (lastAttempt) {
                        showError("Incorrect password", buttonText = "Exit", onAction = { Platform.exit() })
                    } else {
                        showError("Incorrect password", "Try again")
                    }
                    return@InputDataController false
                }
                true
            }, {
                val byLogin: User = findByLogin(it.login.value)!!
                user = byLogin
                if (user.setPassword.value) {
                    changePass({ loadUI(user) }, { Platform.exit() })
                } else {
                    loadUI(byLogin)
                }

            }).show({ Platform.exit() })
        }
    }

    private fun changePass(callback: () -> Unit = {}, closeCallbak: () -> Unit = {}) {
        loginForm = InputDataController(ChangePasswordRequest(),
                "Change",
                "Change password", {
            val oldPass = it.oldPassword.value
            val pass = it.password.value
            val pass2 = it.passwordConfirm.value
            if (!BCrypt.checkpw(oldPass, user.password.value)) {
                showError("Incorrect old password", "Try again")
                return@InputDataController false
            }
            if (pass == oldPass) {
                showError("New password is equal old", "Please change new password")
                return@InputDataController false
            }
            if (pass2 != pass) {
                showError("Confirmed password not equal password")
                return@InputDataController false
            }
            if (user.checkPassword.value && !validateNewPass(pass)) {
                showError("Invalid password", "В пароле недопустимо наличие латинских букв, символов кириллицы и цифр")
                return@InputDataController false
            }
            true
        }, {
            callback()
            saveUser(user.apply {
                setPassword.value = false
                password.value = BCrypt.hashpw(it.password.value, BCrypt.gensalt(13))
            })
            PopupUtils.infoPopup(body, "Password successfully changed!", 3)
            loginForm = null
        }).show(closeCallbak)
    }

    private fun loadUI(byLogin: User) {
        loadRouters(byLogin).thenAccept {
            Platform.runLater {
                menu.selectionModel.selectFirst()
            }
        }
        PopupUtils.infoPopup(body, "Hello, ${byLogin.firstName.value} ${byLogin.lastName.value}", 3)
        menu.items.addAll(Label(ABOUT), Label(CHANGE_PASSWORD), Label(LOGOUT))
        if (byLogin.role.value == Role.ADMIN) {
            menu.items.add(0, Label(USERS))
        }
    }

    private fun validateNewPass(pass: String): Boolean {
        return pass.all {
            !(it in 'a'..'Z' || (it in 'а'..'Я') || (it in '0'..'9'))
        }
    }

    private fun showError(title: String,
                          text: String = "",
                          buttonText: String = "Close",
                          container: StackPane? = loginForm,
                          onAction: () -> Unit = {
                              errorDialog?.close()
                              errorDialog = null
                          }) {
        val layout = JFXDialogLayout()
        layout.body.add(Label(text))
        layout.heading.add(Label(title))
        val closeBtn = JFXButton(buttonText)
        closeBtn.onAction = EventHandler { onAction() }
        layout.actions.add(closeBtn)
        errorDialog = JFXDialog(container ?: body, layout, JFXDialog.DialogTransition.CENTER, false)
        errorDialog?.show()
    }

}