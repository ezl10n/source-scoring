package com.hpe.g11n.sourcescoring.fileparser;

import java.util.List;

import com.hpe.g11n.sourcescoring.pojo.InputData;

/**
 * 
 * @Descripation
 * @CreatedBy: Ali Cao
 * @Date: 2016年8月17日
 * @Time: 下午2:27:34
 *
 */
public interface IFileParser {
	public List<InputData> parser(String filePath);
}
