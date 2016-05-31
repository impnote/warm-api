package com.donler

import com.donler.repository.trend.ShowtimeRepository
import com.donler.repository.user.TokenRepository
import com.donler.repository.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class WarmSpringApplication implements CommandLineRunner {


	@Autowired
	ShowtimeRepository showTimeRepository
	@Autowired
	UserRepository userRepository
	@Autowired
	TokenRepository tokenRepository

	@Override
	void run(String... args) throws Exception {

		showTimeRepository.deleteAll()
		userRepository.deleteAll()
		tokenRepository.deleteAll()

	}

	static void main(String[] args) {
		SpringApplication.run WarmSpringApplication, args
	}
}
