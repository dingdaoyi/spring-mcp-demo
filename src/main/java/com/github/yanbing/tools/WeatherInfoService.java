package com.github.yanbing.tools;

import cn.idev.excel.FastExcel;
import com.github.yanbing.config.AmapConfigProperties;
import com.github.yanbing.model.WeatherResponse;
import com.github.yanbing.model.an.McpService;
import com.github.yanbing.model.excel.CityModel;
import com.github.yanbing.model.excel.CommonDataListener;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 高德天气预报查询
 */
@Slf4j
@McpService
public class WeatherInfoService {

    @Resource
    private AmapConfigProperties amapConfigProperties;

    private Map<String, CityModel> cityModels;

    // 初始化城市编码数据
    @PostConstruct
    public void init() {
        try (InputStream mapCityIo = getClass().getResourceAsStream("/xlsx/AMap_adcode_citycode.xlsx")) {
            CommonDataListener<CityModel> dataListener = new CommonDataListener<>();
            FastExcel.read(mapCityIo)
                    .sheet()
                    .head(CityModel.class)
                    .registerReadListener(dataListener)
                    .doRead();
            this.cityModels = dataListener.getList().stream()
                    .collect(Collectors.toMap(CityModel::getCityName,
                            Function.identity(),
                            (t, t2) -> t));
        } catch (IOException e) {
            throw new RuntimeException("加载城市编码数据失败", e);
        }
    }

    // 工具方法：根据城市名查询区域编码
    @Tool(name = "queryAdCodeByCityName",
            description = "根据区县名称获取区域编码")
    public Optional<String> queryAdCodeByCityName(
            @ToolParam(description = "区县名称") String cityName) {

        log.info("查询城市编码: {}", cityName);
        return Optional.ofNullable(cityModels.get(cityName))
                .map(CityModel::getAdCode);
    }

    // 工具方法：根据区域编码查询天气
    @Tool(name = "queryByAdCode",
            description = "根据区域编码查询天气预报")
    public WeatherResponse queryByAdCode(
            @ToolParam(description = "区域编码") String adCode) {

        log.info("查询天气信息，区域编码: {}", adCode);

        return WebClient.builder()
                .baseUrl(amapConfigProperties.getUrl())
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/v3/weather/weatherInfo")
                        .queryParam("key", amapConfigProperties.getKey())
                        .queryParam("city", adCode)
                        .queryParam("extensions", "all")
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        Mono.error(new RuntimeException("高德天气服务异常")))
                .bodyToMono(WeatherResponse.class)
                .block();
    }

}