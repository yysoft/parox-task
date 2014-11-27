define(		["jquery","template","utils/table","messenger"],
	function( jQuery,  template,   table,            messenger){
	
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