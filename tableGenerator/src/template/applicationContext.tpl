<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
    xmlns:tx="http://www.springframework.org/schema/tx" xmlns:flex="http://www.springframework.org/schema/flex"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd
        http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd
        http://www.springframework.org/schema/flex http://www.springframework.org/schema/flex/spring-flex-1.0.xsd">

	<!-- Service -->
	<!-- ======================================================================================== -->
	<#list serviceBeanList as serviceBean>
	<bean id="${servicePackage}.${serviceBean.key}ServiceImpl" class="${servicePackage}.${serviceBean.key}ServiceImpl">
		<flex:remoting-destination />
		<property name="dao"><ref bean="${daoPackage}.${serviceBean.key}Dao"/></property>
	</bean>
	</#list>
	<!-- ============== -->
	<!-- ======================================================================================== -->

	<!-- DAO -->
	<!-- ======================================================================================== -->
	<#list daoBeanList as daoBean>
	<bean id="${daoPackage}.${daoBean.key}Dao" class="org.springframework.aop.framework.ProxyFactoryBean" >
        <property name="proxyInterfaces">
            <value>com.base.dao.DaoIf</value>
        </property>
        <property name="target">
            <ref local="${daoPackage}.${daoBean.key}DaoImpl"/>
        </property>
    </bean>
    <bean id="${daoPackage}.${daoBean.key}DaoImpl" class="${daoPackage}.${daoBean.key}DaoImpl">
        <property name="sqlMapClient"><ref bean="sqlMapClient"/></property>
        <property name="dataSource"><ref bean="dataSource"/></property>
    </bean>
	</#list>
	<!-- ============== -->
	<!-- ======================================================================================== -->

</beans>