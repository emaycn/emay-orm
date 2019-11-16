package cn.emay.orm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;

import cn.emay.utils.db.common.Page;

/**
 * emay hibernate jdbctemple 通用dao支持<br/>
 * 请继承此dao使用，需要注意的是，hibernate4在spring中的使用，需要配置事务处理。
 * 
 * @author Frank
 *
 */
public abstract class AbstractDaoSupport extends AbstractSqlDaoSupport{

	/**
	 * 获取HibernateTemplate
	 * 
	 * @return
	 */
	protected abstract HibernateTemplate getHibernateTemplate();

	/**
	 * 获取SessionFactory
	 * 
	 * @return
	 */
	protected abstract SessionFactory getSessionFactory();

	/**
	 * 不带参数的分页查询SQL，limit=0则不分页<br/>
	 * 使用了hibernate的自适应分页
	 * 
	 * @param sql
	 * @param start
	 * @param limit
	 * @return
	 */
	public List<?> getPageListResultBySqlByHibernate(String sql, int start, int limit) {
		return this.getPageListResultBySqlByHibernate(sql, start, limit, null);
	}

	/**
	 * 带参数的分页查询SQL，limit=0则不分页<br/>
	 * 使用了hibernate的自适应分页
	 * 
	 * @param sql
	 * @param start
	 * @param limit
	 * @param params
	 * @return
	 */
	public List<?> getPageListResultBySqlByHibernate(final String sql, final int start, final int limit, final Map<String, Object> params) {
		if (sql == null) {
			return new ArrayList<Object>();
		}
		return this.getHibernateTemplate().execute(new HibernateCallback<List<?>>() {
			@Override
			public List<?> doInHibernate(Session session) throws HibernateException {
				Query<?> query = session.createSQLQuery(sql);
				fillParameters(query, params);
				if (limit != 0) {
					query.setFirstResult(start);
					query.setMaxResults(limit);
				}
				return query.list();
			}
		});
	}

	/*-----------------------------------------[hql]--------------------------------------------*/

	/**
	 * 增
	 * 
	 * @param pojo
	 */
	public void save(Object pojo) {
		if (pojo == null) {
			return;
		}
		this.getHibernateTemplate().save(pojo);
	}

	/**
	 * 改
	 * 
	 * @param pojo
	 */
	public void update(Object pojo) {
		if (pojo == null) {
			return;
		}
		this.getHibernateTemplate().update(pojo);
	}

	/**
	 * 删
	 * 
	 * @param pojo
	 */
	public void delete(Object pojo) {
		if (pojo == null) {
			return;
		}
		this.getHibernateTemplate().delete(pojo);
	}

	/**
	 * 批量增<br/>
	 * 大数据情况下慢，不推荐使用 <br/>
	 * 推荐使用：execBatchSql
	 * 
	 * @param pojos
	 */
	@Deprecated
	public void saveByBatch(List<?> pojos) {
		if (pojos == null || pojos.size() == 0) {
			return;
		}
		for (int i = 0; i < pojos.size(); i++) {
			this.getHibernateTemplate().save(pojos.get(i));
			if (i % 50 == 0) {
				this.getHibernateTemplate().flush();
				this.getHibernateTemplate().clear();
			}
		}
		this.getHibernateTemplate().flush();
		this.getHibernateTemplate().clear();
	}

	/**
	 * 批量改<br/>
	 * 大数据情况下慢，不推荐使用 <br/>
	 * 推荐使用：execBatchSql
	 * 
	 * @param pojos
	 */
	@Deprecated
	public void updateByBatch(List<?> pojos) {
		if (pojos == null || pojos.size() == 0) {
			return;
		}
		for (int i = 0; i < pojos.size(); i++) {
			this.getHibernateTemplate().update(pojos.get(i));
			if (i % 50 == 0) {
				this.getHibernateTemplate().flush();
				this.getHibernateTemplate().clear();
			}
		}
		this.getHibernateTemplate().flush();
		this.getHibernateTemplate().clear();
	}

