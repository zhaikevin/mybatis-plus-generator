package main.java.org.intellij.plugins.mybatis.convert;

/**
 * @Description:
 * @Author: zhaijizhong
 * @Date: 2023/3/22 11:08
 */
public class DefaultJdbcTypeConvert implements IJdbcTypeConvert{

    @Override
    public String processTypeConvert(String fieldType) {
        return fieldType;
    }
}
