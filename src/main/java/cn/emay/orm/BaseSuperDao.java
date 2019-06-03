package cn.emay.orm;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Frank
 *
 * @param <E>
 */
public interface BaseSuperDao<E extends Serializable> {

	/**
	 * 增
	 * 
	 * @param entity
	 * 
	 */
	public void save(E entity);

	/**
	 * 改
	 * 
	 * @param entity
	 */
	public void update(E entity);

	/**
	 * 删
	 * 
	 * @param entity
	 */
	public void delete(E entity);

	/**
	 * 按照主键删除
	 * 
	 * @param ids
	 *            删除的主键集合
	 */
	public void deleteById(Serializable... ids);

	/**
	 * 根据ID查
	 * 
	 * @param id
	 * @return 
	 */
	public E findById(Serializable id);

	/**
	 * 查询所有
	 * @return 
	 */
	public List<E> findAll();

	/**
	 * 批量增
	 * @param entities
	 */
	public void saveBatch(List<E> entities);

	/**
	 * 批量改
	 * @param entities
	 */
	public void updateBatch(List<E> entities);

	/**
	 * 批量删
	 * @param entities
	 */
	public void deleteBatch(List<E> entities);

}
