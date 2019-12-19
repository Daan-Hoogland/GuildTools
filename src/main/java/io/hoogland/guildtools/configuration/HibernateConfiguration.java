package io.hoogland.guildtools.configuration;

import io.hoogland.guildtools.utils.ConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Properties;

@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
@ComponentScan({"io.hoogland.guildtools.models"})
public class HibernateConfiguration {

    private static final String DRIVER = "org.postgresql.Driver";
    private static final String DIALECT = "org.hibernate.dialect.PostgreSQL92Dialect";
    private static final boolean FORMAT_SQL = true;

//    @Bean
//    public LocalSessionFactoryBean sessionFactory() {
//        HashMap config = (HashMap) ConfigUtils.getConfig().get("database");
//
//        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
//        sessionFactory.setDataSource(dataSource(config));
//        sessionFactory.setPackagesToScan("io.hoogland.guildtools.model");
//        sessionFactory.setHibernateProperties(hibernateProperties(config));
//
//        return sessionFactory;
//    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HashMap config = (HashMap) ConfigUtils.getConfig().get("database");

        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource(config));
        em.setPackagesToScan(new String[]{"io.hoogland.guildtools.models"});

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties(config));

        return em;
    }


    public DataSource dataSource(HashMap<String, String> databaseConfig) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DRIVER);
        dataSource.setUrl(databaseConfig.get("url"));
        dataSource.setUsername(databaseConfig.get("user"));
        dataSource.setPassword(databaseConfig.get("password"));
        return dataSource;
    }

    private Properties hibernateProperties(HashMap<String, String> databaseConfig) {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", DIALECT);
        properties.put("hibernate.show_sql", databaseConfig.get("showsql"));
        properties.put("hibernate.format_sql", FORMAT_SQL);
        properties.put("hibernate.hbm2ddl.auto", databaseConfig.get("hbm2ddl"));
        return properties;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }
}

