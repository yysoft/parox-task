/**
 * 
 */
package com.daoman.task.service.product;

import com.daoman.task.domain.Pager;
import com.daoman.task.domain.SessionUser;
import com.daoman.task.domain.product.Product;
import com.daoman.task.domain.product.ProductCond;
import com.daoman.task.domain.product.ProductDefine;
import com.daoman.task.domain.product.ProductFull;

/**
 * @author mays
 *
 */
public interface ProductService {

	public Pager<Product> pager(Pager<Product> pager, ProductCond cond);
	
	public ProductFull saveFull(ProductFull productFull);
	
	public Product update(Product product);
	
	public Integer remove(Integer id);
	
	public Product queryOne(Integer id, SessionUser user);
	
	public ProductFull queryOneFull(Integer id, Boolean readDefine, SessionUser user);
	
	public ProductFull updateFull(ProductFull productFull);
	
	public ProductFull initFull(SessionUser user, Product product, ProductDefine define);
}
