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

    @PostConstruct
    public void init() {
        try (InputStream mapCityIo = getClass().getResourceAsStream("/xlsx/AMap_adcode_citycode.xlsx")) {
            CommonDataListener<CityModel> dataListener = new CommonDataListener<>();
            FastExcel.read(mapCityIo)
                    .sheet()
                    .head(CityModel.class)
                    .registerReadListener(dataListener)
                    .doRead();
            this.cityModels = dataListener.getList()
                    .stream()
                    .collect(Collectors.toMap(CityModel::getCityName, cityModel -> cityModel, (t, t2) -> t));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Tool(name = "queryAdCodeByCityName", description = "根据区县名称获取区域编码")
    public Optional<String> queryAdCodeByCityName(@ToolParam(description = "区县名称") String cityName) {
        log.info("queryAdCodeByCityName: {}", cityName);
        return Optional.ofNullable(cityModels.get(cityName))
                .map(CityModel::getAdCode);
    }

    @Tool(name = "queryByAdCode", description = "根据区域编码查询天气预报")
    public WeatherResponse queryByAdCode(@ToolParam(description = "区域编码") String adCode) {
        log.info("queryByAdCode: {}", adCode);
        WebClient client = WebClient.builder()
                .baseUrl(amapConfigProperties.getUrl()).build();
        WeatherResponse weatherResponse = client.get()
                .uri("/v3/weather/weatherInfo?key=" + amapConfigProperties.getKey()
                     + "&city=" + adCode
                     + "&extensions=all")
//                .attribute("key", amapConfigProperties.getKey())
//                .attribute("city", adCode)
//                .attribute("extensions", "all")
//                .header("User-Agent", "spring-mcp-demo")
                .retrieve()
                .onStatus(status -> {
                            log.info("status: {}", status);
                            return status.is4xxClientError();
                        },
                        response -> Mono.error(new RuntimeException("Not found")))
                .bodyToMono(WeatherResponse.class).block();
        log.info("weatherResponse: {}", weatherResponse);
        return weatherResponse;
    }

}