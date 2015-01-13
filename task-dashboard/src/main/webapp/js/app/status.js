define(		["jquery","template","utils/table","messenger", "Global"],
	function( jQuery,  template,   table,            messenger,    Global){
	
		var message=Messenger();
		var def={"table":table, "message":message};
		
		table["preBuildTable"]=function(p){
			if(p.records == null){
				return p;
			}
			
			jQuery.each(p.records, function (idx, obj){
				obj.gmtBasetimeStr=new Date(obj.gmtBasetime).format(Global.DATE_FORMAT_DEFAULT);
				obj.gmtTriggerStr=new Date(obj.gmtTrigger).format(Global.DATE_FORMAT_DEFAULT);
			});
			return p;
		}
		
		table["afterBuildTable"]=function(){
			
		};
		
		def["bindRemoveOne"]=function(root, act){
			
			root.on("click",act, function(){
				
				var data={};
				data["id"]=jQuery(this).attr("model-id");
				var _this=jQuery(this);
				
				def.doRemove(data, function(){
					_this.parent().parent().hide();
				});
				
			});
		};
		
		def["bindRerun"]=function(root, act){
			root.on("click", act, function(){
				var data={};
				data["jobName"] = jQuery(this).attr("model-job-name");
				data["gmtBasetime"]=jQuery(this).attr("model-gmt-basetime");
				
				if(!confirm("Are you sure?")){
					return false;
				}
				
				def.doRun(data, function(resp){});
				
			});
		};
		
		def["doRemove"]=function(data, cb){
			
			if(!confirm("Are you sure?")){
				return false;
			}
			
			jQuery.post(CONTEXT_PATH+"/status/delete.do", data, function(resp){
				if(resp.result){
					cb(resp);
					message.post({
						message: resp.data,
						type: "success",
						hideAfter:2,
						showCloseButton: true
					});
				}else{
					message.post({
						message: resp.data,
						type: "error",
						hideAfter:5,
						showCloseButton: true
					});
				}
			}, "json");
		};
		
		def["removeAll"]=function(jobName){
			
			var data={"jobName":jobName};
			
			def.doRemove(data, function(){
				def.table.search(data);
			});
		}
		
		def["doRun"]=function(data, cb){
			
			jQuery.post(CONTEXT_PATH+"/definition/run.do", data, function(resp){
				if(resp.result){
					cb(resp);
					message.post({
						message: resp.data,
						type: "success",
						hideAfter:2,
						showCloseButton: true
					});
				}else{
					message.post({
						message: resp.data,
						type: "error",
						hideAfter:5,
						showCloseButton: true
					});
				}
			}, "json");
			
		}
		
		return def;
	}
);