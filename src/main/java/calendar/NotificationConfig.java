package calendar;

import com.github.javafaker.ChuckNorris;
import com.github.javafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class NotificationConfig {

    @Bean
    public ChuckNorris chuckNorris() {
        return (new Faker()).chuckNorris();
    }
}
