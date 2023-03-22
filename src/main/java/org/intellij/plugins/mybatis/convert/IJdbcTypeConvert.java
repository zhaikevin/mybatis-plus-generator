package main.java.org.intellij.plugins.mybatis.convert;

/**
 * @Description:
 * @Author: zhaijizhong
 * @Date: 2023/3/22 10:59
 */
public interface IJdbcTypeConvert {

    /**
     * 执行类型转换
     * @param fieldType 字段类型
     * @return
     */
    String processTypeConvert(String fieldType);
}
