/**
 * 
 */
package com.daoman.task.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import net.caiban.db.YYConnPool;

import org.springframework.stereotype.Component;

/**
 * @author parox
 *
 */
@Component("jobInit")
public class JobInit {
	
	@PostConstruct
	public void init(){
		YYConnPool.getInstance().initConnPools(null);
		
		//TODO 初始化任务
	}
	
	@PreDestroy
	public void destroy(){
		YYConnPool.getInstance().destoryConnectionPools();
	}
	
}
