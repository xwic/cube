Cube.CubeViewer = (function($,escape){
	var MAGIC_NUMBER = 10;
	var resize;
	
	function sizeSetter(table, config){
		table.fixedHeaderTable('destroy');
		var x = ((table.find('tr').first().find('th').length -1 ) * config.columnWidth + config.firstColumnWidth);
		x = x > table.parent().width() - config.widthOffset ? table.parent().width() - config.widthOffset : (x + config.columnWidth + MAGIC_NUMBER);
		
		var y = parseInt(($(window).height()-config.heightOffset),10);
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
					
					resize = function resize() {
						var newHeight = win.height(),
							newWidth = win.width();
				    	if(winWidth !== newWidth || winHeight !== newHeight){
				    		clearTimeout(resizeTimer);
				    		winWidth = newWidth;
					    	winHeight = newHeight;
					    	resizeTimer = setTimeout(setSize, 100);//fancy resize callback to make the ui more responsive
					    }
					    
					}
					jQuery(window).resize(resize);
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
			jQuery(window).off('resize',resize);
			resize = null;
		}
	};
}(jQuery,JWic.util.JQryEscape));