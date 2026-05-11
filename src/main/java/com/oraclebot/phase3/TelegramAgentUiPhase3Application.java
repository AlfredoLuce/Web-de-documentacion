package com.oraclebot.phase3;

import com.oraclebot.phase3.config.AiProps;
import com.oraclebot.phase3.config.BotProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({BotProps.class, AiProps.class})
public class TelegramAgentUiPhase3Application {

    public static void main(String[] args) {
        SpringApplication.run(TelegramAgentUiPhase3Application.class, args);
    }
}

