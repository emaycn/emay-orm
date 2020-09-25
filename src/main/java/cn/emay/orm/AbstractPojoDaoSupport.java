package cn.emay.orm;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.Map.Entry;

/**
 * emay hibernate jdbcTemple 通用dao支持<br/>
 * 请继承此dao使用，需要注意的是，hibernate4在spring中的使用，需要配置事务处理。
 *
 * @param <E>
 * @author Frank
 */
public abstract class AbstractPojoDaoSupport<E extends java.io.Serializable> extends AbstractDaoSupport {

    /**
     * 当前POJO的Class属性
     */
    final public Class<E> entityClass;

    /**
     * 查询当前POJO全表的Hql语句
     */
    final public String FIND_ALL_HQL;

    @SuppressWarnings("unchecked")
    public AbstractPojoDaoSupport() {
        this.entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        FIND_ALL_HQL = " from " + this.entityClass.getSimpleName() + " ";
    }

    /**
     * 增
     *
     * @param pojo 数据对象
     */
    public void save(E pojo) {
        super.save(pojo);
    }

    /**
     * 改
     *
     * @param pojo 数据对象
     */
    public void update(E pojo) {
        super.update(pojo);
    }

    /**
     * 删
     *
     * @param pojo 数据对象
     */
    public void delete(E pojo) {
        super.delete(pojo);
    }

    /**
     * 批量增
     *
     * @param entities 数据对象
     */
    @Deprecated
    public void saveBatch(List<E> entities) {
        super.saveByBatch(entities);
    }

    /**
     * 批量改
     *
     * @param entities 数据对象
     */
    @Deprecated
    public void updateBatch(List<E> entities) {
        super.updateByBatch(entities);
    }

    /**
     * 批量删
     *
     * @param entities 数据对象
     */
    @Deprecated
    public void deleteBatch(List<E> entities) {
        super.deleteByBatch(entities);
    }

    /**
     * 按照主键删除
     *
     * @param ids 删除的主键集合
     */
    public void deleteById(Serializable... ids) {
        if (ids == null) {
            return;
        }
        int size = ids.length;
        String hql = "delete from " + this.entityClass.getSimpleName() + " where id in (:newIds)";
        Map<String, Object> params = new HashMap<>();
        List<Object> newIds = new ArrayList<>(1000);
        for (int i = 0; i < size; i++) {
            newIds.add(ids[i]);
            boolean isSaveOne = (i != 0 && i % 980 == 0) || i == size - 1;
            if (isSaveOne) {
                params.put("newIds", newIds);
                this.execByHql(hql, params);
                params.clear();
                params = new HashMap<>();
                newIds.clear();
                newIds = new ArrayList<>(1000);
            }
        }
    }

    /**
     * 批量删,主键名为ID
     *
     * @param ids 删除的主键集合
     */
    public void deleteBatchByPKids(List<? extends Serializable> ids) {
        deleteById(ids.toArray(new Serializable[0]));
    }

    /**
     * 批量删,主键名为ID
     *
     * @param entities 删除的元素集合
     */
    public void deleteBatchByPKid(List<E> entities) {
        if (entities == null || entities.size() == 0) {
            return;
        }
        int size = entities.size();
        Method method;
        try {
            method = this.entityClass.getMethod("getId");
            method.setAccessible(true);
        } catch (SecurityException | NoSuchMethodException e2) {
            throw new IllegalArgumentException(e2);
        }
        List<Serializable> ids = new ArrayList<>(size);
        for (E e : entities) {
            Object id;
            try {
                id = method.invoke(e);
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e1) {
                throw new IllegalArgumentException(e1);
            }
            ids.add((Serializable) id);
        }
        deleteBatchByPKids(ids);
    }

    /**
     * 清楚hibernate缓存对象
     *
     * @param entities 对象
     */
    @SafeVarargs
    public final void evict(E... entities) {
        evictBatch(Arrays.asList(entities));
    }