	/**
	 * 批量删<br/>
	 * 大数据情况下慢，不推荐使用 <br/>
	 * 推荐使用：execBatchSql
	 * 
	 * @param pojos
	 */
	@Deprecated
	public void deleteByBatch(List<?> pojos) {
		if (pojos == null || pojos.size() == 0) {
			return;
		}
		for (int i = 0; i < pojos.size(); i++) {
			this.getHibernateTemplate().delete(pojos.get(i));
			if (i % 50 == 0) {
				this.getHibernateTemplate().flush();
				this.getHibernateTemplate().clear();
			}
		}
		this.getHibernateTemplate().flush();
		this.getHibernateTemplate().clear();
	}

	/**
	 * 批量清除对象<br/>
	 * 
	 * @param pojos
	 */
	public void evictByBatch(List<?> pojos) {
		if (pojos == null || pojos.size() == 0) {
			return;
		}
		for (int i = 0; i < pojos.size(); i++) {
			this.getHibernateTemplate().evict(pojos.get(i));
			if (i % 50 == 0) {
				this.getHibernateTemplate().flush();
				this.getHibernateTemplate().clear();
			}
		}
		this.getHibernateTemplate().flush();
		this.getHibernateTemplate().clear();
	}

	/**
	 * 执行hql,无参数
	 * 
	 * @param hql
	 */
	public Integer execByHql(String hql) {
		return this.execByHql(hql, null);
	}

	/**
	 * 执行hql，有参数
	 * 
	 * @param hql
	 * @param params
	 */
	public Integer execByHql(final String hql, final Map<String, Object> params) {
		if (hql == null) {
			return 0;
		}
		return this.getHibernateTemplate().execute(new HibernateCallback<Integer>() {
			@Override
			public Integer doInHibernate(Session session) throws HibernateException {
				Query<?> query = session.createQuery(hql);
				if (params != null && params.size() > 0) {
					fillParameters(query, params);
				}
				return query.executeUpdate();
			}
		});
	}

	/**
	 * 根据ID查找
	 * 
	 * @param entityClass
	 * @param id
	 * @return
	 */
	public Object getById(Class<?> entityClass, Serializable id) {
		return this.getHibernateTemplate().get(entityClass, id);
	}

	/**
	 * 查询单一结果,无参
	 * 
	 * @param hql
	 * @return
	 */
	public Object getUniqueResult(String hql) {
		return this.getUniqueResult(Object.class, hql, null);
	}

	/**
	 * 查询单一结果,无参，转换为指定的Class
	 * 
	 * @param clazz
	 * @param hql
	 * @return
	 */
	public <T> T getUniqueResult(Class<T> clazz, String hql) {
		return this.getUniqueResult(clazz, hql, null);
	}

	/**
	 * 查询单一结果,有参
	 * 
	 * @param hql
	 * @param params
	 * @return
	 */
	public Object getUniqueResult(String hql, Map<String, Object> params) {
		return this.getUniqueResult(Object.class, hql, params);
	}

	/**
	 * 查询单一结果,有参，转换为指定的Class
	 * 
	 * @param clazz
	 * @param hql
	 * @param params
	 * @return
	 */
	public <T> T getUniqueResult(Class<T> clazz, final String hql, final Map<String, Object> params) {
		if (hql == null) {
			return null;
		}
		if (clazz == null) {
			throw new NullPointerException("clazz is null");
		}
		return this.getHibernateTemplate().execute(new HibernateCallback<T>() {
			@Override
			public T doInHibernate(Session session) throws HibernateException {
				Query<T> query = session.createQuery(hql, clazz);
				if (params != null && params.size() > 0) {
					fillParameters(query, params);
				}
				return (T) query.setMaxResults(1).uniqueResult();
			}
		});
	}

	/**
	 * 查询，无参<br/>
	 * 
	 * @param hql
	 * @return
	 */
	public List<?> getListResult(String hql) {
		return this.getPageListResult(Object.class, hql, 0, 0, null);
	}

