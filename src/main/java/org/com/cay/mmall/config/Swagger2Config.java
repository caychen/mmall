package org.com.cay.mmall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by caychen on 2018/7/4.
 */
@Configuration      //让Spring来加载该类配置
@EnableWebMvc       //启用Mvc，非springboot框架需要引入注解@EnableWebMvc
@EnableSwagger2     //启用Swagger2
public class Swagger2Config {

	/**
	 * buildDocket()用于创建Docket的Bean，
	 * buildApiInfo()创建Api的基本信息，用于显示在文档页面上。
	 * select()函数返回一个ApiSelectorBuilder实例，用来控制哪些接口暴露给Swagger2来展现。
	 * 一般采用指定扫描的包路径来定义，本例中Swagger会扫描controller包下所有定义的API，并产生文档内容（除了被@ApiIgnore指定的请求）。
	 *
	 * @return
	 */
	@Bean
	public Docket petApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("org.com.cay.mmall.controller"))
				.paths(PathSelectors.any())
				.build();

	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("服务平台 API")
				.description("服务Api接口")
				.contact(new Contact("caychen", "https://github.com/caychen/mmall", "412425870@qq.com"))
				.version("1.0")
				.build();
	}
}
