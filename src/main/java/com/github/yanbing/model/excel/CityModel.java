
package com.github.yanbing.model.excel;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author dingyunwei
 */
@Data
public class CityModel {
    /**
     * 区县名称
     */
    @ExcelProperty("中文名")
    private String cityName;

    /**
     * cityCode
     */
    @ExcelProperty("citycode")
    private String cityCode;

    /**
     * 区域编码
     */
    @ExcelProperty("adcode")
    private String adCode;
}