package com.hpe.g11n.sourcescoring.gui.control;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.text.*;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.WindowConstants;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hpe.g11n.sourcescoring.gui.tasks.SourceScoringTask;

/**
 * Created by foy on 2016-08-05.
 */

public class MainViewController extends BaseController  implements Initializable{

	@FXML
	private Parent root;

	@FXML
	private TextField sourceUrl;

	@FXML
	private TextField outputUrl;

	@FXML
	private GridPane checkRules;

	@FXML
	private ProgressBar progressBar;

	private DirectoryChooser chooser;

	private FileChooser fileChooser;

	private Thread t;
	
	public String info;

	@Inject
	@Named("ruleNames")
	List<String> checkBoxs;

	SourceScoringTask task = new SourceScoringTask();
	
	List<Integer> rules = new ArrayList<Integer>();

	public MainViewController() {
		super();
		injector.injectMembers(task);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (chooser == null) {
			chooser = new DirectoryChooser();
		}
		if (fileChooser == null) {
			fileChooser = new FileChooser();
		}
		
		int k = 0;
		int j = 0;
		for(String checkBoxValue:checkBoxs){
			checkRules.add(new CheckBox(checkBoxValue), j, k);
			j++;
			if(j==4){
				k++;
				j=0;
			}
		}
			
	}

	@FXML
	public void chooseSource(ActionEvent event) {
		fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("LPU", "*.lpu"),
                new FileChooser.ExtensionFilter("JSON", "*.json"),
                new FileChooser.ExtensionFilter("properties", "*.properties")
            );
		List<File> lstFile = fileChooser.showOpenMultipleDialog(root.getScene().getWindow());
		if (lstFile != null) {
			String path = "";
			for(File file:lstFile){
				path = path+file.getAbsolutePath()+";";
			}
			sourceUrl.setText(path.substring(0,path.length()-1));
		}
	}

	@FXML
	public void chooseOutput(ActionEvent event) {
		File file = chooser.showDialog(root.getScene().getWindow());
		if (file != null) {
			outputUrl.setText(file.getAbsolutePath());
		}
	}

	@FXML
	public void close(ActionEvent event) {
		if (t != null && t.isAlive()) {
			t.stop();
		} else {
			deletedFile(outputUrl.getText());
			System.exit(WindowConstants.DO_NOTHING_ON_CLOSE);
		}
	}

	public Parent getRulesConfigView() throws IOException {
		return loadView("fxml/rulesConfigView.fxml", new RulesConfigViewController());
	}
	
	public Parent showInfoView(String info) throws IOException {
		return loadView("fxml/showInfo.fxml", new ShowInfoController(info));
	}
	
	/**
	 * 
	 * @Descripation display the warning information
	 * @CreatedBy: Ali Cao
	 * @Date: 2016年8月18日
	 * @Time: 下午2:25:41
	 * @param event
	 * @throws IOException
	 */
	
	public void showInfoPage(ActionEvent event) throws IOException {
		openPage(showInfoView(info),"Warning");
	}

	@FXML
	public void rulesConfigPage(ActionEvent event) throws IOException {
		openPage(getRulesConfigView(),"Global Rules Setting!");
	}

	/**
	 * 
	 * @Descripation open a fxml on UI
	 * @CreatedBy: Ali Cao
	 * @Date: 2016年8月18日
	 * @Time: 下午3:06:27
	 * @param parent
	 * @param title
	 */
	public void openPage(Parent parent,String title){
		Stage stage=new Stage();
		stage.setScene(new Scene(parent));
		stage.setResizable(false);
		stage.setTitle(title);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(root.getScene().getWindow());
		stage.show();
	}
	
	@FXML
	public void scoring(ActionEvent event) {
		for (int i = 0; i < checkRules.getChildren().size(); i++) {
			CheckBox cb = (CheckBox) checkRules.getChildren().get(i);
			if (cb.isSelected()) {
				rules.add(i);
			}
		}
		try {
			if(sourceUrl.getText() ==null || "".equals(sourceUrl.getText())){
			info="Please choose the source file!";
			showInfoPage(event);
			return;
			}
			if(outputUrl.getText() ==null || "".equals(outputUrl.getText())){
				info="Please choose the folder that write output to it!";
				showInfoPage(event);
				return;
			}
			if(rules.size()==0){
				info="Please select a check point or more than one!";
				showInfoPage(event);
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		progressBar.setVisible(true);
		progressBar.progressProperty().bind(task.progressProperty());
		task.setUp(sourceUrl.getText(), outputUrl.getText() + "/", rules);
		t = new Thread(task);
		t.setDaemon(true);
		t.start();
	}

	public void deletedFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			String[] tempList = file.list();
			File temp = null;
			for (int i = 0; i < tempList.length; i++) {
				if(tempList[i].endsWith(".csv")){
					if (filePath.endsWith(File.separator)) {
						temp = new File(filePath + tempList[i]);
					} else {
						temp = new File(filePath + File.separator + tempList[i]);
					}
					if (temp.isFile()) {
						temp.delete();
					}
				}
			}
		}
	}
}
