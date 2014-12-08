define(		["jquery","template","utils/table","messenger"],
	function( jQuery,  template,   table,            messenger){
	
		var form={};
		var definition={"table":table, form: form};
		
		table["preBuildTable"]=function(p){
			console.log('build table before:'+p);
			return p;
		};
		
		table["afterBuildTable"]=function(){
			console.log('build table after');
		};
		
		form["fillForm"]=function(url, data, formId){
			jQuery.post(url, data, function(resp){
				
				form.fillBaseForm(resp, formId);
				
			},"json");
		};
		
		form["setUrl"]=function(url){
			this.saveUrl=url;
		};
		
		form["fillBaseForm"]=function(o, formId){
			var item = null;
			var type=null;
			
			for(var name in o){
				item=jQuery("#"+formId+" input[name="+name+"]");
				
				if(typeof item.attr("type")== "undefined"){
					
					continue;
				}
				
				if(item.attr("type") == "text" || item.attr("type") == "hidden"){
					jQuery("#"+formId+" input[name="+name+"]").val(o[name]);
					console.log(name+"   "+o[name])
				}
				
				if(item.attr("type") == "radio"){
					jQuery("#"+formId+" input[name="+name+"][value="+o[name]+"]").prop("checked",true);
					console.log(name+"   "+o[name])
				}
				
			}
			
			jQuery("#"+formId+" textarea[name=description]").val(o["description"]);
			
		};
		
		form["save"]=function(form){
			jQuery.post(this.saveUrl, form.serialize(), function(resp){
				//提示消息
				console.log("保存成功："+JSON.stringify(resp));
			}, "json");
		};
		return definition;
	}
);