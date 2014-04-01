Cube.CubeViewer = (function($,escape){
	return {
		initialize : function (options){
			var cStyle;
			var cId;
			var tblId ='#'+escape(options.controlID);
			var table = $(tblId);
			var parent = table.parent();
			if(options.fixedHeaders){
				table.fixedHeaderTable({ 
					footer: false, 
					cloneHeadToFoot: false, 
					fixedColumns: 1,
					height: "600px", 
					width: parent.width()+"px"
				});
			}
			
			table.find("tr").mouseover(function() {
				$(this).addClass("hover");
			}).mouseout(function() {
				$(this).removeClass("hover");
			});
		},
		destroy: function(options){
			var tblId ='#'+escape(options.controlID);
			var table = $(tblId);
			table.fixedHeaderTable('destroy');
		}
	};
}(jQuery,JWic.util.JQryEscape));