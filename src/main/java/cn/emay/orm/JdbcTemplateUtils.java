package cn.emay.orm;

import java.beans.IntrospectionException;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import cn.emay.utils.db.common.Page;

/**
 * JdbcTemplate工具类
 * 
 * @author Frank
 *
 */
public class JdbcTemplateUtils {

	/**
	 * 存储数据<br/>
	 * 按照驼峰转下划线的方式，自动将数据Class的字段映射到数据库字段
	 * 
	 * @param jdbcTemplate
	 * @param tableName
	 *            数据库表名，非必填，如果未填，则将类名按照驼峰转下划线的规则转换
	 * @param dataList
	 *            数据
	 * @param isIgnore
	 *            是否忽略已经重复的数据
	 * @param autoId
	 *            是否适用数据库的自动生成ID
	 * @return
	 */
	public static <T> int[] saveByAutoNamed(JdbcTemplate jdbcTemplate, String tableName, List<T> dataList, boolean isIgnore, boolean autoId) {
		if (dataList == null || dataList.size() == 0) {
			throw new IllegalArgumentException("dataList is empty");
		}
		T data = dataList.get(0);
		if (null == data) {
			throw new IllegalArgumentException("first data is null");
		}
		String sql;
		try {
			sql = Class2SqlUtils.class2SaveSql(data.getClass(), tableName, isIgnore, autoId);
		} catch (IntrospectionException e) {
			throw new IllegalArgumentException(e);
		}
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(dataList.toArray());
		return namedParameterJdbcTemplate.batchUpdate(sql, params);
	}

	/**
	 * 查询唯一值,并转换为对象T<br/>
	 * 按照驼峰转下划线的方式，自动将数据库字段映射到Class的字段
	 * 
	 * @param jdbcTemplate
	 * @param clazz
	 *            对象Class
	 * @param sql
	 *            SQL
	 * @param parameters
	 *            参数
	 * @return
	 */
	public static <T> T findObjectUnique(JdbcTemplate jdbcTemplate, Class<T> objectClass, String sql, Object... parameters) {
		return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<T>(objectClass), parameters);
	}

	/**
	 * 查询列表,并转换为对象T<br/>
	 * 按照驼峰转下划线的方式，自动将数据库字段映射到Class的字段
	 * 
	 * @param jdbcTemplate
	 * @param clazz
	 *            对象Class
	 * @param sql
	 *            SQL
	 * @param parameters
	 *            参数
	 * @return
	 */
	public static <T> List<T> findObjectListByClass(JdbcTemplate jdbcTemplate, Class<T> clazz, String sql, Object... parameters) {
		return findObjectListByMapper(jdbcTemplate, new BeanPropertyRowMapper<T>(clazz), sql, parameters);
	}

	/**
	 * 查询列表,并以Mapper转换为对象T<br/>
	 * 
	 * @param jdbcTemplate
	 * @param rowMapper
	 *            查询值与对象的映射
	 * @param sql
	 *            SQL
	 * @param parameters
	 *            参数
	 * @return
	 */
	public static <T> List<T> findObjectListByMapper(JdbcTemplate jdbcTemplate, RowMapper<T> rowMapper, String sql, Object... parameters) {
		return jdbcTemplate.query(sql, parameters, rowMapper);
	}

	/**
	 * 查询分页,并转换为对象T<br/>
	 * 按照驼峰转下划线的方式，自动将数据库字段映射到Class的字段
	 * 
	 * @param jdbcTemplate
	 * @param clazz
	 * @param sql
	 * @param start
	 * @param limit
	 * @param parameters
	 * @return
	 */
	public static <T> Page<T> findObjectPageByClassInMysql(JdbcTemplate jdbcTemplate, Class<T> clazz, String sql, int start, int limit, Object... parameters) {
		return findObjectPageByMapperInMysql(jdbcTemplate, new BeanPropertyRowMapper<T>(clazz), sql, start, limit, parameters);
	}

	/**
	 * 查询分页,并以Mapper转换为对象T<br/>
	 * 
	 * @param jdbcTemplate
	 * @param rowMapper
	 * @param sql
	 * @param start
	 * @param limit
	 * @param parameters
	 * @return
	 */
	public static <T> Page<T> findObjectPageByMapperInMysql(JdbcTemplate jdbcTemplate, RowMapper<T> rowMapper, String sql, int start, int limit, Object... parameters) {
		Integer totalCount = findObjectPageCountInMysql(jdbcTemplate, sql, parameters);
		StringBuffer querySql = new StringBuffer(sql);
		querySql.append(" LIMIT " + start + "," + limit + " ");
		List<T> list = findObjectListByMapper(jdbcTemplate, rowMapper, querySql.toString(), parameters);
		Page<T> page = new Page<T>();
		page.setList(list);
		page.setNumByStartAndLimit(start, limit, totalCount);
		return page;
	}

	/**
	 * 查询总数
	 * 
	 * @param jdbcTemplate
	 * @param sql
	 * @param parameters
	 * @return
	 */
	private static Integer findObjectPageCountInMysql(JdbcTemplate jdbcTemplate, String sql, Object... parameters) {
		int fromindex = sql.toLowerCase().indexOf(" from ");
		if (fromindex < 0) {
			throw new RuntimeException("sql" + " has no from");
		}
		boolean isHasOrder = false;
		int orderbyindex = sql.toLowerCase().indexOf(" order ");
		if (orderbyindex > 0) {
			isHasOrder = !sql.toLowerCase().substring(orderbyindex).contains(")");
		}
		String countsql = sql;
		if (isHasOrder) {
			orderbyindex = countsql.toLowerCase().indexOf(" order ");
			countsql = countsql.substring(0, orderbyindex);
		}
		countsql = "select count(*) " + countsql.substring(fromindex);
		return jdbcTemplate.queryForObject(countsql, parameters, Integer.class);
	}

}
