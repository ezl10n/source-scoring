package com.hpe.g11n.sourcescoring.core.rules;


import com.hpe.g11n.sourcescoring.core.IRule;
import com.hpe.g11n.sourcescoring.core.annotation.RuleData;
import com.hpe.g11n.sourcescoring.pojo.InputDataObj;
import com.hpe.g11n.sourcescoring.pojo.ReportData;
import com.hpe.g11n.sourcescoring.utils.Constant;
import com.typesafe.config.Config;

import java.util.List;

@RuleData(id="VariablesCheckRule",name=Constant.VARIABLES,order=4,ruleClass = VariablesCheckRule.class)
public class VariablesCheckRule implements IRule{

	@Override
	public List<ReportData> gatherReport() {
		return null;
	}

	@Override
	public void setConfig(Config config) {

	}

	@Override
	public boolean check(List<InputDataObj> lstIdo) {
		// TODO Auto-generated method stub
		return false;
	}

}
