Cube.CubeViewer = (function($,escape){
	return {
		initialize : function (options){
			$('#'+escape(options.controlID)).fixedHeaderTable({ 
				footer: false, 
				cloneHeadToFoot: false, 
				fixedColumns: 1 
			});
		},
		destroy: function(options){
			
		}
	};
}(jQuery,JWic.util.JQryEscape));