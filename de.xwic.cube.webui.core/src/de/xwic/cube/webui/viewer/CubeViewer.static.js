Cube.CubeViewer = (function($,escape){
	return {
		initialize : function (options){
			var cStyle;
			var cId;
			var tblId ='#'+escape(options.controlID); 
			if(options.fixedHeaders){
				$(tblId).fixedHeaderTable({ 
					footer: false, 
					cloneHeadToFoot: false, 
					fixedColumns: 1 
				});
				
			}
			$(tblId).find("tr").mouseover(function() {
				$(this).addClass("hover");
			}).mouseout(function() {
				$(this).removeClass("hover");
			});
		},
		destroy: function(options){
			
		}
	};
}(jQuery,JWic.util.JQryEscape));