    /**
     * 清楚hibernate缓存对象
     *
     * @param entities 对象
     */
    public void evictBatch(List<E> entities) {
        super.evictByBatch(entities);
    }

    /**
     * 根据元素删除POJO
     *
     * @param fieldName 字段名
     * @param value     值
     */
    public void deleteByProperty(String fieldName, Object value) {
        if (fieldName == null || fieldName.trim().equalsIgnoreCase("")) {
            return;
        }
        String hql = "delete " + entityClass.getSimpleName() + " where " + fieldName + " =  :value  ";
        Map<String, Object> param = new HashMap<>();
        param.put("value", value);
        this.execByHql(hql, param);
    }

    /**
     * 根据ID查找POJO
     *
     * @param id id
     * @return 数据对象
     */
    public E findById(Serializable id) {
        return this.getHibernateTemplate().get(entityClass, id);
    }

    /**
     * 查询所有数据
     *
     * @return 数据对象
     */
    @SuppressWarnings("unchecked")
    public List<E> findAll() {
        return (List<E>) this.getListResult(FIND_ALL_HQL);
    }

    /**
     * 根据元素查找唯一POJO
     *
     * @param fieldName 字段名
     * @param value     值
     * @return 数据对象
     */
    public E findByProperty(String fieldName, Object value) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(fieldName, value);
        return this.findByProperties(properties);
    }

    /**
     * 根据元素查找唯一POJO
     *
     * @param properties 字段名值对
     * @return 数据对象
     */
    @SuppressWarnings("unchecked")
    public E findByProperties(Map<String, Object> properties) {
        StringBuffer hql = new StringBuffer(FIND_ALL_HQL);
        Map<String, Object> param = new HashMap<>();
        this.fillHqlAndParamsByProperties(hql, param, properties, false);
        return (E) this.getUniqueResult(hql.toString(), param);
    }

    /**
     * 根据元素查找POJO集合
     *
     * @param fieldName 字段名
     * @param value     值
     * @return 数据对象
     */
    public List<E> findListByProperty(String fieldName, Object value) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(fieldName, value);
        return this.findListByProperties(properties);
    }

    /**
     * 根据元素查找POJO集合
     *
     * @param properties 字段名值对
     * @return 数据对象
     */
    @SuppressWarnings("unchecked")
    public List<E> findListByProperties(Map<String, Object> properties) {
        StringBuffer hql = new StringBuffer(FIND_ALL_HQL);
        Map<String, Object> param = new HashMap<>();
        this.fillHqlAndParamsByProperties(hql, param, properties, false);
        return (List<E>) this.getListResult(hql.toString(), param);
    }

    /**
     * 根据元素查找POJO
     *
     * @param fieldName 字段名
     * @param value     值
     * @return 数据对象
     */
    @SuppressWarnings("unchecked")
    public List<E> findListLikeProperty(String fieldName, Object value) {
        StringBuffer hql = new StringBuffer(FIND_ALL_HQL);
        Map<String, Object> param = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        properties.put(fieldName, value);
        this.fillHqlAndParamsByProperties(hql, param, properties, false);
        return (List<E>) this.getListResult(hql.toString(), param);
    }

    /**
     * 填充数据
     *
     * @param hql        hql语句
     * @param param      参数接收器
     * @param properties 字段名值对
     * @param isLike     是否like
     */
    public void fillHqlAndParamsByProperties(StringBuffer hql, Map<String, Object> param, Map<String, Object> properties, boolean isLike) {
        if (properties == null || properties.size() == 0) {
            return;
        }
        String line = isLike ? "like" : "=";
        String tmp = "tmp";
        int i = 0;
        for (Entry<String, Object> entry : properties.entrySet()) {
            String filedName = entry.getKey();
            Object filedValue = entry.getValue();
            if (i != 0) {
                hql.append(" and ");
            } else {
                hql.append(" where ");
            }
            String key = tmp + i;
            hql.append(filedName).append(" ").append(line).append(" :").append(key);
            param.put(key, filedValue);
            i++;
        }
    }

}
