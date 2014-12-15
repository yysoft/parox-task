define(		["jquery","template","utils/table","messenger", "Global"],
	function( jQuery,  template,   table,            messenger,    Global){
	
		var message=Messenger();
		var status={"table":table, "message":message};
		
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
		
		return status;
	}
);