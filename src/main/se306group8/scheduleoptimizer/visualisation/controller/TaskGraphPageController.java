package se306group8.scheduleoptimizer.visualisation.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.VBox;
import se306group8.scheduleoptimizer.visualisation.manager.ScheduleManager;

import java.net.URL;
import java.util.ResourceBundle;

public class TaskGraphPageController extends Controller {

	@FXML
	private VBox tasks;
	@FXML
	private VBox processors;
	@FXML
	private LineChart chart;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		startManager(new ScheduleManager(
				tasks, processors, chart
		),0l,1000l);

	}

}
