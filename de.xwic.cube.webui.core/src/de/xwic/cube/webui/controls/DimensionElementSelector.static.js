Cube.DimensionElementSelector = (function($,util,Cube){
	"use strict";
	var buildHtml, buildTree, buildMultiSelectKey, bindNode, intialOpen, applyFilter, clearFilter, refreshTreeStates,
		nodeMap = {},
		tmpl = Cube.tmpl,
		map = util.map,
		reduce = util.reduce,
		defineProp = Cube.defineProp,
		defineObservable = Cube.defineObservable,
		load = JWic.resourceRequest,
		fireAction = JWic.fireAction;
	//Node Data Model
	function Node(title, key, path){
		var childListener,
			eventListeners = [],
			children = [],
			that = this;
		//define public properties
		defineProp(this,"expanded");
		defineProp(this,"state");
		defineProp(this,"matchFilter");
		this.matchFilter(true);
		//the key (read-only)
		this.key = function(){
			return key;
		};
		//the title (read-only)
		this.title = function(){
			return title;
		}
		//the path in the cube
		this.path = function(){
			return path;
		};
		
		//return a copy of the children of this node
		this.children = function(){
			return children.concat();
		};
		//make this instance observable on the property changed event
		//this adds a firePropertyChanged method on this object and an event system (if needed)
		defineObservable(this,"propertyChanged");
		
		//setup prop changed listener for 'my' state
		//this will trigger a silent child prop change to this prop
		this.on('propertyChanged',function(prop,val){
			if(prop === 'state'){
				if(this.state() === Node.SELECTED || this.state() === Node.UNSELECTED){
					map(children,function(c){
						//change state only if the child matches the filter(is visible)
						if (c.matchFilter()){
							c.state(val);//set state but don't fire event if state is UNDEFINED
						}
					});
				}
			}
		});
		/**
		 * private callback for children to hook on to
		 */
		childListener = function(propName,value){
			var childrenStatesSame = false,
				child = this;
			if(propName === 'state'){
				childrenStatesSame = !reduce(children, function(acc, c){
					//take into account only the filtered(visible) children
					return acc || (c.matchFilter() && c.state() !== child.state());
				},false);
				if(childrenStatesSame){
					that.state(child.state());
				}else{
					that.state(Node.UNDEFINED);
				}
				
			}else if(propName === 'expanded'){
				if(value){
					that.expanded(true);
				}
			}
		};
		/**
		 * 
		 * @param child
		 * @returns {Node}
		 */
		this.addChild = function addChild(child){
			child.on("propertyChanged",childListener);
			children.push(child);
			return this;
		}
		/**
		 * 
		 * @param child
		 * @returns {Node}
		 */
		this.removeChild = function addChild(child){
			var index = children.indexOf(child);
			children[index].unbind('propertyChanged',childListener);
			children.splice(index,1);
			return this;
		}
		this.toggle = function(){
			var state = this.state();
			if(state === Node.SELECTED){
				state = Node.UNSELECTED;
			}else if(state === Node.UNSELECTED){
				state = Node.SELECTED;
			}else if(state === Node.UNDEFINED){
				state = Node.UNSELECTED;
			}
			this.state(state);
		}
	}
	//possible states
	Node.SELECTED = "checked";
	Node.UNDEFINED = "undefined";
	Node.UNSELECTED = "unchecked";
	
	buildHtml = (function buildHtml(control,options){
		var loaded = false,
			controlId = options.controlId,
			container = control.find("#tree-container"),
			loading   = control.find("#tree-loading"),
			tree	  = control.find("#tree"),
			doc 	  = $(document),
			buttonOk  = control.find('#tree-submit'),
			rootNode  = tmpl(control, "#node-template"),
			nodeModel = new Node(options.defaultTitle, "","");
		//some css on root node and clean up on the root node
		//its sort of a special case
		rootNode.addClass("root").find('#expand').remove();
		rootNode.find("#select").addClass("root-check");
		
		//clear the node map
		nodeMap = {};
		bindNode(nodeModel,rootNode, options, {});
		nodeModel.state(Node.UNSELECTED);
		//multiselect ok button
		buttonOk.unbind('click').on('click',function(){
			fireAction(controlId,'selection',buildMultiSelectKey(nodeModel));
			return false;
		});
		return function(){
			
			//show the container
			container.show();
			//hide the other container
			$('.tree-container').not(container).hide();
			//hide on outside click
			doc.one('click',function(){
				container.hide();
			});
			//blank everything out
			//pop the new content back in
			container.position({
				my: 'left top',
				at: 'left bottom',
				of: control.find('#showTree_container')
			});
			if(!loaded){
				loading.show();
				tree.html('');
				load(controlId,function(resp){
					var data = $.parseJSON(resp.responseText);
					loaded = true;
					loading.hide();
					var filterField = JWic.$('search_' + options.controlId);
					if (filterField){
						filterField.parent().show();
					}
					
					//store the root into the node map for fast retrieval
					nodeMap[nodeModel.path()] = nodeModel;

					buildTree(rootNode.find('#children'), nodeModel, control, data.elements, options);
					intialOpen(nodeModel,options.dimensionElementsPaths);
					tree.append(rootNode);
					//add some css
					tree.find('li:last-child').addClass('last');
					//remove the last expand elements
					tree.find("ul:not(:has(>li))").parent().find('#expand').remove();
				},'');
			}else{
				nodeModel.state(Node.UNSELECTED);
				intialOpen(nodeModel,options.dimensionElementsPaths);//reset what's checked on reopen
			}
			
			return false;
		};
	});
	
	buildTree = function buildTree(tree,nodeModel,control,data,options){
		return reduce(data, function(acc, el){
			var node = tmpl(control, "#node-template"),
				newNode = new Node(el.title || el.key, el.key, nodeModel.path()+el.key+"/");
			bindNode(newNode, node, options, el);
			//setup intial states
			newNode.expanded(false);
			newNode.state(Node.UNSELECTED);
			//add node to the map
			nodeMap[newNode.path()]=newNode;
			nodeModel.addChild(newNode);
			buildTree(node.find('#children') ,newNode, control, el.elements,options);
			return acc.append(node);
		},tree);
	};
	

	buildMultiSelectKey = function buildMultiSelectKey(node) {
		var key = node.path(), 
			multiSelectKey = (function makeKey(key, node) {
				var path = node.path();
				if (node.state() === Node.UNSELECTED) {
					return key;
				}
				if (node.state() === Node.SELECTED) {
					return key + path.substring(0, path.length - 1) + "##";
				}
				return reduce(node.children(), makeKey, key);
			}(key, node));
		return multiSelectKey.substring(0, multiSelectKey.length - 2);//trim off the ## bit at the end
	};

	bindNode = function bindNode(node, uiNode, options, rawNode) {
		var select = uiNode.find('#select'), 
			title = uiNode.find("#title"),
			expand = uiNode.find('#expand'),
			children = uiNode.find('#children');
		
		//add a custom attribute to the li dom element to store the node key to be found in the node map when needed 
		uiNode.attr('nodePath',node.path());
		
		node.on('propertyChanged', function(propName,val, oldVal) {
			
			if (propName === 'state') {
				if(oldVal){
					select.removeClass(oldVal);
				}
				select.addClass(val);
			}else if(propName === 'expanded'){
				if(val){
					children.show();
					expand.addClass('node-close').removeClass("node-open");
				}else{
					children.hide();
					expand.removeClass('node-close').addClass("node-open");
				}
			}
		});
		
		title.text(node.title()).on('click',function() {
			var path = node.path(),
				rawChildNodes = rawNode.elements; 
			if(options.multiSelection){
				node.toggle();//multselect mode
			}else{
				if(options.selectLeafsOnly && rawChildNodes != null && rawChildNodes.length>0){
					node.expanded(!node.expanded());//leaf only mode
				}else{
					fireAction(options.controlId, 'selection', path.substring(
							0, path.length - 1));//regular mode
				}
			}
			return false;
		});
		select.on('click', function() {
			node.toggle();
			return false;
		});
		expand.on('click', function() {
			node.expanded(!node.expanded());
			return false;
		});
	};
	
	intialOpen = function intialOpen(node, paths){
		var children = node.children(),
			path = node.path();
		
		path = path.substring(0,path.length-1);
		if(paths.length === 0){
			node.state(Node.SELECTED);
		}
		if($.inArray(path,paths) != -1){
			node.expanded(true);
			node.state(Node.SELECTED);
		}
		if(children.length === 0){
			return;
		}
		map(children, function(i){
			intialOpen(i,paths);
		});
	};
	
	/**
	 * Clears the the filter if displayed and show all items
	 */
	clearFilter = function clearFilter(options){
		var clearIcon = JWic.$("cse_" + options.controlId);
		var control = JWic.$('ctrl_'+options.controlId);
		if (clearIcon){
			//show all items
			control.find('li a#title').parent().show();
			//hide the template
			control.find('#node-template').hide();
			//hide the icon
			clearIcon.find(".j-listColSel-clearSearch").hide();
			
			//mark all nodes visible
			$.each( nodeMap, function( key, value ) {
				  value.matchFilter(true);
			});
			
			//reevaluate one leaf node of each parent to trigger parent state refresh
			//start from root
			refreshTreeStates(nodeMap[""]);
		}
	};
	
	/**
	 * reevaluate one leaf node of each parent to trigger parent state refresh
	 */
	refreshTreeStates = function refreshTreeStates(node){
		if (node && node.matchFilter() && node.children().length > 0){
			var children = node.children();
			var refreshed = false;
			for(var i=0; i<children.length; i++){
				//if one of the leaf children triggered refresh don't do it again 
				refreshed = refreshed || refreshTreeStates(children[i]);
			}
			return false;
		}else{
			//node is not visible or is not leaf
			if (!node.matchFilter()){
				return false;
			}else{
				//leaf node
				var crtState = node.state();
				//change and restore the state to trigger refreshing the parents
				node.toggle();
				node.state(crtState);
				return true;
			}
		}
	};
	
	/**
	 * Filters the displayed node titles over the specified input. The filtering works only of the first level of nodes
	 */
	applyFilter = function applyFilter(options) {
		var controlId = options.controlId;
		var filterField = JWic.$('search_' + controlId);
		var val = jQuery.trim(filterField.val()).toLowerCase();
		var clearFilter = JWic.$("cse_" + controlId);
		if (val.length != 0) {
			clearFilter.find(".j-listColSel-clearSearch").show();
		} else {
			clearFilter.find(".j-listColSel-clearSearch").hide();
		}
		
		var base = JWic.$('ctrl_'+controlId);
		//var visibleRows = [];
		var visibleRows = {};
		
		//search for nodes that are matching the filter and store them in matchedRows
		base.find('li a#title').each(function(i,item) {
			var row = jQuery(item);
			var title = row.text();
			
			//only the template node has empty text
			if (title.length == 0){
				return;
			} 
			
			//always show the all element if exists
			if (options.defaultTitle=== title){
				//visibleRows.push($(row).parent()[0]);
				visibleRows[$(row).parent().attr('nodePath')]=$(row).parent()[0];
				return;
			}
			
			//if the filter is cleared show all nodes
			if (val.length == 0 || val === options.filterLabel.toLowerCase()) {
				//visibleRows.push($(row).parent()[0]);
				visibleRows[$(row).parent().attr('nodePath')]=$(row).parent()[0];
			} else {
				//if the filter value was found store the li dom element 
				if (title && title.toLowerCase().indexOf(val) != -1) {
					
					//if (visibleRows.indexOf($(row).parent()[0]) == -1){
					if (!visibleRows[$(row).parent().attr('nodePath')]){
						//visibleRows.push($(row).parent()[0]);
						visibleRows[$(row).parent().attr('nodePath')]=$(row).parent()[0];
					}
					
					//add all children on all levels into the visibility list
					row.parent().find('li a#title').each(function(i,item) {
						if (!visibleRows[$(item).parent().attr('nodePath')]){
						//if (visibleRows.indexOf($(item).parent()[0]) == -1){
							//visibleRows.push($(item).parent()[0]);
							visibleRows[$(item).parent().attr('nodePath')]=$(item).parent()[0];
						}
					});
					
					//add all ancestor parents into the visibility list
					var parent = row.parent();
					//stop on root li assuming that the order is respected
					while (parent && parent.length && (parent.is('li') || parent.is('ul') )&& !(parent.hasClass('root') && parent.is('li'))){
						//skip ul
						if (parent.is('ul')){
							parent= parent.parent();
						}
						if (!visibleRows[parent.attr('nodePath')]){
						//if (visibleRows.indexOf(parent[0]) == -1){
							//visibleRows.push(parent[0]);
							visibleRows[parent.attr('nodePath')]=parent[0];
						}
						parent = parent.parent()
					}
					
				} 
			}
			
		});
		
		//iterate again thru all nodes and show only the ones marked as visible
		base.find('li a#title').each(function(i,item) {
			var prnt = $(item).parent()[0];
			var nodePath = $(item).parent().attr('nodePath');
			var node = nodeMap[nodePath];
			if (!node){
				return;
			}
			if (visibleRows[$(item).parent().attr('nodePath')]){
			//if (visibleRows.indexOf(prnt) != -1){
				//this are matching the filter
				node.matchFilter(true);
				$(prnt).show();
			}else{
				node.matchFilter(false);
				$(prnt).hide();
			}
		});
		
		//reevaluate one leaf node of each parent to trigger parent state refresh
		//start from root
		refreshTreeStates(nodeMap[""]);
		
	};
	
	//exports
	return {
		initialize : function(options){
			var control = JWic.$('ctrl_'+options.controlId),
				build = buildHtml(control,options);
			
			control.find("#showTree").on('click', build);
			
			//initialize the filter if exists
			var filterField = JWic.$('search_' + options.controlId);
			var clearFilterIcon = JWic.$("cse_" + options.controlId);
			
			if (filterField){

				filterField.on("keyup", function(e) { 
					var val = this.value; 

					if (val.length == 0){
						clearFilter(options);
					} else if (val.length >= options.minCharsToTriggerFiltering){
						applyFilter(options);
					}
				});
				filterField.on("click", function(e) {e.stopPropagation();});
				
				/** On IE 11 the placeholder does not work so we have to simulate with focus/blur handlers*/
				filterField.on("focus", function(e) {
					if (this.value == options.filterLabel){
						this.value = '';	
					}
				});
				
				filterField.on("blur", function(e) {
					if (this.value == ''){
						this.value = options.filterLabel;	
					}
				});
				
				filterField.val(options.filterLabel);
				
				
				clearFilterIcon.on("click", function(e) {
					filterField.val(options.filterLabel);
					//stop bubbling in order to avoid closing the list
					e.stopPropagation(); 
					 //show all items
					clearFilter(options);
					});
				
			}
		},
		destroy : function(options){
			JWic.$('ctrl_'+options.controlId).find("#showTree").unbind('click');
			var filterField = JWic.$('search_' + options.controlId);
			if (filterField){
				filterField.unbind("click").unbind("keyup").unbind('focus').unbind('blur');
				JWic.$("cse_" + options.controlId).find(".j-listColSel-clearSearch").unbind("click");
			}
		}
	};
}(jQuery,JWic.util,Cube));
