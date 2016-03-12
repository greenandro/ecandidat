package fr.univlorraine.ecandidat.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.tools.LocalTimePersistenceConverter;
import fr.univlorraine.ecandidat.repositories.CandidatRepository;

/**
 * Configuration JPA
 * 
 * @author Adrien Colson
 */
@Configuration
@EnableTransactionManagement(mode=AdviceMode.ASPECTJ)
@EnableJpaRepositories(basePackageClasses=CandidatRepository.class,entityManagerFactoryRef="entityManagerFactoryEcandidat",transactionManagerRef="transactionManagerEcandidat")
public class JpaConfigEcandidat{

	public final static String PERSISTENCE_UNIT_NAME = "pun-jpa-ecandidat";

	private Logger logger = LoggerFactory.getLogger(JpaConfigEcandidat.class);
	
	/**
	 * @return Source de données
	 */
	@Bean
	public DataSource dataSourceEcandidat() {
		JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
		return dsLookup.getDataSource("java:/comp/env/jdbc/dbEcandidat");
	}
	
	/**
	 * @return Execute la migration flyway
	 */
	@Bean(destroyMethod="")
	@DependsOn("dataSourceEcandidat")
	public Flyway flyway() {
		try{
			logger.info("Database analysis: in progress...");
			Flyway flyway = new Flyway();
			flyway.setDataSource(dataSourceEcandidat());
			flyway.setBaselineOnMigrate(true);
			flyway.setValidateOnMigrate(true);
			flyway.repair();
			flyway.migrate();
			logger.info("Database analysis: finish...");
			return flyway;
		}catch (Exception e){
			logger.error("Database analysis: ERROR",e);
			throw e;
		}		
	}

	/**
	 * @return EntityManager Factory
	 */
	@Bean(name="entityManagerFactoryEcandidat")
	@DependsOn("flyway")
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryEcandidat() {
		LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		localContainerEntityManagerFactoryBean.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
		localContainerEntityManagerFactoryBean.setPackagesToScan(Candidat.class.getPackage().getName(),LocalTimePersistenceConverter.class.getPackage().getName());
		localContainerEntityManagerFactoryBean.setDataSource(dataSourceEcandidat());
		localContainerEntityManagerFactoryBean.setJpaDialect(new EclipseLinkJpaDialect());

		Properties jpaProperties = new Properties();
		/* Active le static weaving d'EclipseLink */
		jpaProperties.put(PersistenceUnitProperties.WEAVING, "static");
		/* Désactive le cache partagé */
		jpaProperties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, String.valueOf(false));
		localContainerEntityManagerFactoryBean.setJpaProperties(jpaProperties);

		EclipseLinkJpaVendorAdapter jpaVendorAdapter = new EclipseLinkJpaVendorAdapter();
		jpaVendorAdapter.setGenerateDdl(false);
		jpaVendorAdapter.setShowSql(false);
		localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);

		return localContainerEntityManagerFactoryBean;
	}
	
	/**
	 * @return Transaction Manager
	 */
	@Bean(name="transactionManagerEcandidat")
	@Primary
	public JpaTransactionManager transactionManagerEcandidat(EntityManagerFactory entityManagerFactoryEcandidat) {
		return new JpaTransactionManager(entityManagerFactoryEcandidat);
	}
}
