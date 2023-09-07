package de.kergru.demo

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.messaging.Message
import org.springframework.messaging.support.GenericMessage
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.Many
import reactor.core.publisher.Sinks.many
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier


@SpringBootApplication
class KotlinSpringCloudAzureEventHubApplication: CommandLineRunner {

	companion object {
		private val LOGGER = org.slf4j.LoggerFactory.getLogger(KotlinSpringCloudAzureEventHubApplication::class.java)

		@JvmStatic
		fun main(args: Array<String>) {
			runApplication<KotlinSpringCloudAzureEventHubApplication>(*args)
		}
	}

	private val many: Many<Message<String>> = many().unicast().onBackpressureBuffer<Message<String>>()



	@Bean
	fun messageProducer(): Supplier<Flux<Message<String>>> {
		return Supplier<Flux<Message<String>>> {
			many.asFlux()
				.doOnNext { m -> LOGGER.info("Manually sending message {}", m) }
				.doOnError { t -> LOGGER.error("Error encountered", t) }
		}
	}

	/*
	@Bean
	fun uppercase(): Function<String, String> {
		return Function<String, String> { s -> s.uppercase() }
	}
	*/

	@Bean
	fun messageConsumer(): Consumer<Message<String>> {
		return Consumer<Message<String>> { message -> LOGGER.info("New message received: '{}'", message.getPayload()) }
	}

	override fun run(vararg args: String?) {
		many.emitNext(GenericMessage("Hello World"), Sinks.EmitFailureHandler.FAIL_FAST)
	}
}