package ${rootName}.${tableName}.${package};

import ${rootName}.util.Paging;
/**
 * ${tableComment}condition
 * @author xieb
 * @version 1.00
 * ${date}
 */
public class ${classdef}Condition extends Paging{

    private static final long serialVersionUID = 1L; 

<#list fieldList as field>
    /** ${field.comment} */
    private ${field.type} ${field.name};

</#list>

<#list fieldList as field>

    public ${field.type} get${field.methodAppend}() {
        return ${field.name};
    }

    public void set${field.methodAppend}(${field.type} ${field.name}) {
        this.${field.name} = ${field.name};
    }
</#list>
}

