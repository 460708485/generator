<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE sqlMap PUBLIC "-//ibatis.apache.org//DTD SQL Map 2.0//EN" "http://ibatis.apache.org/dtd/sql-map-2.dtd">

<sqlMap namespace="${daoPackage}.${tblName?cap_first}DaoImpl">

	<typeAlias alias="${tblName}"
		type="${package}.${tblName?cap_first}" />

	<!-- Normal SQL -->
	<sql id="${tblName}Select">
		${selectSql}
	</sql>

	<sql id="${tblName}WhereKey">where ${primaryKeys}</sql>

	<sql id="${tblName}WhereCondition">
		<dynamic prepend="where">
${conditionSql}
		</dynamic>
	</sql>

	<sql id="${tblName}OrderBy">
		order by 
		<isEmpty property="orderConditionString">
            ${sortKeys}
        </isEmpty>
        <isNotEmpty property="orderConditionString">
            $orderConditionString$
        </isNotEmpty>
	</sql>

	<sql id="${tblName}Insert">
${insertKeySql}
		values
${insertValueSql}
	</sql>

	<sql id="${tblName}Update">
		<dynamic prepend="update ${tblNameSql} set">
${updateSql}
		</dynamic>
	</sql>

	<sql id="${tblName}Delete">delete from ${tblNameSql}</sql>

	<!-- search result -->
	<resultMap id="${tblName}" class="${tblName}">
	<#list rsltSetColumList as rsltSetColum>
		<result property="${rsltSetColum.value}" column="${rsltSetColum.key}" />
	</#list>
	</resultMap>

	<!-- search sql -->
	<select id="${tblName}FindByKey" resultMap="${tblName}"
		parameterClass="${tblName}">
		<include refid="${tblName}Select" />
		<include refid="${tblName}WhereKey" />
	</select>

	<select id="${tblName}FindByCondition" resultMap="${tblName}"
		parameterClass="java.util.Map">
		<include refid="${tblName}Select" />
		<include refid="${tblName}WhereCondition" />
		<include refid="${tblName}OrderBy" />
	</select>

	<select id="${tblName}FindAll" resultMap="${tblName}">
		<include refid="${tblName}Select" />
		<include refid="${tblName}OrderBy" />
	</select>

	<select id="${tblName}CountByCondition" resultClass="int"
		parameterClass="java.util.Map">
		select count(${countColumn}) from ${tblNameSql}
		<include refid="${tblName}WhereCondition" />
	</select>

	<select id="${tblName}LockByKey" resultMap="${tblName}"
		parameterClass="${tblName}">
		<include refid="${tblName}Select" />
		<include refid="${tblName}WhereKey" />
		for update with rr
	</select>

	<select id="${tblName}LockByCondition" resultMap="${tblName}"
		parameterClass="java.util.Map">
		<include refid="${tblName}Select" />
		<include refid="${tblName}WhereCondition" />
		<include refid="${tblName}OrderBy" />
		for update with rr
	</select>


	<!-- insert sql -->
	<insert id="${tblName}Insert" parameterClass="${tblName}">
		<include refid="${tblName}Insert" />
	</insert>


	<!-- update sql -->
	<update id="${tblName}Update" parameterClass="${tblName}">
		<include refid="${tblName}Update" />
		<include refid="${tblName}WhereKey" />
	</update>

	<update id="${tblName}UpdateByCondition"
		parameterClass="java.util.Map">
		<include refid="${tblName}Update" />
		<include refid="${tblName}WhereCondition" />
	</update>

	<!-- delete sql -->
	<delete id="${tblName}Delete"
		parameterClass="${tblName}">
		<include refid="${tblName}Delete" />
		<include refid="${tblName}WhereKey" />
	</delete>

	<delete id="${tblName}DeleteByCondition"
		parameterClass="java.util.Map">
		<include refid="${tblName}Delete" />
		<include refid="${tblName}WhereCondition" />
	</delete>

</sqlMap>
