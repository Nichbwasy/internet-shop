package com.shop.common.utils.all.config.dao;

import liquibase.change.DatabaseChange;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
@Configuration
@ConditionalOnClass({ SpringLiquibase.class, DatabaseChange.class })
@ConditionalOnProperty(prefix = "spring.liquibase", name = "enabled", matchIfMissing = true)
@AutoConfigureAfter({ DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@Import({DatabaseSchemaInitConfiguration.SpringLiquibaseDependsOnPostProcessor.class})
public class DatabaseSchemaInitConfiguration {

    @Component
    @ConditionalOnProperty(prefix = "spring.liquibase", name = "enabled", matchIfMissing = true)
    public static class SchemaInitBean implements InitializingBean {

        private final DataSource dataSource;
        private final String schemaName;

        public SchemaInitBean(DataSource dataSource, @Value("${database.schema.name}") String schemaName) {
            this.dataSource = dataSource;
            this.schemaName = schemaName;
        }

        @Override
        public void afterPropertiesSet() {
            // Creates schema if it not exists
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                log.info("Creating an '{}' schema if it not exists.", schemaName);
                statement.execute("create schema if not exists \"" + schemaName + "\" ");
            } catch (SQLException e) {
                log.error("Failed to initialize a schema '{}'! {}", schemaName, e.getMessage());
                throw new RuntimeException(String.format("Failed to initialize a schema '%s'! %s", schemaName, e.getMessage()));
            }
        }
    }

    @ConditionalOnBean(SchemaInitBean.class)
    static class SpringLiquibaseDependsOnPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {
        SpringLiquibaseDependsOnPostProcessor() {
            super(SpringLiquibase.class, SchemaInitBean.class);
        }
    }

}
