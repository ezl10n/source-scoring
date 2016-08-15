package com.hpe.g11n.sourcescoring.gui.tasks;


import com.google.inject.Inject;
import com.hp.g11n.sdl.psl.interop.core.IPslSourceList;
import com.hp.g11n.sdl.psl.interop.core.IPslSourceLists;
import com.hp.g11n.sdl.psl.interop.core.IPslSourceString;
import com.hp.g11n.sdl.psl.interop.core.enums.PslState;
import com.hpe.g11n.sourcescoring.core.ISourceScoring;
import com.hpe.g11n.sourcescoring.gui.utils.PassoloTemplate;
import com.hpe.g11n.sourcescoring.pojo.InputDataObj;
import com.hpe.g11n.sourcescoring.pojo.ReportData;


import com.typesafe.config.Config;

import javafx.concurrent.Task;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SourceScoringTask extends Task<Void> {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private String source;
	private String report;
	private List<Integer> rulesCheckedIdx;
	@Inject
	InputDataObj ido;
	@Inject
	ISourceScoring checkReport;
	private Config config;
	public List<InputDataObj> lstIdo = new ArrayList<InputDataObj>();
	private static final String STATE="psl.psl-generate-sourcescoring-report.concatenation.state";
	private List<String> lstState;
	int totalProgress =0;
	int totalCount =0;

	public void setUp(String sourceDir, String reportDir,List<Integer> rulesCheckedIdx) {
		this.source = sourceDir;
		this.report = reportDir;
		this.rulesCheckedIdx = rulesCheckedIdx;
		checkReport.build(rulesCheckedIdx);
	}

	@Override
	protected Void call() throws Exception {
		// output
		String[] sourcePaths = source.split(";");
		SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmmss");
		final FileWriter fw = new FileWriter(report+"SourceScoring"+sdf.format(new Date())+".csv");
		List<IPslSourceLists> lstIPslSourceLists = new ArrayList<IPslSourceLists>();
		for(String sourcePath:sourcePaths){
			PassoloTemplate.build(sourcePath).process((p,sourceLists) -> {
				lstIPslSourceLists.add(sourceLists);
				int progress=totalProgress;
				totalCount=totalCount + sourceLists.getCount();
				for (int i=0;i<sourceLists.toList().size();i++) {
					for (IPslSourceString sourceString : sourceLists.toList().get(i).getSourceStrings()) {
						for(String sourceStringState:lstState){
							if(sourceString.hasState(PslState.valueOf(sourceStringState))){
								ido = new InputDataObj();
								ido.setLpuName(new File(sourcePath).getName());
								ido.setFileName(sourceString.getIDName());
								ido.setSourceStrings(sourceString.getText());
								ido.setStringId(sourceString.getID());
								lstIdo.add(ido);
								break;
							}
						}
					}
					progress++;
					if(i== sourceLists.toList().size()-1){
						totalProgress = progress;
					}
					this.updateProgress(progress,totalCount);
				}

			});
		}
		checkReport.check(lstIdo);
		//report
		List<ReportData> report = checkReport.report();
		fw.write("LPU NAME,FILE NAME,STRING ID,SOURCE STRINGS,ERROR TYPE,DETAILS\n");
		report.forEach( r -> {
			try {
				fw.write(r.getLpuName()+","+r.getFileName()+","+r.getStringId()
						+","+r.getSourceStrings()+","+r.getErrorType()+","+r.getDetails()+"\n");
				
			} catch (IOException e) {
				log.error("write report CSV failure.",e);
			}

		});
		fw.close();
		return null;
	}
	public void setConfig(Config config) {
		this.config=config;
		lstState=this.config.getStringList(STATE);
	}
}
