package ru.disdev.controller

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import com.jfoenix.controls.JFXProgressBar
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import org.apache.commons.codec.digest.DigestUtils
import ru.disdev.MainApplication
import ru.disdev.entity.PhraseRequest
import ru.disdev.service.LoadDataService
import ru.disdev.service.PHRASE
import ru.disdev.utils.AlertUtils


class LoadController : Controller {

    private val INITIAL_MESSAGE = "Loading..."

    @FXML
    private lateinit var progressBar: JFXProgressBar
    @FXML
    private lateinit var infoLabel: Label
    @FXML
    private lateinit var body: StackPane

    private var loginForm: StackPane? = null
    private var errorDialog: JFXDialog? = null

    override fun initialize() {
        infoLabel.text = INITIAL_MESSAGE
        Platform.runLater {
            loginForm = InputDataController(PhraseRequest(), "Next", "", {
                val value = it.phrase.value
                val md5Hex = DigestUtils.md5(value)
                if (!md5Hex.contentEquals(PHRASE)) {
                    showError("Incorrect phrase", buttonText = "Exit", onAction = { Platform.exit() })
                    return@InputDataController false
                }
                true
            }, { load() }).show { Platform.exit() }
        }
    }

    private fun load() {
        val service = LoadDataService()
        service.setOnSucceeded({ MainApplication.nextState() })
        service.setOnFailed({ event -> AlertUtils.showMessageAndCloseProgram(event.source.exception) })
        progressBar.progressProperty().bind(service.progressProperty())
        service.start()
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