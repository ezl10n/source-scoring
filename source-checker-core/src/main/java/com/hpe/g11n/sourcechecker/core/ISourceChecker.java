package com.hpe.g11n.sourcechecker.core;




import java.util.List;

import com.hpe.g11n.sourcechecker.pojo.InputData;
import com.hpe.g11n.sourcechecker.pojo.ReportData;

public interface ISourceChecker {
	String check(List<InputData> lstIdo,String product);
    String check(List<InputData> lstIdo,String product,ITaskProgressCallback callBack);
    List<ReportData> report();
    void build(List<Integer> rulesChecked);
}
