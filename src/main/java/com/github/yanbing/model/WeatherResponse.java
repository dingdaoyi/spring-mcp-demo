package com.github.yanbing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingyunwei
 */
@Data
public class WeatherResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("count")
    private String count;

    @JsonProperty("info")
    private String info;

    @JsonProperty("infocode")
    private String infocode;

    @JsonProperty("forecasts")
    private List<Forecast> forecasts;

    @Data
    public static class Forecast {
        @JsonProperty("city")
        private String city;

        @JsonProperty("adcode")
        private String adcode;

        @JsonProperty("province")
        private String province;

        @JsonProperty("reporttime")
        private String reportTime;

        @JsonProperty("casts")
        private List<DailyForecast> casts;
    }

    @Data
    public static class DailyForecast {
        @JsonProperty("date")
        private String date;

        @JsonProperty("week")
        private String week;

        @JsonProperty("dayweather")
        private String dayWeather;

        @JsonProperty("nightweather")
        private String nightWeather;

        @JsonProperty("daytemp")
        private String dayTemp;

        @JsonProperty("nighttemp")
        private String nightTemp;

        @JsonProperty("daywind")
        private String dayWind;

        @JsonProperty("nightwind")
        private String nightWind;

        @JsonProperty("daypower")
        private String dayPower;

        @JsonProperty("nightpower")
        private String nightPower;

        @JsonProperty("daytemp_float")
        private String dayTempFloat;

        @JsonProperty("nighttemp_float")
        private String nightTempFloat;

    }
}