package ru.disdev.controller

import com.jfoenix.controls.*
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import org.mindrot.jbcrypt.BCrypt
import ru.disdev.entity.LoginRequest
import ru.disdev.entity.Role
import ru.disdev.entity.User
import ru.disdev.service.*
import ru.disdev.utils.PopupUtils


private var user: User? = null

fun getUser() = user

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
            if (text == EXIT) {
                Platform.exit()
            }
            val component = getRoute(text)
            if (component != null) body.content = component
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
            loginForm = InputDataController(LoginRequest(), "Войти", "Вход", {
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
                loadRouters()
                PopupUtils.infoPopup(body, "Hello, ${byLogin.firstName.value} ${byLogin.lastName.value}", 3)
                menu.items.addAll(Label(PROFILE), Label(ABOUT), Label(EXIT))
                if (byLogin.role.value == Role.ADMIN) {
                    menu.items.add(0, Label(USERS))
                }
            }).show({ Platform.exit() })
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