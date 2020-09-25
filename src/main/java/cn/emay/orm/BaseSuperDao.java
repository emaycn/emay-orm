package cn.emay.orm;

import java.io.Serializable;
import java.util.List;

/**
 * @param <E>
 * @author Frank
 */
public interface BaseSuperDao<E extends Serializable> {

    /**
     * 增
     *
     * @param entity 实体
     */
    void save(E entity);

    /**
     * 改
     *
     * @param entity 实体
     */
    void update(E entity);

    /**
     * 删
     *
     * @param entity 实体
     */
    void delete(E entity);

    /**
     * 按照主键删除
     *
     * @param ids 删除的主键集合
     */
    void deleteById(Serializable... ids);

    /**
     * 根据ID查
     *
     * @param id id
     * @return 数据
     */
    E findById(Serializable id);

    /**
     * 查询所有
     *
     * @return 所有数据
     */
    List<E> findAll();

    /**
     * 批量增
     *
     * @param entities 对象
     */
    void saveBatch(List<E> entities);

    /**
     * 批量改
     *
     * @param entities 对象
     */
    void updateBatch(List<E> entities);

    /**
     * 批量删
     *
     * @param entities 对象
     */
    void deleteBatch(List<E> entities);

}
