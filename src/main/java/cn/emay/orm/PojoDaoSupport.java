package cn.emay.orm;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * emay hibernate jdbctemple 通用dao支持<br/>
 * 请继承此dao使用，需要注意的是，hibernate4在spring中的使用，需要配置事务处理。
 * 
 * @author Frank
 *
 * @param <E>
 */
public abstract class PojoDaoSupport<E extends java.io.Serializable> extends DaoSupport {

	/**
	 * 当前POJO的Class属性
	 */
	final public Class<E> entityClass;

	/**
	 * 查询当前POJO全表的Hql语句
	 */
	final public String FIND_ALL_HQL;

	@SuppressWarnings("unchecked")
	public PojoDaoSupport() {
		this.entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		FIND_ALL_HQL = new StringBuffer().append(" from ").append(this.entityClass.getSimpleName()).append(" ").toString();
	}

	/**
	 * 增
	 * 
	 * @param pojo
	 */
	public void save(E pojo) {
		super.save(pojo);
	}

	/**
	 * 改
	 * 
	 * @param pojo
	 */
	public void update(E pojo) {
		super.update(pojo);
	}

	/**
	 * 删
	 * 
	 * @param pojo
	 */
	public void delete(E pojo) {
		super.delete(pojo);
	}

	/**
	 * 批量增
	 */
	@Deprecated
	public void saveBatch(List<E> entities) {
		super.saveByBatch(entities);
	}

	/**
	 * 批量改
	 */
	@Deprecated
	public void updateBatch(List<E> entities) {
		super.updateByBatch(entities);
	}

	/**
	 * 批量删
	 */
	@Deprecated
	public void deleteBatch(List<E> entities) {
		super.deleteByBatch(entities);
	}

	/**
	 * 批量删,主键名为ID
	 * 
	 * @param entities
	 *            删除的元素集合
	 */
	public void deleteBatchByPKids(List<? extends Serializable> ids) {
		int size = ids.size();
		String hql = "delete from " + this.entityClass.getSimpleName() + " where id in (:newids)";
		Map<String, Object> params = new HashMap<String, Object>();
		List<Object> newids = new ArrayList<Object>(1000);
		for (int i = 0; i < size; i++) {
			newids.add(ids.get(i));
			if ((i != 0 && i % 980 == 0) || i == size - 1) {
				params.put("newids", newids);
				this.execByHql(hql, params);
				params.clear();
				params = new HashMap<String, Object>();
				newids.clear();
				newids = new ArrayList<Object>(1000);
			}
		}
	}

	/**
	 * 批量删,主键名为ID
	 * 
	 * @param entities
	 *            删除的元素集合
	 */
	public void deleteBatchByPKid(List<E> entities) {
		if (entities == null || entities.size() == 0) {
			return;
		}
		int size = entities.size();
		Method method = null;
		try {
			method = this.entityClass.getMethod("getId");
			method.setAccessible(true);
		} catch (SecurityException e2) {
			throw new IllegalArgumentException(e2);
		} catch (NoSuchMethodException e2) {
			throw new IllegalArgumentException(e2);
		}
		List<Serializable> ids = new ArrayList<Serializable>(size);
		for (E e : entities) {
			Object id = null;
			try {
				id = method.invoke(e);
			} catch (IllegalArgumentException e1) {
				throw new IllegalArgumentException(e1);
			} catch (IllegalAccessException e1) {
				throw new IllegalArgumentException(e1);
			} catch (InvocationTargetException e1) {
				throw new IllegalArgumentException(e1);
			}
			ids.add((Serializable) id);
		}
		deleteBatchByPKids(ids);
	}

	/**
	 * 批量清除对象
	 */
	public void evictBatch(List<E> entities) {
		super.evictByBatch(entities);
	}

	/**
	 * 根据元素删除POJO
	 * 
	 * @param fieldName
	 * @param value
	 */
	public void deleteByProperty(String fieldName, Object value) {
		if (fieldName == null || fieldName.trim().equalsIgnoreCase("")) {
			return;
		}
		String hql = "delete " + entityClass.getSimpleName() + " where " + fieldName + " =  :value  ";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("value", value);
		this.execByHql(hql, param);
	}

	/**
	 * 根据ID查找POJO
	 * 
	 * @param id
	 * @return
	 */
	public E findById(Serializable id) {
		return this.getHibernateTemplate().get(entityClass, id);
	}

	/**
	 * 查询所有数据
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<E> findAll() {
		return (List<E>) this.getListResult(FIND_ALL_HQL);
	}

	/**
	 * 根据元素查找唯一POJO
	 * 
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public E findByProperty(String fieldName, Object value) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(fieldName, value);
		return this.findByProperties(properties);
	}

	/**
	 * 根据元素查找唯一POJO
	 * 
	 * @param properties
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public E findByProperties(Map<String, Object> properties) {
		StringBuffer hql = new StringBuffer(FIND_ALL_HQL);
		Map<String, Object> param = new HashMap<String, Object>();
		this.fillHqlAndParamsByProperties(hql, param, properties, false);
		return (E) this.getUniqueResult(hql.toString(), param);
	}

	/**
	 * 根据元素查找POJO集合
	 * 
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public List<E> findListByProperty(String fieldName, Object value) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(fieldName, value);
		return this.findListByProperties(properties);
	}

	/**
	 * 根据元素查找POJO集合
	 * 
	 * @param properties
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<E> findListByProperties(Map<String, Object> properties) {
		StringBuffer hql = new StringBuffer(FIND_ALL_HQL);
		Map<String, Object> param = new HashMap<String, Object>();
		this.fillHqlAndParamsByProperties(hql, param, properties, false);
		return (List<E>) this.getListResult(hql.toString(), param);
	}

	/**
	 * 根据元素查找POJO
	 * 
	 * @param fieldName
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<E> findListLikeProperty(String fieldName, Object value) {
		StringBuffer hql = new StringBuffer(FIND_ALL_HQL);
		Map<String, Object> param = new HashMap<String, Object>();
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(fieldName, value);
		this.fillHqlAndParamsByProperties(hql, param, properties, false);
		return (List<E>) this.getListResult(hql.toString(), param);
	}

	/**
	 * 填充数据
	 * 
	 * @param hql
	 * @param param
	 * @param properties
	 * @param isLike
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
			hql.append(filedName).append(" " + line + " :").append(key);
			param.put(key, filedValue);
			i++;
		}
	}

}
