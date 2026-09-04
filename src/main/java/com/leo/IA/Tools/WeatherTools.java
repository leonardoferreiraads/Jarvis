package com.leo.IA.Tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherTools {
    @Value("${weather.api.key}")
    private String weatherKey;

    @Tool("Busca a previsão do tempo de uma cidade")
    public String buscarInfoClima(String localizacao) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://api.openweathermap.org/data/2.5/weather?q="
                + localizacao
                + "&appid=" + weatherKey
                + "&units=metric&lang=pt_br";

        return restTemplate.getForObject(url, String.class);
    }
}