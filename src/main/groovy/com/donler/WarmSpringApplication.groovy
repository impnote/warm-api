package com.donler

import com.donler.repository.trend.ShowtimeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class WarmSpringApplication implements CommandLineRunner {


	@Autowired
	ShowtimeRepository showTimeRepository

	@Override
	void run(String... args) throws Exception {


    }

	static void main(String[] args) {
		SpringApplication.run WarmSpringApplication, args
	}
}
