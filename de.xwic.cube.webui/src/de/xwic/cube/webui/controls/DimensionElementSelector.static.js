Cube.DimensionElementSelector = (function($,util,Cube){
	"use strict";
	var renderTree,showTree,parseList,split,strip,openPath,closePaths,
		escape = util.JQryEscape,
		map = util.map,
		reduce = util.reduce,
		compose = util.compose,
		requestResource = JWic.resourceRequest,
		fireAction = JWic.fireAction;
		
	split = function(separator){
		return function(string){
			return string.split(separator);
		};
	};
	strip = (function(){
		var regExp = new RegExp("'", 'g');
		return function(string){
			return string.replace(regExp,"");
		}
	}());
	
	openPath = function openPath(control){
		var innerControl = control;
		return function openPathInner(path){
			innerControl = innerControl.find("#"+path.shift()).show();
			if(path.length > 0){
				return openPathInner(path);
			}
			return control;
		};
	};
	closePaths = function closePaths(control){
		control.find('.treeNode').hide();
		return control;
	};
	showTree = function showTree(control,controlId,paths) {
		var elm = control.find('#xcubeLeafselBox'),
			data = elm.data('data'),
			elmTbl = control.find("#xcubeLeafselTbl"),
			showTreeLink = control.find('#showTree'),
			content = elm.find('#xcubeLeafselBoxContent');
			
		elm.css('display',"inline")
		.show()
		.position({
			my : 'left top',
			at: 'left bottom',
			of : showTreeLink
		}).on('click',function(){
			return false;
		}).find('#xcubeLeafselBoxLoading').show();
		
		
		$(window).one('click',function(){
			elm.hide();
		});
		if(data == null){
			requestResource(controlId,function(data){
				data = $.parseJSON(data.responseText);;
				renderTree(data, content, controlId, paths)
				elm.data('data',data);
			},'');
		}else{
			renderTree(data, content, controlId, paths);
		}
		return elm;
	};
	renderTree = function renderTree(dimensions,container,id,paths){
		var stripSplitAndOpen = compose([openPath(container),split("/"),strip]);
		
		container.html('').append(parseList(dimensions.elements,id,''));
		container.parent().find('#xcubeLeafselBoxLoading').hide();
		
		map(paths,stripSplitAndOpen);
		
		return container;
	};
	
	parseList = function parseList(list,controlId, rootKey){
		return reduce(list, function(ul,data){
			var key = data.key,
				title = data.title || key,
				li = $('<li>'),
				innerList = parseList(data.elements, controlId, rootKey+key + "/").attr('id',key).hide().addClass('treeNode'),
				openA = $('<a>').attr('href',"javascript: void(0)").text('+').on('click',function(){
					innerList.toggle();
					a.toggleClass('treeNodeOpen');
				}),
				a = $('<a>').attr('href','javascript: void(0)').text(title).on('click',function(){
					fireAction(controlId,'selection',li.data('node'));
				});
			
				li.data('node',rootKey+key);
				
				if(data.elements.length > 0){//if not leaf
					li.append(openA);
				}
				li.append(a);
				
			return ul.append(li.append(innerList));
		},$('<ul>').attr('id','root'));
	};

	return {
		initialize : function(controlId,options){
			var	control = $('#ctrl_'+escape(controlId));
			control.find('#showTree').on('click',function(){
				showTree(control,controlId,options.dimensionElementsPaths);
				return false;
			});
			
			
		},
		destroy : function(controlId, options){
			$('#ctrl_'+escape(controlId)).unbind();
		}
	};
}(jQuery,JWic.util,Cube));
