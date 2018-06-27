package com.coco.mailtemplate.persistence;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.coco.mailtemplate.Launcher;
import com.coco.util.serviceutils.EncryptorSupplier;
import com.coco.util.serviceutils.SimpleEncryptor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.mailtemplate.template")
@Slf4j
public class PersistenceConfig {
	private static final String DRIVER = "org.postgresql.Driver";

	@Bean
	public EncryptorSupplier propertiesEncryptorSupplier() {
		return SimpleEncryptor::new;
	}

	@Bean
	@Qualifier("env")
	public DatasourceProperties envDatasourceProperties(Environment env, EncryptorSupplier encryptorSupplier)
			throws Exception {

		String url = env.getProperty("COCOON_DB_URL");
		String username = env.getProperty("COCOON_DB_USER");
		String password = env.getProperty("COCOON_DB_USER_PWD");
		String cryptKey = env.getProperty("COCOON_CRYPT_KEY");

		DatasourceProperties datasourceProperties = url != null && username != null && password != null
				&& cryptKey != null
						? DatasourceProperties.builder().driver(DRIVER).uri(url).username(username)
								.password(encryptorSupplier.get(cryptKey).decrypt(password)).build()
						: null;

		log.info("Processing datasource properties from env variables: {}",
				datasourceProperties != null ? "SUCCESS" : "FAIL");
		return datasourceProperties;
	}

	@Bean
	@Qualifier("file")
	public DatasourceProperties fileDatasourceProperties(Environment env, EncryptorSupplier encryptorSupplier)
			throws Exception {
		String url = env.getProperty("cocoon.db.url");
		String username = env.getProperty("cocoon.db.username");
		String password = env.getProperty("cocoon.db.password");
		String key = env.getProperty("cocoon.db.key");

		DatasourceProperties datasourceProperties = url != null && username != null && password != null
				? DatasourceProperties.builder().driver(DRIVER).uri(url).username(username)
						.password(encryptorSupplier.get(key).decrypt(password)).build()
				: null;

		log.info("Processing datasource properties from properties file: {}",
				datasourceProperties != null ? "SUCCESS" : "FAIL");
		return datasourceProperties;
	}

	@Bean
	@Primary
	public DatasourceProperties datasourceProperties(@Qualifier("env") DatasourceProperties envDatasourceProperties,
			@Qualifier("file") DatasourceProperties fileDatasourceProperties) {
		if (envDatasourceProperties != null) {
			log.info("Using datasource properties from env variables");
			return envDatasourceProperties;
		}

		if (fileDatasourceProperties != null) {
			log.info("Using datasource properties from properties file");
			return fileDatasourceProperties;
		}

		String errorMessage = "Could not create datasource properties neither from env variables, nor from property file";
		log.error(errorMessage);
		throw new IllegalStateException(errorMessage);
	}

	@Bean
	public HibernateProperties hibernateProperties() {
		return HibernateProperties.builder().defaultSchema("cocoon_plat")
				.dialect("org.hibernate.dialect.PostgreSQL9Dialect").showSql(true).formatSql(true).build();
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
		return jpaTransactionManager;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Qualifier("hikariDataSource") DataSource dataSource, HibernateProperties hibernateProperties) {
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(dataSource);
		entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		entityManagerFactoryBean.setPackagesToScan(Launcher.class.getPackage().getName());

		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.default_schema", hibernateProperties.getDefaultSchema());
		jpaProperties.put("hibernate.dialect", hibernateProperties.getDialect());
		jpaProperties.put("hibernate.show_sql", hibernateProperties.isShowSql());
		jpaProperties.put("hibernate.format_sql", hibernateProperties.isFormatSql());

		entityManagerFactoryBean.setJpaProperties(jpaProperties);
		return entityManagerFactoryBean;
	}

	@Bean(destroyMethod = "close")
	public HikariDataSource hikariDataSource(DatasourceProperties datasourceProperties) {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName(datasourceProperties.getDriver());
		hikariConfig.setJdbcUrl(datasourceProperties.getUri());
		hikariConfig.setUsername(datasourceProperties.getUsername());
		hikariConfig.setPassword(datasourceProperties.getPassword());
		return new HikariDataSource(hikariConfig);
	}
}