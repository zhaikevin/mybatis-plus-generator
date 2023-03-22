package main.java.org.intellij.plugins.mybatis.convert;

/**
 * @Description:
 * @Author: zhaijizhong
 * @Date: 2023/3/22 11:07
 */
public class MysqlJdbcTypeConvert implements IJdbcTypeConvert {

    @Override
    public String processTypeConvert(String fieldType) {
        if (fieldType.equalsIgnoreCase("datetime")) {
            return "TIMESTAMP";
        }
        return fieldType;
    }
}
