package com.spring.batch.helloworld;

import com.spring.batch.helloworld.utils.ParameterValidator;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.util.Arrays;

@EnableBatchProcessing
@SpringBootApplication
@Import(ChunksConfiguration.class)
public class HelloWorldApplication {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private ApplicationContext ctx;

	@Bean
	public CompositeJobParametersValidator validator() {
		CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
		DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator();
		defaultJobParametersValidator.setRequiredKeys(new String[] {"fileName", "name"});


		defaultJobParametersValidator.afterPropertiesSet();
		validator.setValidators(Arrays.asList(new ParameterValidator(), defaultJobParametersValidator));

		return validator;
	}

	@Bean
	public Step step() {
		return this.stepBuilderFactory.get("step")
				.tasklet(new Tasklet() {
					@Override
					public RepeatStatus execute(StepContribution contribution,
							ChunkContext chunkContext) {
						System.out.println("Hello, World!");
						return RepeatStatus.FINISHED;
					}
				}).build();
	}

	@Bean
	public Step step1() {
		return this.stepBuilderFactory.get("step1")
				.tasklet(helloWorldTasklet(null,null))
				.build();
	}

	/*@Bean
	public Job job() {
		return this.jobBuilderFactory.get("job")
				.start(step1())
				.validator(validator())
				.build();
	}*/

	@Bean
	public Job chunkBasedJob() {
		return this.jobBuilderFactory.get("chunkBasedJob")
				.start(ctx.getBean("chunkStep", Step.class))
				.build();
	}

	@Bean
	public Tasklet helloWorldTasklet() {
		return (contribution, chunkContext) -> {
			String name = (String) chunkContext.getStepContext()
					.getJobParameters()
					.get("name");
			System.out.println(String.format("Hello, %s!", name));
			return RepeatStatus.FINISHED;
		};
	}

	@StepScope
	@Bean
	public Tasklet helloWorldTasklet(@Value("#{jobParameters['name']}") String name) {
		return (contribution, chunkContext) -> {
			System.out.println(String.format("Hello, %s!", name));
			return RepeatStatus.FINISHED;
		};
	}

	@StepScope
	@Bean
	public Tasklet helloWorldTasklet(
			@Value("#{jobParameters['name']}") String name,
			@Value("#{jobParameters['fileName']}") String fileName) {
		return (contribution, chunkContext) -> {
			System.out.println(
					String.format("Hola, %s!", name));
			System.out.println(
					String.format("fileName = %s", fileName));return RepeatStatus.FINISHED;
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(HelloWorldApplication.class, args);
	}
}
