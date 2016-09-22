Cube.DimensionElementSelector = (function($,util,Cube){
	"use strict";
	var buildHtml, buildTree, buildMultiSelectKey, bindNode, intialOpen, applyFilter,
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
						c.state(val);//set state but don't fire event if state is UNDEFINED
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
					return acc || (c.state() !== child.state());
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

	var filterPlaceholder = 'Filter';
	
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
	 * Filters the displayed node titles over the specified input. The filtering works only of the first level of nodes
	 */
	applyFilter = function applyFilter(controlId) {
		var filterField = JWic.$('search_' + controlId);
		var val = jQuery.trim(filterField.val()).toLowerCase();
		var clearFilter = JWic.$("cse_" + controlId);
		if (val.length != 0) {
			clearFilter.find(".j-listColSel-clearSearch").show();
		} else {
			clearFilter.find(".j-listColSel-clearSearch").hide();
		}
		
		
		var base = JWic.$('ctrl_'+controlId);
		var visibleRows = [];
	
		//search for nodes that are matching the filter and store them in matchedRows
		base.find('li a#title').each(function(i,item) {
			var row = jQuery(item);
			var title = row.text();
			
			//only the template node has empty text
			if (title.length == 0){
				return;
			} 
			
			//always show the all element if exists
			if ('- All -'=== title){
				visibleRows.push($(row).parent()[0]);
				return;
			}
			
			//if the filter is cleared show all nodes
			if (val.length == 0 || val === filterPlaceholder.toLowerCase()) {
				visibleRows.push($(row).parent()[0]);
			} else {
				//if the filter value was found store the li dom element 
				if (title && title.toLowerCase().indexOf(val) != -1) {
					
					if (visibleRows.indexOf($(row).parent()[0]) == -1){
						visibleRows.push($(row).parent()[0]);
					}
					
					//add all children on all levels into the visibility list
					row.parent().find('li a#title').each(function(i,item) {
						if (visibleRows.indexOf($(item).parent()[0]) == -1){
							visibleRows.push($(item).parent()[0]);
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
						if (visibleRows.indexOf(parent[0]) == -1){
							visibleRows.push(parent[0]);
						}
						parent = parent.parent()
					}
					
				} 
			}
			
		});
		
		//iterate again thru all nodes and show only the ones marked as visible
		base.find('li a#title').each(function(i,item) {
			var prnt = $(item).parent()[0];
			if (visibleRows.indexOf(prnt) != -1){
				$(prnt).show();
			}else{
				$(prnt).hide();
			}
		});
	};
	
	//exports
	return {
		initialize : function(options){
			var control = JWic.$('ctrl_'+options.controlId),
				build = buildHtml(control,options), searchFilter = applyFilter;
			control.find("#showTree").on('click', build);
			var filterField = JWic.$('search_' + options.controlId);
			if (filterField){
				filterField.on("keyup", function(e) { 
					var val = this.value; 

					if (val.length == 0){
						control.find('li a#title').parent().show();
						control.find('#node-template').hide();
					} else if (val.length > 2){
						searchFilter(options.controlId);
					}
				});
				filterField.on("click", function(e) {e.stopPropagation();});
				
				/** On IE 11 the placeholder does not work so we have to simulate with focus/blur handlers*/
				filterField.on("focus", function(e) {
					if (this.value == filterPlaceholder){
						this.value = '';	
					}
				});
				
				filterField.on("blur", function(e) {
					if (this.value == ''){
						this.value = filterPlaceholder;	
					}
				});
				
				filterField.val(filterPlaceholder);
				
				var clearFilter = JWic.$("cse_" + options.controlId);
				clearFilter.on("click", function(e) {
					filterField.val(filterPlaceholder);
					e.stopPropagation(); 
					
					 control.find('li a#title').parent().show();
					 control.find('#node-template').hide();
					 clearFilter.find(".j-listColSel-clearSearch").hide();
					});
				clearFilter.find(".j-listColSel-clearSearch").hide();
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
