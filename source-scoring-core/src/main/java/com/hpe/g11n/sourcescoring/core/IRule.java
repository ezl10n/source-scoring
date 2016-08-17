package com.hpe.g11n.sourcescoring.core;




import com.hpe.g11n.sourcescoring.pojo.InputData;
import com.hpe.g11n.sourcescoring.pojo.ReportData;
import com.typesafe.config.Config;

import java.util.List;

public interface IRule {
	boolean check(List<InputData> lstIdo);
	List<ReportData> gatherReport();
	void setConfig(Config config);
}
