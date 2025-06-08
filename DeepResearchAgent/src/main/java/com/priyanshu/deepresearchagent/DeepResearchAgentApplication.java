package com.priyanshu.deepresearchagent;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DeepResearchAgentApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();
        for (DotenvEntry entry : dotenv.entries()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }
        SpringApplication.run(DeepResearchAgentApplication.class, args);
    }
}
