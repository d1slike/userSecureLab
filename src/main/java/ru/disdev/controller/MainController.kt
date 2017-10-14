package ru.disdev.controller

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import de.jensd.fx.fontawesome.Icon
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import org.mindrot.jbcrypt.BCrypt
import ru.disdev.entity.User
import ru.disdev.service.deleteByLogin
import ru.disdev.service.findAll
import ru.disdev.service.findByLogin
import ru.disdev.service.saveUser
import ru.disdev.utils.PopupUtils
import ru.disdev.utils.TableUtils


class MainController : Controller {

    @FXML
    private lateinit var userTable: TableView<User>
    @FXML
    private lateinit var editButton: JFXButton
    @FXML
    private lateinit var deleteButton: JFXButton
    @FXML
    private lateinit var newResultButton: JFXButton
    @FXML
    private lateinit var root: StackPane
    private var user: User = User()
    private var form: StackPane? = null
    private var errorDialog: JFXDialog? = null
    private var isNew: Boolean = false
    private var userToEdit: User = User();


    override fun acceptData(`object`: Any) {
        if (`object` is User) {
            user = `object`
        }
    }

    override fun initialize() {
        newResultButton.onAction = EventHandler {
            isNew = true
            editUser(User())
        }
        newResultButton.graphic = Icon("PLUS_CIRCLE")
        newResultButton.text = ""

        editButton.text = ""
        editButton.onAction = EventHandler {
            isNew = false
            userToEdit = userTable.selectionModel.selectedItem
            editUser(userToEdit.copy().apply {
                password.value = ""
            })
        }
        editButton.graphic = Icon("PENCIL")
        editButton.isDisable = true

        deleteButton.isDisable = true
        deleteButton.graphic = Icon("TRASH").apply { textFill = Color.RED }
        deleteButton.onAction = EventHandler {
            val login = userTable.selectionModel.selectedItem.login.value
            if (login == user.login.value) {
                showError("You cant delete yourself", container = root)
            } else {
                deleteByLogin(login)
                val empty = userTable.selectionModel.isEmpty
                editButton.isDisable = empty
                deleteButton.isDisable = empty
            }
        }

        TableUtils.fillTableColumns(User::class.java, userTable)
        userTable.selectionModel.selectedIndexProperty().addListener { _, _, _ ->
            editButton.isDisable = false
            deleteButton.isDisable = false
        }
        userTable.items = findAll()

    }

    private fun editUser(user: User) {
        form = InputDataController(user,
                "Save",
                "Add User", {
            val login = it.login.value
            val existsUser = findByLogin(login)
            if (isNew && existsUser != null) {
                showError("Already exists", "User with login ${it.login.value} already exists")
                return@InputDataController false
            }
            if (!isNew && login == this.user.login.value && it.blocked.value) {
                showError("You cannot block yourself")
                return@InputDataController false
            }
            true
        }, {
            saveUser(it.apply {
                val pass = password.value
                if (pass != null && !pass.isBlank()) {
                    password.value = BCrypt.hashpw(pass, BCrypt.gensalt(13))
                } else {
                    password.value = userToEdit.password.value
                }
                if (!isNew && !userToEdit.checkPassword.value && it.checkPassword.value) {
                    setPassword.value = true
                }
            })
            PopupUtils.infoPopup(root, "User successfully saved", 3)
        }).show()
    }

    private fun showError(title: String,
                          text: String = "",
                          buttonText: String = "Close",
                          container: StackPane? = form,
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
        errorDialog = JFXDialog(container, layout, JFXDialog.DialogTransition.CENTER, false)
        errorDialog?.show()
    }

}