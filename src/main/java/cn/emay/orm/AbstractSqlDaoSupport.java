package cn.emay.orm;

import cn.emay.utils.db.common.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * emay jdbcTemple 通用dao支持<br/>
 * 请继承此dao使用。
 *
 * @author Frank
 */
public abstract class AbstractSqlDaoSupport {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * 获取JdbcTemplate
     *
     * @return JdbcTemplate
     */
    protected abstract JdbcTemplate getJdbcTemplate();

    /**
     * 获取名字映射JdbcTemplate
     *
     * @return NamedParameterJdbcTemplate
     */
    protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        if (namedParameterJdbcTemplate == null) {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getJdbcTemplate());
        }
        return namedParameterJdbcTemplate;
    }

    /*-----------------------------------------[sql]--------------------------------------------*/

    /**
     * 批量执行插入/更新/删除SQL语句
     *
     * @param sql sql语句
     */
    public void execBatchSql(String... sql) {
        if (sql == null || sql.length == 0) {
            return;
        }
        this.getJdbcTemplate().batchUpdate(sql);
    }

    /**
     * 执行插入/更新/删除SQL语句
     *
     * @param sql sql语句
     */
    public void execSql(String sql) {
        if (sql == null) {
            return;
        }
        this.getJdbcTemplate().execute(sql);
    }

    /**
     * 查询单一结果
     *
     * @param sql    sql语句
     * @param params 参数
     * @return 数据
     */
    public Object getUniqueResultBySql(String sql, Object... params) {
        if (sql == null) {
            return null;
        }
        if (params == null || params.length == 0) {
            return this.getJdbcTemplate().queryForObject(sql, Object.class);
        } else {
            return this.getJdbcTemplate().queryForObject(sql, params, Object.class);
        }
    }

    /**
     * 查询SQL
     *
     * @param sql    sql语句
     * @param params 参数
     * @return 数据
     */
    public List<?> getListResultBySql(String sql, Object... params) {
        if (sql == null) {
            return new ArrayList<>();
        }
        if (params == null || params.length == 0) {
            return this.getJdbcTemplate().queryForList(sql, Object.class);
        } else {
            return this.getJdbcTemplate().queryForList(sql, params, Object.class);
        }
    }

    /*-----------------------sql by jdbcTemple---------------------------------------*/

    /**
     * 存储数据<br/>
     * 按照驼峰转下划线的方式，自动将数据Class的字段映射到数据库字段
     *
     * @param tableName 数据库表名，非必填，如果未填，则将类名按照驼峰转下划线的规则转换
     * @param dataList  数据
     * @param isIgnore  是否忽略已经重复的数据
     * @param autoId    是否适用数据库的自动生成ID
     * @return 存储成功的条数
     */
    public <T> int[] saveByAutoNamed(String tableName, List<T> dataList, boolean isIgnore, boolean autoId) {
        return JdbcTemplateUtils.saveByAutoNamed(getJdbcTemplate(), tableName, dataList, isIgnore, autoId);
    }

    /**
     * 查询唯一值,并转换为对象T<br/>
     * 按照驼峰转下划线的方式，自动将数据库字段映射到Class的字段
     *
     * @param objectClass 对象Class
     * @param sql         SQL
     * @param parameters  参数
     * @return 数据
     */
    public <T> T findObjectUnique(Class<T> objectClass, String sql, Object... parameters) {
        return JdbcTemplateUtils.findObjectUnique(getJdbcTemplate(), objectClass, sql, parameters);
    }

    /**
     * 查询列表,并转换为对象T<br/>
     * 按照驼峰转下划线的方式，自动将数据库字段映射到Class的字段
     *
     * @param clazz      对象Class
     * @param sql        SQL
     * @param parameters 参数
     * @return 数据
     */
    public <T> List<T> findObjectListByClass(Class<T> clazz, String sql, Object... parameters) {
        return JdbcTemplateUtils.findObjectListByClass(getJdbcTemplate(), clazz, sql, parameters);
    }

    /**
     * 查询列表,并以Mapper转换为对象T<br/>
     *
     * @param rowMapper  查询值与对象的映射
     * @param sql        SQL
     * @param parameters 参数
     * @return 数据
     */
    public <T> List<T> findObjectListByMapper(RowMapper<T> rowMapper, String sql, Object... parameters) {
        return JdbcTemplateUtils.findObjectListByMapper(getJdbcTemplate(), rowMapper, sql, parameters);
    }

    /**
     * 查询分页,并转换为对象T<br/>
     * 按照驼峰转下划线的方式，自动将数据库字段映射到Class的字段
     *
     * @param clazz      类型
     * @param sql        sql
     * @param start      起始index
     * @param limit      查询数量
     * @param parameters 参数
     * @return 分页数据
     */
    public <T> Page<T> findObjectPageByClassInMysql(Class<T> clazz, String sql, int start, int limit, Object... parameters) {
        return JdbcTemplateUtils.findObjectPageByClassInMysql(getJdbcTemplate(), clazz, sql, start, limit, parameters);
    }

    /**
     * 查询分页,并以Mapper转换为对象T<br/>
     *
     * @param rowMapper  数据转换器
     * @param sql        sql
     * @param start      起始index
     * @param limit      查询数量
     * @param parameters 参数
     * @return 分页数据
     */
    public <T> Page<T> findObjectPageByMapperInMysql(RowMapper<T> rowMapper, String sql, int start, int limit, Object... parameters) {
        return JdbcTemplateUtils.findObjectPageByMapperInMysql(getJdbcTemplate(), rowMapper, sql, start, limit, parameters);
    }

}
