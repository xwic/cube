Cube.CubeViewer = (function($,escape){
	var COLUMN_WIDTH = 80,
		FIRST_COLUMN_WIDTH = 266,
		SCROLL_BAR_SIZE = 15,
		HEIGHT_OF_THE_REST_OF_THE_WINDOW = 380; //aproximate height of everything else
	
	
	function sizeSetter(table){
		table.fixedHeaderTable('destroy');
		var x = ((table.find('tr').first().find('th').length -1 ) * COLUMN_WIDTH + FIRST_COLUMN_WIDTH+ SCROLL_BAR_SIZE);
		x = x > table.parent().width() ? table.parent().width() : x;
		var y = parseInt(($(window).height()-HEIGHT_OF_THE_REST_OF_THE_WINDOW),10);
		table.fixedHeaderTable({ 
			footer: false, 
			cloneHeadToFoot: false, 
			fixedColumns: 1,
			height: y+"px",
			width: x+"px"
		});
	}
	
	return {
		initialize : function (options){
			var cStyle, cId,
				tblId ='#'+escape(options.controlID),
				table = $(tblId),
				parent = table.parent();
			
			function setSize(){
				sizeSetter(table);
			}
			if(options.fixedHeaders){
				sizeSetter(table);
				setTimeout(setSize, 0);
				(function (){
					var resizeTimer;
					jQuery(window).resize(function() {
					    clearTimeout(resizeTimer);
					    resizeTimer = setTimeout(setSize, 50);//fancy resize callback to make the ui more responsive
					});
				}());
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