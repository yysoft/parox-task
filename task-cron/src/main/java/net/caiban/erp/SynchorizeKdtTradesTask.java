/**
 * 
 */
package net.caiban.erp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.caiban.db.YYConn;
import net.caiban.db.YYConnPool;
import net.caiban.erp.domain.SysApp;
import net.caiban.utils.DateUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.daoman.task.CronTask;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kdt.api.KdtApiClient;

/**
 * 同步口袋通订单
 * @author parox
 *		1. 拿到公司的 app info，暂时仅koudaitong
 *		2. 获取basedate前一天的数据 use_has_next
 *		3. 检查是否是目标数据（num_iid exist in product.code -> pid）
 *		4. 检查是否已经保存过 (tid exist in trade)
 *		5. 若存在，跳过，否则保存
 */
public class SynchorizeKdtTradesTask implements CronTask {

	final static String DATE_ZERO="yyyy-MM-dd 00:00:00";
	
	final static Logger LOG = Logger.getLogger(SynchorizeKdtTradesTask.class);
	
	@Override
	public boolean exec(Date baseDate) throws Exception {

		String from = DateUtil.toString(DateUtil.getDateAfterDays(baseDate, -10), DATE_ZERO);
		String to = DateUtil.toString(baseDate, DATE_ZERO);
		
		List<SysApp> appList = null;
		
		Integer idMax = 0;
		do {
			appList = queryApp(idMax);
			
			for(SysApp app: appList){
				idMax = app.getId();
				
				synchronizeKdt(app, from, to);
			}
		} while (appList!=null && appList.size()>0);
		
		return false;
	}
	
	private List<SysApp> queryApp(Integer idMax) throws SQLException{
		YYConn conn = new YYConn("pcerp");
		
		StringBuffer sb = new StringBuffer();
		sb.append(" select id, cid, app_key, app_secret from sys_app");
		sb.append(" where domain='koudaitong.com'");
		sb.append(" and id>").append(idMax);
		sb.append(" order by id asc limit 200");
		
		List<SysApp> result = Lists.newArrayList();
		try {
			ResultSet rs = conn.executeQuery(sb.toString());
			while (rs.next()) {
				SysApp app = new SysApp();
				app.setId(rs.getInt("id"));
				app.setCid(rs.getInt("cid"));
				app.setAppKey(rs.getString("app_key"));
				app.setAppSecret(rs.getString("app_secret"));
				result.add(app);
			}
		} finally {
			if(conn!=null){
				conn.close();
			}
		}
		
		return result;
	}
	
	private void synchronizeKdt(SysApp app, String from, String to) throws Exception {
		KdtApiClient client = null;
		try {
			client =  new KdtApiClient(app.getAppKey(), app.getAppSecret());
		} catch (Exception e) {
			throw new Exception("Error create kdt client. app key["+app.getAppKey()+"]");
		}
		
		HashMap<String, String> params = Maps.newHashMap();
		params.put("status", "WAIT_BUYER_CONFIRM_GOODS");
		params.put("start_created", from);
		params.put("end_created", to);
		params.put("use_has_next", "true");
		
		boolean hasNext = false;
		int pageNo = 1;
		
		do{
			JSONObject jobj = remoteTrades(client, params, pageNo);
			
			if(jobj==null){
				break;
			}
			
			hasNext = jobj.optBoolean("has_next", false);
			pageNo++;
			
			JSONArray jarry = jobj.getJSONArray("trades");
			
			for(int i=0;i<jarry.size();i++){
				saveTrade(jarry.optJSONObject(i), app.getCid());
			}
			
		}while(hasNext);
	}
	
	private void saveTrade(JSONObject trade, Integer cid){
		
		if(trade==null){
			return ;
		}
		
		int pid = queryPid(trade.optString("num_iid", ""), cid);
		if(pid<=0){
			return ;
		}
		
		saveToDB(trade, cid, pid);
	}
	
