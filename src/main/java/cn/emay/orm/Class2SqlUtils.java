package cn.emay.orm;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.springframework.util.StringUtils;

/**
 * 类转sql工具
 * 
 * @author Frank
 *
 */
public class Class2SqlUtils {

	/**
	 * 将Class转换为save Sql <br/>
	 * 按照驼峰转下划线的方式，自动将数据Class的字段映射到数据库字段
	 * 
	 * @param clazz
	 *            类
	 * @param tableName
	 *            数据库表名，非必填，如果未填，则将类名按照驼峰转下划线的规则转换
	 * @param isIgnore
	 *            是否忽略已经重复的数据
	 * @param autoId
	 *            是否适用数据库的自动生成ID
	 * @return
	 * @throws IntrospectionException
	 */
	public static String class2SaveSql(Class<?> clazz, String tableName, boolean isIgnore, boolean autoId) throws IntrospectionException {
		if(clazz == null) {
			throw new NullPointerException("class is null");
		}
		// 转换
		if (StringUtils.isEmpty(tableName)) {
			tableName = hump2Underline(clazz.getSimpleName());
		}
		List<String> tableColumns = new ArrayList<String>();
		List<String> modelColumns = new ArrayList<String>();
		BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor property : propertyDescriptors) {
			String filedName = property.getName();
			if ("class".equalsIgnoreCase(filedName)) {
				continue;
			}
			if (autoId && "id".equalsIgnoreCase(filedName)) {
				continue;
			}
			modelColumns.add(":" + filedName);
			String colunmName = hump2Underline(filedName);
			tableColumns.add(colunmName);
		}
		// 拼接
		StringBuffer buff = new StringBuffer();
		if (isIgnore) {
			buff.append("insert ignore into ");
		} else {
			buff.append("insert into ");
		}
		buff.append(tableName).append(" ");
		buff.append("(").append(join(tableColumns, ",")).append(")");
		buff.append(" values ");
		buff.append("(").append(join(modelColumns, ",")).append(")");
		return buff.toString();
	}

	/**
	 * 驼峰命名转换为下划线命名
	 * 
	 * @param clazz
	 * @return
	 */
	public static String hump2Underline(String name) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isUpperCase(c) && i > 0) {
				buff.append("_");
			}
			buff.append(c);
		}
		return buff.toString().toLowerCase();
	}

	/**
	 * 按照字符拼接集合
	 * 
	 * @param iterable
	 * @param separator
	 * @return
	 */
	private static String join(final Iterable<?> iterable, final String separator) {
		if (iterable == null) {
			return null;
		}
		return join(iterable.iterator(), separator);
	}

	/**
	 * 按照字符拼接集合
	 * 
	 * @param iterator
	 * @param separator
	 * @return
	 */
	private static String join(final Iterator<?> iterator, final String separator) {
		if (iterator == null) {
			return null;
		}
		if (!iterator.hasNext()) {
			return "";
		}
		final Object first = iterator.next();
		if (!iterator.hasNext()) {
			return Objects.toString(first, "");
		}
		final StringBuilder buf = new StringBuilder(256);
		if (first != null) {
			buf.append(first);
		}
		while (iterator.hasNext()) {
			if (separator != null) {
				buf.append(separator);
			}
			final Object obj = iterator.next();
			if (obj != null) {
				buf.append(obj);
			}
		}
		return buf.toString();
	}

}
