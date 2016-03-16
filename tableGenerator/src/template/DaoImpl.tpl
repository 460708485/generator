package ${rootName}.${tableName}.${package};

import org.springframework.stereotype.Repository;

import ${rootName}.base.BaseDaoImpl;
import ${rootName}.${tableName}.dao.${TableName}Dao;
import ${rootName}.${tableName}.dto.${TableName};
import ${rootName}.${tableName}.dto.${TableName}Condition;

/**
 * ${tableComment}daoImpl
 * @author xieb
 * @version 1.00
 * ${date}
 */
 
@Repository
public class ${classdef} extends BaseDaoImpl<${TableName},${TableName}Condition> implements ${TableName}Dao{
	
}