	/**
	 * 查询，无参，转换元素为执行Class<br/>
	 * 
	 * @param clazz
	 * @param hql
	 * @return
	 */
	public <T> List<T> getListResult(Class<T> clazz, String hql) {
		return this.getPageListResult(clazz, hql, 0, 0, null);
	}

	/**
	 * 查询，有参<br/>
	 * 
	 * @param hql
	 * @param params
	 * @return
	 */
	public List<?> getListResult(String hql, Map<String, Object> params) {
		return this.getPageListResult(Object.class, hql, 0, 0, params);
	}

	/**
	 * 查询，有参，转换元素为执行Class<br/>
	 * 
	 * @param clazz
	 * @param hql
	 * @param params
	 * @return
	 */
	public <T> List<T> getListResult(Class<T> clazz, String hql, Map<String, Object> params) {
		return this.getPageListResult(clazz, hql, 0, 0, params);
	}

	/**
	 * 分页查询，无参<br/>
	 * limit=0不分页
	 * 
	 * @param hql
	 * @param start
	 * @param limit
	 * @return
	 */
	public List<?> getPageListResult(String hql, int start, int limit) {
		return this.getPageListResult(Object.class, hql, start, limit, null);
	}

	/**
	 * 分页查询，无参，转换元素为执行Class<br/>
	 * limit=0不分页
	 * 
	 * @param clazz
	 * @param hql
	 * @param start
	 * @param limit
	 * @return
	 */
	public <T> List<T> getPageListResult(Class<T> clazz, String hql, int start, int limit) {
		return this.getPageListResult(clazz, hql, start, limit, null);
	}

	/**
	 * 分页查询，有参<br/>
	 * limit=0不分页
	 * 
	 * @param hql
	 * @param start
	 * @param limit
	 * @param params
	 * @return
	 */
	public List<?> getPageListResult(final String hql, final int start, final int limit, final Map<String, Object> params) {
		return this.getPageListResult(Object.class, hql, start, limit, params);
	}

	/**
	 * 分页查询，有参,将元素转换为指定的Class<br/>
	 * limit=0不分页
	 * 
	 * @param clazz
	 * @param hql
	 * @param start
	 * @param limit
	 * @param params
	 * @return
	 */
	public <T> List<T> getPageListResult(Class<T> clazz, final String hql, final int start, final int limit, final Map<String, Object> params) {
		if (hql == null) {
			return new ArrayList<T>();
		}
		if (clazz == null) {
			throw new NullPointerException("clazz is null");
		}
		return this.getHibernateTemplate().execute(new HibernateCallback<List<T>>() {
			@Override
			public List<T> doInHibernate(Session session) throws HibernateException {
				Query<T> query = session.createQuery(hql, clazz);
				if (params != null && params.size() > 0) {
					fillParameters(query, params);
				}
				if (limit != 0) {
					query.setFirstResult(start);
					query.setMaxResults(limit);
				}
				return query.list();
			}
		});
	}

	/**
	 * 分页查询<br/>
	 * 注意：如果是带有group的hql，本方法不支持<br/>
	 * 
	 * @param hql
	 * @param start
	 * @param limit
	 * @param param
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public <T> Page<T> getPageResult(String hql, int start, int limit, Map<String, Object> params, Class<T> clazz) {
		if (clazz == null) {
			throw new NullPointerException("clazz is null");
		}
		Map<String, Object> map = this.getPageResultMap(hql, start, limit, params,clazz);
		Page<T> page = new Page<T>();
		page.setCurrentPageNum(Integer.valueOf(String.valueOf(map.get(Page.CURRENT_PAGE))));
		page.setStart(Integer.valueOf(String.valueOf(map.get(Page.START))));
		page.setLimit(Integer.valueOf(String.valueOf(map.get(Page.LIMIT))));
		page.setTotalCount(Integer.valueOf(String.valueOf(map.get(Page.TOTAL_COUNT))));
		page.setTotalPage(Integer.valueOf(String.valueOf(map.get(Page.TOTAL_PAGE))));
		page.setList((List<T>) map.get(Page.DATA_LIST));
		return page;
	}

	/**
	 * 分页查询<br/>
	 * 注意：如果是带有group的hql，本方法不支持<br/>
	 * 此方法返回MAP:<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;list=数据列表<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;totalCount=数据总数<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;start=从第几条开始<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;imit=每页多少条<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;currentPage=当前页数<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;totalPage=供多少页<br/>
	 * 
	 * @param hql
	 * @param start
	 * @param limit
	 * @param param
	 * @return
	 */
	public Map<String, Object> getPageResultMap(String hql, int start, int limit, Map<String, Object> params) {
		return getPageResultMap(hql, start, limit, params, Object.class);
	}
	
