package ru.disdev.controller

import com.jfoenix.controls.JFXProgressBar
import javafx.fxml.FXML
import javafx.scene.control.Label
import ru.disdev.MainApplication
import ru.disdev.service.LoadDataService
import ru.disdev.utils.AlertUtils


class LoadController : Controller {

    private val INITIAL_MESSAGE = "Загрузка данных"

    @FXML
    private lateinit var progressBar: JFXProgressBar
    @FXML
    private lateinit var infoLabel: Label

    override fun initialize() {
        val service = LoadDataService()
        service.setOnSucceeded({ MainApplication.nextState() })
        service.setOnFailed({ event -> AlertUtils.showMessageAndCloseProgram(event.source.exception) })
        progressBar.progressProperty().bind(service.progressProperty())
        infoLabel.text = INITIAL_MESSAGE
        service.start()
    }

}