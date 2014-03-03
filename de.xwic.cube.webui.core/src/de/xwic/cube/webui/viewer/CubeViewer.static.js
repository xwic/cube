Cube.CubeViewer = (function($,escape){
	return {
		initialize : function (options){
			if(options.fixedHeaders){
				$('#'+escape(options.controlID)).fixedHeaderTable({ 
					footer: false, 
					cloneHeadToFoot: false, 
					fixedColumns: 1 
				});
			}
		},
		destroy: function(options){
			
		}
	};
}(jQuery,JWic.util.JQryEscape));