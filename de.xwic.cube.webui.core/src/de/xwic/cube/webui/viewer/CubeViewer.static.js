Cube.CubeViewer = (function($,escape){

	var WIDTH_OF_THE_REST_OF_THE_WINDOW = 10;//aproximate width of everything else
		HEIGHT_OF_THE_REST_OF_THE_WINDOW = 380; //aproximate height of everything else
	
	
	function sizeSetter(table, config){
		table.fixedHeaderTable('destroy');
		var x = ((table.find('tr').first().find('th').length -1 ) * config.columnWidth + config.firstColumnWidth);
		x = x > table.parent().width() - WIDTH_OF_THE_REST_OF_THE_WINDOW ? table.parent().width() - WIDTH_OF_THE_REST_OF_THE_WINDOW : x;
		var y = parseInt(($(window).height()-HEIGHT_OF_THE_REST_OF_THE_WINDOW),10);
		config.width = x + 'px';
		config.height = y + 'px';
		table.fixedHeaderTable(config);
	}
	
	return {
		initialize : function (options){
			var cStyle, cId,
				tblId ='#'+escape(options.controlID),
				table = $(tblId),
				parent = table.parent();
			
			function setSize(){
				sizeSetter(table, options.fixedHeaderConfig);
			}
			if(options.fixedHeaders){
				sizeSetter(table, options.fixedHeaderConfig);
				(function (){
					var resizeTimer = 0,
						win = $(window);
						winWidth = win.width(),
						winHeight = win.height();
					
					jQuery(window).resize(function() {
						var newHeight = win.height(),
							newWidth = win.width();
				    	if(winWidth !== newWidth || winHeight !== newHeight){
				    		clearTimeout(resizeTimer);
				    		winWidth = newWidth;
					    	winHeight = newHeight;
					    	resizeTimer = setTimeout(setSize, 50);//fancy resize callback to make the ui more responsive
					    }
					    
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