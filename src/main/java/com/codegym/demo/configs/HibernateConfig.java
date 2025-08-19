package com.codegym.demo.configs;

import com.zaxxer.hikari.*;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.*;
import org.springframework.orm.hibernate5.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    @Bean
    public DataSource dataSource() {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:mysql://localhost:3306/app_db?useSSL=false&serverTimezone=UTC");
        cfg.setUsername("root");
        cfg.setPassword("123456@Abc");
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
        cfg.setMaximumPoolSize(10);
        return new HikariDataSource(cfg);
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("com.codegym.demo.models"); // nơi đặt @Entity
        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        props.put("hibernate.hbm2ddl.auto", "update");  // dev: update | validate | none
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        // Giữ nguyên context Spring cho currentSession (Spring 6 vẫn dùng package hibernate5)
        props.put("hibernate.current_session_context_class",
                "org.springframework.orm.hibernate5.SpringSessionContext");

        factoryBean.setHibernateProperties(props);
        return factoryBean;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager tx = new HibernateTransactionManager();
        tx.setSessionFactory(sessionFactory);
        return tx;
    }
}
