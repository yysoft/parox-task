/**
 * 
 */
package com.daoman.task.persist.product;

import com.daoman.task.domain.product.ProductDefine;

/**
 * @author mays
 *
 */
public interface ProductDefineMapper {

	public Integer insert(ProductDefine define);
	
	public ProductDefine queryOne(Integer id);
	
	public ProductDefine queryOneByPid(Integer pid);
	
	public Integer delete(Integer id);
	
	public Integer deleteByPid(Integer pid);
	
	public Integer update(ProductDefine define);
	
	public Integer updateByPid(ProductDefine define);
	
}
