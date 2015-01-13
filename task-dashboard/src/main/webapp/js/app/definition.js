define(		["jquery","template","utils/table","messenger"],
	function( jQuery,  template,   table,            messenger){
	
		var form={};
		var definition={"table":table, form: form};
		var message=Messenger();
		
		definition["remove"]=function(id, btn){
			if(!confirm("Are you sure?")){
				return false;
			}
			
			var data = {"id":id};
			
			jQuery.post(CONTEXT_PATH+"/definition/delete.do", data, function(resp){
				if(resp.result){
					btn.parent().parent().hide();
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
		
		definition["run"]=function(id, runST, btn){
			
			var url=CONTEXT_PATH+"/definition/startTask.do";
			if(runST=="1"){
				url=CONTEXT_PATH+"/definition/stopTask.do";
			}
			
			jQuery.post(url, {id:id}, function(resp){
				if(resp.result){
					if(runST == "0" ){
						btn.removeClass("btn-success");
						btn.addClass("btn-warning");
						btn.children().removeClass("glyphicon-play");
						btn.children().addClass("glyphicon-stop");
						
						jQuery("#run-st-"+id).removeClass("glyphicon-stop");
						jQuery("#run-st-"+id).removeClass("text-danger");
						jQuery("#run-st-"+id).addClass("glyphicon-play");
						jQuery("#run-st-"+id).addClass("text-success");
						
						btn.attr("model-isinuse",1);
					}
					if(runST=="1"){
						btn.removeClass("btn-warning");
						btn.addClass("btn-success");
						btn.children().removeClass("glyphicon-stop");
						btn.children().addClass("glyphicon-play");
						
						jQuery("#run-st-"+id).removeClass("glyphicon-play");
						jQuery("#run-st-"+id).removeClass("text-success");
						jQuery("#run-st-"+id).addClass("glyphicon-stop");
						jQuery("#run-st-"+id).addClass("text-danger");
						
						btn.attr("model-isinuse",0);
					}
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
		
		table["preBuildTable"]=function(p){
			
			return p;
		};
		
		table["afterBuildTable"]=function(){
//			jQuery("#"+table.config.renderTo+" .act-delete").click(function(){
//				var id= jQuery(this).attr("model-id");
//				
//				definition.remove(id, jQuery(this));
//				
//			});
//			
//			jQuery("#"+table.config.renderTo+" .act-run").click(function(){
//				
//				var id= jQuery(this).attr("model-id");
//				var runST=jQuery(this).attr("model-isinuse");
//				definition.run(id, runST, jQuery(this));
//				
//			});
		};
		
		definition["bindTableAct"]=function(){
			
			jQuery("#"+table.config.renderTo).on("click", "button[data-act=run]", function(){
				
				var id= jQuery(this).attr("model-id");
				var runST=jQuery(this).attr("model-isinuse");
				definition.run(id, runST, jQuery(this));
				
			});
			
			jQuery("#"+table.config.renderTo).on("click", "button[data-act=remove]", function(){
				
				var id= jQuery(this).attr("model-id");
				definition.remove(id, jQuery(this));
				
			});
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
		
		//@Deprecate
		form["save"]=function(form, isWithUpload, cb){
			
			if(!isWithUpload){
				
				jQuery.post(this.saveUrl, form.serialize(), function(resp){
					cb(true, resp);
				}, "json");
				return true;
			}
			
			jQuery.ajax({
				url:this.saveUrl,
				data:form.serialize(),
				type:"POST",
//				contentType:"multipart/form-data",
				contentType:false,
				cache:false,
				dataType:"json",
				error:function(jqxhr, textStatus, error){
					cb(false, {textStatus: textStatus});
				},
				success:function(resp){
					cb(true, resp);
				}
			});
			
		};
		return definition;
	}
);