package ${rootName}.${tableName}.${package};

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import ${rootName}.${tableName}.dao.${TableName}Dao;
import ${rootName}.${tableName}.dto.${TableName};
import ${rootName}.${tableName}.dto.${TableName}Condition;
import ${rootName}.${tableName}.service.${TableName}Service;

/**
 * ${tableComment}ServiceImpl
 * @version 1.0
 * @author xieb
 * @date 2014年12月9日 下午2:09:48
 *
 */
@Service
public class ${TableName}ServiceImpl implements ${TableName}Service{

	@Resource
	private ${TableName}Dao ${tableName}Dao;
	
	public Long add(${TableName} po) {
		return ${tableName}Dao.add(po);
	}
	
	public int delete(Long id) {
		return ${tableName}Dao.delete(id);
	}
	
	public int update(${TableName} po) {
		return ${tableName}Dao.update(po);
	}
	
	public ${TableName} findById(Long id) {
		return ${tableName}Dao.findById(id);
	}
	
	public List<${TableName}> findPageByCondition(${TableName}Condition pc) {
		return ${tableName}Dao.findPageByCondition(pc);
	}
}
