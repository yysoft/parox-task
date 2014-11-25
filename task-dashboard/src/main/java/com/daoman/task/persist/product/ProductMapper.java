/**
 * 
 */
package com.daoman.task.persist.product;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.daoman.task.domain.Pager;
import com.daoman.task.domain.product.Product;
import com.daoman.task.domain.product.ProductCond;

/**
 * @author mays
 *
 */
public interface ProductMapper {

	public Integer insert(Product product);
	
	public List<Product> pagerDefault(@Param("pager") Pager<Product> pager, @Param("cond") ProductCond cond);
	
	public Integer pagerDefaultCount(@Param("cond") ProductCond cond);
	
	public Integer delete(Integer id);
	
	public Integer update(Product product);
	
	public Product queryOne(Integer id);
	
}
