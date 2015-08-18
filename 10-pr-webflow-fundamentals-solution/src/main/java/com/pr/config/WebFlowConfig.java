package com.pr.config;

import com.pr.audit.AuditFlowExecutorListener;
import com.pr.resolver.CustomValidationHintResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.webflow.config.AbstractFlowConfiguration;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.mvc.builder.MvcViewFactoryCreator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by iuliana.cosmina on 7/12/15.
 */
@Configuration
public class WebFlowConfig extends AbstractFlowConfiguration {

    @Autowired
    private MvcConfig mvcConfig;

    @Bean
    public FlowExecutor flowExecutor() {
        return getFlowExecutorBuilder(flowRegistry())
                .addFlowExecutionListener(new AuditFlowExecutorListener(), "*")
                .build();
    }

    @Bean
    public FlowDefinitionRegistry flowRegistry() {
        return getFlowDefinitionRegistryBuilder(flowBuilderServices())
                .setBasePath("/WEB-INF")
                .addFlowLocationPattern("/**/*-flow.xml").build();
    }

    @Bean
    public FlowBuilderServices flowBuilderServices() {
        return getFlowBuilderServicesBuilder()
                .setViewFactoryCreator(mvcViewFactoryCreator())
                .setValidator(this.mvcConfig.validator())
                .setConversionService(conversionService())
                .setValidationHintResolver(customValidationHintResolver())
                .setDevelopmentMode(true)
                .build();
    }

    @Bean
    public CustomValidationHintResolver customValidationHintResolver(){
        return new CustomValidationHintResolver();
    }

    @Bean
    public MvcViewFactoryCreator mvcViewFactoryCreator() {
        MvcViewFactoryCreator factoryCreator = new MvcViewFactoryCreator();
        factoryCreator.setViewResolvers(Arrays.<ViewResolver>asList(this.mvcConfig.tilesViewResolver()));
        factoryCreator.setUseSpringBeanBinding(true);
        return factoryCreator;
    }

    @Bean
    DefaultConversionService conversionService() {
        return new DefaultConversionService(conversionServiceFactoryBean().getObject());
    }


    @Bean
    FormattingConversionServiceFactoryBean conversionServiceFactoryBean() {
        FormattingConversionServiceFactoryBean fcs = new FormattingConversionServiceFactoryBean();
        Set<Formatter> fmts = new HashSet<>();
        fmts.add(this.mvcConfig.dateFormatter());
        fmts.add(this.mvcConfig.hospitalFormatter());
        fcs.setFormatters(fmts);
        return fcs;

    }
}