	/**
	 * 分页查询<br/>
	 * 注意：如果是带有group的hql，本方法不支持<br/>
	 * 此方法返回MAP:<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;list=数据列表<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;totalCount=数据总数<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;start=从第几条开始<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;imit=每页多少条<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;currentPage=当前页数<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;totalPage=供多少页<br/>
	 * 
	 * @param hql
	 * @param start
	 * @param limit
	 * @param param
	 * @return
	 */
	public<T> Map<String, Object> getPageResultMap(String hql, int start, int limit, Map<String, Object> params, Class<T> clazz) {
		if (hql == null) {
			return new HashMap<String, Object>();
		}
		Map<String, Object> result = new HashMap<String, Object>();
		List<T> list = this.getPageListResult(clazz,hql, start, limit, params);
		this.fillPageInfo(result, start, limit, hql, params);
		result.put(Page.DATA_LIST, list);
		return result;
	}
	
	/**
	 * 填充参数
	 * 
	 * @param query
	 * @param param
	 */
	@SuppressWarnings("rawtypes")
	public void fillParameters(Query query, Map<String, Object> params) {
		if (params != null && query != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				if (entry.getValue() instanceof Collection) {
					query.setParameterList(entry.getKey(), (Collection) entry.getValue());
				} else if (entry.getValue() instanceof Object[]) {
					query.setParameterList(entry.getKey(), (Object[]) entry.getValue());
				} else {
					query.setParameter(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	/**
	 * 填充分页信息<br/>
	 * 会有一次查询，不支持带group的语句
	 * 
	 * @param result
	 * @param start
	 * @param limit
	 * @param selectHql
	 * @param param
	 */
	public void fillPageInfo(Map<String, Object> result, int start, int limit, String selectHql, Map<String, Object> params) {
		if (limit <= 0) {
			limit = 20;
		}
		// 寻找from
		int fromindex = selectHql.toLowerCase().indexOf(" from ");
		if (fromindex < 0) {
			// 适应老的规则，所有表名、字段名不能有from
			fromindex = selectHql.toLowerCase().indexOf("from ");
			if (fromindex < 0) {
				throw new RuntimeException("hql" + " has no from");
			}
		}
		// 判断是否能截取最后的order
		boolean isHasOrder = false;
		int orderbyindex = selectHql.toLowerCase().indexOf(" order ");
		if (orderbyindex > 0) {
			isHasOrder = !selectHql.toLowerCase().substring(orderbyindex).contains(")");
		}
		// 截取from
		String countHql = "select count(*) " + selectHql.substring(fromindex);
		// 截取order
		if (isHasOrder) {
			orderbyindex = countHql.toLowerCase().indexOf(" order ");
			countHql = countHql.substring(0, orderbyindex);
		}
		// System.out.println("DaoSupport.class selct count hql is : " + countHql);
		// 查询
		long totalCount = (Long) this.getUniqueResult(countHql, params);
		// 赋值
		int total = (int) totalCount;
		// 简单的分页逻辑
		int currentPage = start / limit + 1;
		int totalPage = total / limit;
		if (totalCount % limit != 0) {
			totalPage++;
		}
		result.put(Page.TOTAL_COUNT, totalCount);
		result.put(Page.START, start);
		result.put(Page.LIMIT, limit);
		result.put(Page.CURRENT_PAGE, currentPage);
		result.put(Page.TOTAL_PAGE, totalPage);
	}

}
