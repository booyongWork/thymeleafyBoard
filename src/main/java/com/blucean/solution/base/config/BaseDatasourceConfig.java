package com.blucean.solution.base.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = {"com.blucean.solution.**.mapper"}, sqlSessionFactoryRef = "baseSqlSessionFactory")
public class BaseDatasourceConfig {

    @Bean(name = "baseDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource baseDataSource() {return DataSourceBuilder.create().type(HikariDataSource.class).build();}

    @Bean(name = "baseSqlSessionFactory")
    @Primary
    public SqlSessionFactory baseSqlSessionFactory(
        @Qualifier("baseDataSource") DataSource baseDataSource, ApplicationContext applicationContext
    ) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();

        sessionFactoryBean.setDataSource(baseDataSource);
        sessionFactoryBean.setTypeAliasesPackage("com.blucean.solution.**.dto");
        sessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mapper/mybatis-config.xml"));
        sessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mapper/**/*Mapper.xml"));

        return sessionFactoryBean.getObject();
    }

    @Bean(name = "baseSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate baseSqlSessionTemplate(SqlSessionFactory baseSqlSessionFactory) throws Exception {
        final SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(baseSqlSessionFactory);

        return sqlSessionTemplate;
    }

    @Bean(name = "baseTransactionManager")
    @Primary
    public DataSourceTransactionManager baseTransactionManager(@Qualifier("baseDataSource") DataSource baseDataSource) {
        return new DataSourceTransactionManager(baseDataSource);
    }
}