	private int queryPid(String numIid, Integer cid){
		YYConn conn = new YYConn("pcerp");
		
		StringBuffer sb = new StringBuffer();
		sb.append("select id from product ");
		sb.append(" where cid=").append(cid);
		sb.append(" and code='").append(numIid).append("'");
		try {
			ResultSet rs = conn.executeQuery(sb.toString());
			while (rs.next()) {
				int pid = rs.getInt("id");
				return pid;
			}
		} catch (Exception e) {
			LOG.error("Error execute sql: "+sb.toString());
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
		return 0;
	}
	
	private void saveToDB(JSONObject trade, Integer cid, int pid){
		if(existed(trade.optString("tid", ""), cid)){
			updateGmtCreated(trade, cid);
			return ;
		}
		
		YYConn conn = new YYConn("pcerp");
		
		StringBuffer sb = new StringBuffer();
		sb.append("insert into trade(cid, pid_first, trade_num, source_domain, source_type, gmt_created, gmt_modified, status )");
		sb.append(" values(?,?,?,'koudaitong.com','API',?,now(),0)");
		
		try {
			
			conn.prepareStatement(sb.toString());
			
			conn.setInt(1, cid);
			conn.setInt(2, pid);
			conn.setString(3, trade.optString("tid", ""));
			conn.setString(4, trade.optString("created", DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm:ss")));
			
			conn.executeUpdate();
			
			int tradeId=insertResult(conn);
			
			sb = new StringBuffer();
			sb.append("insert into trade_define(trade_id, details, gmt_created, gmt_modified) ");
			sb.append(" values(?,?,now(),now())");
			conn.prepareStatement(sb.toString());
			conn.setInt(1, tradeId);
			conn.setString(2, trade.toString());
			
			conn.executeUpdate();
			
		} catch (Exception e) {
			LOG.error("Error execute sql: "+sb.toString());
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
		
	}
	
	private Integer updateGmtCreated(JSONObject trade, Integer cid){
		
		String tid = trade.optString("tid", "");
		if(Strings.isNullOrEmpty(tid)){
			return 0;
		}
		
		YYConn conn = new YYConn("pcerp");
		StringBuffer sb = new StringBuffer();
		sb.append("update trade set");
		sb.append(" gmt_created='").append(trade.optString("created", DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm:ss"))).append("'");
		sb.append(" where cid=").append(cid);
		sb.append(" and source_domain='koudaitong.com'");
		sb.append(" and trade_num='").append(trade.optString("tid", "")).append("'");

		try {
			return conn.executeUpdate(sb.toString());
		} catch (SQLException e) {
			return 0;
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
		
	}
	
	public int insertResult(YYConn conn) {
		try {
			ResultSet rs = conn.executeQuery(
					"select last_insert_id()");
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private boolean existed(String tid, Integer cid){
		
		YYConn conn = new YYConn("pcerp");
		StringBuffer sb = new StringBuffer();
		sb.append("select count(*) as c from trade ");
		sb.append(" where cid=").append(cid);
		sb.append(" and source_domain='koudaitong.com'");
		sb.append(" and trade_num='").append(tid).append("'");
		try {
			ResultSet rs = conn.executeQuery(sb.toString());
			while (rs.next()) {
				if(rs.getInt("c")>0){
					return true;
				}
			}
		} catch (Exception e) {
			LOG.error("Error execute sql: "+sb.toString());
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
		return false;
	}
	
	private JSONObject remoteTrades(KdtApiClient client, HashMap<String, String> params, Integer pageNo){
		
		try {
			params.put("page_no", String.valueOf(pageNo));
			HttpResponse response = client.get("kdt.trades.sold.get", params);
			String respString = EntityUtils.toString(response.getEntity());
			JSONObject jobj = JSONObject.fromObject(respString);
			
			return jobj.getJSONObject("response");
		} catch (Exception e) {
			return null;
		}
		
	}
	
	@Override
	public boolean clear(Date baseDate) throws Exception {
		return false;
	}
	
	public static void main(String[] args) throws Exception {
		YYConnPool.getInstance().initConnPools(null);
		SynchorizeKdtTradesTask task = new SynchorizeKdtTradesTask();
		
		task.exec(new Date());
	}

}
