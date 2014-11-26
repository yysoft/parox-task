define(		["jquery","template","utils/table","messenger"],
	function( jQuery,  template,   table,            messenger){
	
//		var definition=jQuery.extend(true, {}, table,{
//			preBuildTable:function(p){
//				console.log('build table before:'+p);
//			},
//			afterBuildTable:function(){
//				console.log('build table after');
//			}
//		});
	
	
//		var definition=(function(tb){
//			
//			tb["preBuildTable"]=function(p){
//				console.log('build table before:'+p);
//			};
//			
//			tb["afterBuildTable"]=function(){
//				console.log('build table after');
//			};
//			
//			return tb;
//		})(table);
//		
//		debugger;
//		return definition;
	
	
		var definition={"table":table};
		
		table["preBuildTable"]=function(p){
			console.log('build table before:'+p);
			return p;
		};
		
		table["afterBuildTable"]=function(){
			console.log('build table after');
		};
		
		return definition;
	}
);