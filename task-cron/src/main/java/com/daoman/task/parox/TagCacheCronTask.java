/**
 * 
 */
package com.daoman.task.parox;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.caiban.db.YYConn;
import net.caiban.db.YYConnPool;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.daoman.task.CronTask;
import com.daoman.task.domain.parox.Tag;
import com.daoman.task.utils.JedisUtil;

/**
 * 将 redis 中标签的缓存信息，存储到数据库中（主要是标签数量计算）
 * @author parox
 *
 */
public class TagCacheCronTask implements CronTask {
	
	Logger LOG = Logger.getLogger(TagCacheCronTask.class);
	
	@Override
	public boolean exec(Date baseDate) throws Exception {
		
		Jedis jedis = JedisUtil.getJedis();
		
		Set<String> tags = jedis.keys("tag_*_*_*");
//		for(String tag: tags){
//			saveToDB(tag, jedis);
//		}
		
		saveToDB("test", jedis);
		
		return true;
	}
	
	
	private void saveToDB(String key, Jedis jedis){
		
		boolean isUpdated = false;
		while(!isUpdated){
			
			jedis.watch(key);
			
			Transaction tx = jedis.multi();
			tx.get(key);
			tx.del(key);
			List<Object> result = tx.exec();
			
			if(result!=null){
				String tagStr = (String) result.get(0);
				
//				Tag tag = (Tag) JSONObject.toBean(JSONObject.fromObject(tagStr), Tag.class);
//				tagService.mergeTag(tag);
				Tag tag =  new Tag();
				tag.setInfoCount(0l);
				tag.setSearchCount(0l);
				tag.setClickCount(0l);
				tag.setTagName("测试");
				tag.setOrgId(0l);
				tag.setCat(0);
				
				saveTag(tag);
				isUpdated = true;
			}
			
			jedis.unwatch();
		}
	}
	
	private boolean saveTag(Tag tag){
		YYConn conn = new YYConn("parox");
		
		StringBuffer sb = new StringBuffer();
		sb.append("update c_tag set" )
			.append(" info_count=info_count+?,")
			.append(" search_count = search_count + ?,")
			.append(" click_count = click_count + ?")
			.append(" where tag_name= ?")
			.append(" and org_id=?")
			.append(" and cat = ?");
		
		boolean result =true;
		try {
			conn.prepareStatement(sb.toString());
			conn.setLong(1, tag.getInfoCount());
			conn.setLong(2, tag.getSearchCount());
			conn.setLong(3, tag.getClickCount());;
			conn.setString(4, tag.getTagName());
			conn.setLong(5, tag.getOrgId());
			conn.setInt(6, tag.getCat());
			
			int impact = conn.executeUpdate();
			if(impact==0){
				result = false;
			}
		} catch (SQLException e) {
			LOG.error("Erorr prepare statement. SQL:"+sb.toString());
			result = false;
		}finally{
			conn.close();
		}
		
		return result;
	}

	@Override
	public boolean clear(Date baseDate) throws Exception {
		return false;
	}

	public static void main(String[] args) {
		YYConnPool.getInstance().initConnPools(null);
		TagCacheCronTask task = new TagCacheCronTask();
		try {
			task.exec(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
