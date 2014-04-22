Cube.CubeViewer = (function($,escape){
	var MAGIC_NUMBER = 10,
		RESIZE_TIMER = 100,
		SCROLL_WIDTH = /msie/.test(navigator.userAgent.toLowerCase()) ? 17 : 15;
		
	function sizeSetter(control, config, table){
		var size = getCorrectSize(control.find('div').first(), config),
			wrapper = control.find('.fht-table-wrapper'),
			bodyFixed = control.find('.fht-fixed-body'),
			headHeight = control.find('.fht-thead').height(),
			maxWidth = control.find('.fht-thead > .fht-table').sort(function(a, b){
				return $(b).width() - $(a).width() ;
			}).width();
		
		if(size.width + SCROLL_WIDTH <= maxWidth){
			wrapper.width(size.width);
			bodyFixed.width(size.width);
		}else{
			wrapper.width(maxWidth + 3);
			bodyFixed.width(maxWidth + 3);
		}
		
		
		control.find('.fht-fixed-column > .fht-tbody').height(size.height - headHeight - SCROLL_WIDTH );
		control.find('.fht-fixed-body > .fht-tbody').height(size.height - headHeight);
		wrapper.height(size.height);
		
		
	}
	
	function getCorrectSize(table, config){
		var x = parseInt($(table).outerWidth(), 10);
		var y = parseInt(($(window).outerHeight()-config.heightOffset),10);
		return {
			width : x,
			height : y
		};
	}
	
	return {
		initialize : function (options){
			var cStyle, cId,
				tblId ='#'+escape(options.controlID),
				table = $(tblId),
				control = $('#ctrl_'+escape(options.controlID)),
				parent = table.parent(),
				config =  options.fixedHeaderConfig,
				size;
			
			function setSize(){
				sizeSetter(control, options.fixedHeaderConfig, table);
				return false;
			}
			if(options.fixedHeaders){
				size = getCorrectSize(control.find('div'), config, table);
				config.width = size.width + 'px';
				config.height = size.height+ 'px';
				table.fixedHeaderTable(config);
				sizeSetter(control, options.fixedHeaderConfig, table);
				
				jQuery(window).resize(setSize);
				table.data('resize', setSize);
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
			var resizeFunction = table.data('resize');
			table.fixedHeaderTable('destroy');
			if(resizeFunction){
				jQuery(window).off('resize',resizeFunction);
			}
			resize = null;
		}
	};
}(jQuery,JWic.util.JQryEscape));