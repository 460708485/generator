package generator.bean;

public class TableInfoBean {

    private String  columnName = null;

    private String  typeName   = null;

    private int     size       = -1;

    private boolean primaryKey = false;

    private boolean isNullAble = false;


    public boolean isPrimaryKey() {

        return primaryKey;
    }


    public void setPrimaryKey(boolean primaryKey) {

        this.primaryKey = primaryKey;
    }


    public String getColumnName() {

        return columnName;
    }


    public void setColumnName(String columnName) {

        this.columnName = columnName;
    }


    public String getTypeName() {

        return typeName;
    }


    public void setTypeName(String typeName) {

        this.typeName = typeName;
    }


    public int getSize() {

        return size;
    }


    public void setSize(int size) {

        this.size = size;
    }


    public boolean isNullAble() {

        return isNullAble;
    }


    public void setNullAble(boolean isNullAble) {

        this.isNullAble = isNullAble;
    }

}
