package com.jameswhatcott.finance.personal_finance_tracker.config;

// import com.plaid.client.ApiClient;
// import com.plaid.client.request.PlaidApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlaidConfig {
    
    @Value("${plaid.client-id:}")
    private String clientId;
    
    @Value("${plaid.secret:}")
    private String secret;
    
    @Value("${plaid.environment:sandbox}")
    private String environment;
    
    @Bean
    public String plaidClientId() {
        return clientId;
    }
    
    @Bean
    public String plaidSecret() {
        return secret;
    }
    
    @Bean
    public String plaidEnvironment() {
        return environment;
    }
}
