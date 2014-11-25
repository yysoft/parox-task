/**
 * 
 */
package com.daoman.task.service.product.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.daoman.task.domain.Pager;
import com.daoman.task.domain.SessionUser;
import com.daoman.task.domain.product.Product;
import com.daoman.task.domain.product.ProductCond;
import com.daoman.task.domain.product.ProductDefine;
import com.daoman.task.domain.product.ProductFull;
import com.daoman.task.domain.product.ProductPrice;
import com.daoman.task.persist.product.ProductDefineMapper;
import com.daoman.task.persist.product.ProductMapper;
import com.daoman.task.persist.product.ProductPriceMapper;
import com.daoman.task.service.product.ProductService;

/**
 * @author mays
 *
 */
@Component("productService")
public class ProductServiceImpl implements ProductService {
	
	@Resource
	private ProductMapper productMapper;
	@Resource
	private ProductPriceMapper productPriceMapper;
	@Resource
	private ProductDefineMapper productDefineMapper;

	@Override
	public Pager<Product> pager(Pager<Product> pager, ProductCond cond) {
		
		pager.setRecords(productMapper.pagerDefault(pager, cond));
		pager.setTotals(productMapper.pagerDefaultCount(cond));
		
		return pager;
	}

	@Override
	public Product update(Product product) {
		
		Integer impact = productMapper.update(product);
		if(impact!=null && impact.intValue()>0){
			return product;
		}
		
		return null;
	}

	@Override
	public Integer remove(Integer id) {
		
		Integer impact = productMapper.delete(id);
		if(impact==null||impact.intValue()<=0){
			return null;
		}
		
		productDefineMapper.deleteByPid(id);
		productPriceMapper.deleteByPid(id);
		
		return impact;
	}

	@Override
	public Product queryOne(Integer id, SessionUser user) {
		
		if(!available(id, user)){
			return null;
		}
		
		return productMapper.queryOne(id);
	}

	@Override
	public ProductFull saveFull(ProductFull productFull) {
		
		if(productFull==null || productFull.getProduct()==null ){
			return null;
		}
		
		//初始化
		Product product = productFull.getProduct();
		
		product.setStatusLife(Product.LIFE_SALING);
		
//		Integer impact = productMapper.insert(product);
//		if(!AssertHelper.positiveInt(impact) || !AssertHelper.positiveInt(product.getId())){
//			return null;
//		}
		
		productFull.getDefine().setProductId(product.getId());
		productDefineMapper.insert(productFull.getDefine());
		
		batchSavePrice(productFull.getPrice(), product.getId());
		
		return null;
	}
	
	private void batchSavePrice(List<ProductPrice> list, Integer pid) {
		
		if(list == null){
			return ;
		}
		
		for(ProductPrice price: list){
			price.setProductId(pid);
			productPriceMapper.insert(price);
		}
		
	}
	
	@Override
	public ProductFull queryOneFull(Integer id, Boolean readDefine, SessionUser user) {
		
		if(!available(id, user)){
			return null;
		}
		
		readDefine = readDefine == null?true:readDefine;
		
		Product product = productMapper.queryOne(id);
		if(product==null){
			return null;
		}
		
		ProductFull full = new ProductFull();
		full.setProduct(product);
		full.setDefine(productDefineMapper.queryOneByPid(id));
		List<ProductPrice> priceList = productPriceMapper.queryByPid(id, null, new Date());
		full.setPrice(priceList);
		
		return full;
	}

	public boolean available(Integer id, SessionUser user){
		if(id==null||id.intValue()<=0){
			return false;
		}
		
		//FIXME v1 验证用户对产品信息的访问权限
		return true;
	}

	@Override
	public ProductFull updateFull(ProductFull productFull) {
		
		if(productFull==null || productFull.getProduct()==null ){
			return null;
		}
		
		//初始化
		Product product = productFull.getProduct();
		
//		Integer impact = productMapper.update(productFull.getProduct());
//		if(!AssertHelper.positiveInt(impact) || !AssertHelper.positiveInt(product.getId())){
//			return null;
//		}
		
		productFull.getDefine().setProductId(product.getId());
		productDefineMapper.updateByPid(productFull.getDefine());
		
		return productFull;
	}

	@Override
	public ProductFull initFull(SessionUser user, Product product,
			ProductDefine define) {
		
		ProductFull productFull = new ProductFull();
		productFull.setProduct(product);
		productFull.setDefine(define);
		
		product.setUidCreated(user.getUid());
		product.setUidModified(user.getUid());
		product.setCid(user.getCid());
		
		return productFull;
	}
}
