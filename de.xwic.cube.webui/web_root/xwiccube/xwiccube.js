var currentOpenCtrlId = null;
var xcube_TreeReq = null;
var insideClick = false;
/**
 * Open the tree for selection.
 * 
 * @param ctrlId
 * @return
 */
function xcube_showTree(ctrlId, currentSelection) {

	if (currentOpenCtrlId != null) {
		xcube_closeTree();
	}

	var elm = document.getElementById("xcubeLeafselBox_" + ctrlId);
	var elmTbl = document.getElementById("xcubeLeafselTbl_" + ctrlId);
	if (elm && elmTbl) {
		insideClick = true; // prevents immidiate closing
		currentOpenCtrlId = null;
		xcube_alignElement(elm, elmTbl);
		xcube_hideElement("SELECT", elm);
		elm.style.display = "inline";
		elm.innerHTML = "Loading......";

		if (!elm.xwc_expanded) {
			elm.xwc_expanded = new Array();
		}

		// add selections list
//		if (!elm.xwc_selection_flags) {
		elm.xwc_selection_flags = currentSelection;
//		}

		// install "close" hook
		document.onclick = function() {
			// debugger;
			if (!insideClick) {
				xcube_closeTree();
			}
			insideClick = false;
		}
		elm.onclick = function innerClick() {
			insideClick = true;
		}
		currentOpenCtrlId = ctrlId;

		// load the data...
		if (!elm.xwc_data) {
			xcube_TreeReq = jWic_sendResourceRequest(ctrlId,
					xcube_processResponse);
		} else {
			xcube_renderTree(elm);
		}

	} else {
		alert("selbox element for " + ctrlId + " not found on page.");
	}
}

/**
 * Process server response.
 * 
 * @return
 */
function xcube_processResponse() {
	if (currentOpenCtrlId != null && xcube_TreeReq != null) {
		if (xcube_TreeReq.readyState == 4 && xcube_TreeReq.status == 200) {
			var resultString = xcube_TreeReq.responseText;
			try {
				var result = JSON.parse(resultString);
				var elm = document.getElementById("xcubeLeafselBox_"
						+ currentOpenCtrlId);
				elm.xwc_data = result;
				var dims = new Array();

				var selections = result.selection;
				for ( var i = 0; i < selections.length; i++) {

					var key = "/" + selections[i];
					var idx = key.lastIndexOf('/');
					while (idx != -1) {
						elm.xwc_expanded[key] = "+";
						key = key.substring(0, idx);
						var idx = key.lastIndexOf('/');
					}
					elm.xwc_expanded["/" + result.dimension] = "+"; // initialy
					// expand root
					// selections will change internally and submit on press ok.
					if (selections[i] != ""){
						dims.push(selections[i]);
					}
				}
				elm.xwc_selection_flags = dims;
				xcube_renderTree(elm, "unchecked");
			} catch (e) {
				alert("Error parsing data:" + e);
			}
		}
	}
}

function xcube_renderTree(elm, state) {
	if (!state){
		state = "unchecked";
	}	
	var isMultiSelection = elm.xwc_data.isMulti;
	if (isMultiSelection) {
		xcube_renderTreeMulti(elm, state);
	} else {
		xcube_renderTreeSingle(elm);
	}
}

function xcube_renderTreeSingle(elm) {

	var data = elm.xwc_data;
	var expandedKeys = elm.xwc_expanded;
	var code = "<table cellspacing=0 cellpadding=0 class=xcube-treetbl>";
	var selMode = elm.attributes["xwcSelectionMode"].value;

	var lastTrack = new Array();
	lastTrack[0] = true;
	// root element (all)
	code += "<tr><td><table cellspacing=0 cellpadding=0><tr><td>";
	code += "<td";
	if (data.selection == "") {
		code += " class=\"selected\"";
	}
	code += "><a href=\"#\" onClick=\"";
	if (selMode != "leaf") {
		code += "xcube_selectElement('/')";
	}
	code += ";return false;\">"
	code += "- All -";
	code += "</a></td></tr></table></td></tr>";

	code += xcube_renderTreeChilds(0, expandedKeys, lastTrack, "/"
			+ data.selection, "", data.elements, selMode);

	code += "</table>";

	elm.innerHTML = code;

}

function xcube_renderTreeChilds(lvl, expandedKeys, lastTrack, selection, path,
		elements, selMode) {

	var imgPath = _contextPath + "/xwiccube/images/";

	var code = "";
	for ( var i = 0; i < elements.length; i++) {
		var de = elements[i];
		var isLeaf = de.elements.length == 0;
		var isLast = (i + 1) >= elements.length;
		var myPath = path + "/" + de.key;
		var isExpanded = expandedKeys[myPath] == "+";

		var action = "";
		code += "<tr><td><table cellspacing=0 cellpadding=0><tr>";
		// indention
		for ( var idl = 0; idl < lvl; idl++) {
			var imgName = lastTrack[idl] ? "blank.png" : "I.png";
			code += "<td width=19><img src=\"" + imgPath + imgName
					+ "\" width=19 height=16></td>";
		}
		var imgName = (isLast ? "L" : "T");
		if (!isLeaf) {
			action = (isExpanded ? "-" : "+");
			imgName += (isExpanded ? "minus" : "plus");
		}
		code += "<td width=19";
		if (action != "") {
			code += " class=\"xcube_tree_actionnode\" onclick=\"xcube_treeNodeToggle('"
					+ myPath + "', '" + action + "')\")";
		}
		code += "><img src=\"" + imgPath + "/" + imgName
				+ ".png\" width=19 height=16>";
		code += "</td>";

		code += "<td";
		if (myPath == selection) {
			code += " class=\"selected\"";
		}
		code += "><a href=\"#\" onClick=\"";
		if (selMode == "leaf" && !isLeaf) {
			code += "xcube_treeNodeToggle('" + myPath + "', '" + action
					+ "')\")";
		} else {
			code += "xcube_selectElement('" + myPath + "')";
		}
		code += ";return false;\">"
		if (de.title && de.title != "") {
			code += de.title;
		} else {
			code += de.key;
		}
		code += "</a></td></tr></table></td></tr>";
		lastTrack[lvl] = isLast;
		if (!isLeaf && isExpanded) {
			code += xcube_renderTreeChilds(lvl + 1, expandedKeys, lastTrack,
					selection, myPath, de.elements, selMode);
		}
	}

	return code;
}

function xcube_renderTreeMulti(elm, state) {

	var data = elm.xwc_data;
	var expandedKeys = elm.xwc_expanded;
	var code = "<table cellspacing=0 cellpadding=0 class=xcube-treetbl>";
	var selMode = elm.attributes["xwcSelectionMode"].value;
	// if no elements nothing is selected - root is the selection
	var selection = elm.xwc_selection_flags;
	var imgPath = _contextPath + "/xwiccube/images/";
	
	var lastTrack = new Array();
	lastTrack[0] = true;
	// root element (all)
	code += "<tr><td><table cellspacing=0 cellpadding=0><tr><td>";
	
	var checkImgName = "unchecked";

	if (elm.xwc_selection_flags.length < 1 && state == "unchecked") {
		checkImgName = "checked";
	}

	if (selMode != "leaf") {
		code += "<td width=19";
		code += " class=\"xcube_tree_actionnode\" onclick=\"xcube_multiCheckElem('', '" + checkImgName + "')\")";

		code += "><img src=\"" + imgPath + "/" + checkImgName
				+ ".gif\" width=19 height=16>";
		code += "</td>";
	}
	
	code += "<td";
	if (checkImgName = "checked") {
		code += " class=\"selected\"";
	}
	code += "><a href=\"#\">";
	code += "- All -";
	code += "</a></td></tr></table></td></tr>";
	
// code += "<td";
// if (elm.xwc_selection_flags.length < 1) {
// code += " class=\"selected\"";
// }
// code += ">"
// code += "- All test-";
// code += "</td></tr></table></td></tr>";

	code += xcube_renderTreeChildsMulti(0, expandedKeys, lastTrack, selection,
			"", data.elements, selMode, state);

	if (elm.xwc_selection_flags.length > 0 ) {
		code += placeButton();
	}else{
		if (state == "unchecked"){
			code += placeButton();
		}
	}
	code += "</table>";
	
	elm.innerHTML = code;

}

function xcube_renderTreeChildsMulti(lvl, expandedKeys, lastTrack, selection,
		path, elements, selMode, state) {

	var imgPath = _contextPath + "/xwiccube/images/";

	var code = "";
	for ( var i = 0; i < elements.length; i++) {
		var de = elements[i];
		var isLeaf = de.elements.length == 0;
		var isLast = (i + 1) >= elements.length;
		var myPath = path + "/" + de.key;
		myPath = myPath.replace('\'', '\\\'');
		var isExpanded = expandedKeys[myPath] == "+";

		
		// checked img and state
		var trimPath = myPath.substring(1);
		var checkImgName = "unchecked";

		if (selection.length < 1 && state == "unchecked"){
			checkImgName = "checked";
		}else{
			for ( var idxSelection = 0; idxSelection < selection.length; idxSelection++) {
				if (beginsWith(selection[idxSelection], trimPath)) {
					checkImgName = "checked";
					break;
				}
			}
		}
		
		var action = "";
		code += "<tr><td><table cellspacing=0 cellpadding=0><tr>";
		// indention
		for ( var idl = 0; idl < lvl; idl++) {
			var imgName = lastTrack[idl] ? "blank.png" : "I.png";
			code += "<td width=19><img src=\"" + imgPath + imgName
					+ "\" width=19 height=16></td>";
		}
		var imgName = (isLast ? "L" : "T");
		if (!isLeaf) {
			action = (isExpanded ? "-" : "+");
			imgName += (isExpanded ? "minus" : "plus");
		}
		code += "<td width=19";
		if (action != "") {
			code += " class=\"xcube_tree_actionnode\" onclick=\"xcube_treeNodeToggle('"
					+ myPath + "', '" + action + "', '" + checkImgName + "')\")";
		}
		code += "><img src=\"" + imgPath + "/" + imgName
				+ ".png\" width=19 height=16>";
		code += "</td>";

		

		
		
		if (selMode != "leaf" || isLeaf) {
			code += "<td width=19";
			code += " class=\"xcube_tree_actionnode\" onclick=\"xcube_multiCheckElem('"
					+ myPath + "', '" + checkImgName + "')\")";

			code += "><img src=\"" + imgPath + "/" + checkImgName
					+ ".gif\" width=19 height=16>";

			// code += "<input type=\"checkbox\" id=\""+ myPath + "_1\"
			// name=\""+ myPath + "_1\" onclick=\"xcube_multiCheckElem('"+
			// myPath + "_1');\" />";
			code += "</td>";

		}

		code += "<td";
		if (checkImgName == "checked") {
			code += " class=\"selected\"";
		}
		code += "><a href=\"#\" onClick=\"";
		code += "xcube_treeNodeToggle('" + myPath + "', '" + action + "', '" + checkImgName + "')\")";
		code += ";return false;\">"
		if (de.title && de.title != "") {
			code += de.title;
		} else {
			code += de.key;
		}
		code += "</a></td></tr></table></td></tr>";
		lastTrack[lvl] = isLast;
		if (!isLeaf && isExpanded) {
			code += xcube_renderTreeChildsMulti(lvl + 1, expandedKeys,
					lastTrack, selection, myPath, de.elements, selMode, state);
		}
	}

	return code;
}

/**
 * Fire selection event.
 * 
 * @param elmId
 * @return
 */
function xcube_selectElement(elmId) {
	jWic().fireAction(currentOpenCtrlId, 'selection', elmId.substring(1));
	xcube_closeTree();
}

/**
 * Toggle tree node.
 * 
 * @param key
 * @param state
 * @return
 */
function xcube_treeNodeToggle(key, state, checkState) {

	if(checkState == "checked"){
		checkState = "unchecked";
	}else{
		checkState = "checked";
	}
	
	if (currentOpenCtrlId != null) {
		var elm = document.getElementById("xcubeLeafselBox_"
				+ currentOpenCtrlId);
		if (elm) {
			elm.xwc_expanded[key] = state;
			xcube_renderTree(elm, checkState); // rerender
		}
	}
}

/**
 * close the currently open tree.
 * 
 * @return
 */
function xcube_closeTree() {
	if (currentOpenCtrlId != null) {
		var elm = document.getElementById("xcubeLeafselBox_"
				+ currentOpenCtrlId);
		if (elm) {
			elm.style.display = "none";
			elm.innerHTML = "...";
			xcube_showElement("SELECT", elm);
			// reinit the selections
			elm.xwc_selection_flags = new Array();
			var selections = elm.xwc_data.selection;
			for ( var i = 0; i < selections.length; i++) {
				elm.xwc_selection_flags.push(selections[i]);
			}
		}
		currentOpenCtrlId = null;
	}
}

function xcube_alignElement(elmSrc, elmAlignTo) {

	var fixedX = -1;
	var fixedY = -1;
	var leftpos = 0
	var toppos = 0
	var fixedWidth = 200;
	var fixedHeight = 300;
	var aTag = elmAlignTo
	do {
		aTag = aTag.offsetParent;
		leftpos += aTag.offsetLeft;
		toppos += aTag.offsetTop;
	} while (aTag.tagName != "BODY");

	var x = fixedX == -1 ? elmAlignTo.offsetLeft + leftpos
			: fixedX == -2 ? elmAlignTo.offsetLeft + leftpos - fixedWidth
					: fixedX
	if (x < 1) {
		x = 1
	}
	elmSrc.style.left = x + "px";
	var y = fixedY == -1 ? elmAlignTo.offsetTop + toppos
			+ elmAlignTo.offsetHeight + 2 : fixedY == -2 ? elmAlignTo.offsetTop
			+ toppos + 2 - fixedHeight : fixedY
	if (y < 1) {
		y = 1
	}
	elmSrc.style.top = y + "px";

}

function xcube_hideElement(elmID, overDiv) {
	if (document.all) {

		var dim = $(overDiv).getDimensions();
		for ( var i = 0; i < document.all.tags(elmID).length; i++) {
			obj = document.all.tags(elmID)[i];
			if (!obj || !obj.offsetParent) {
				continue;
			}

			// Find the element's offsetTop and offsetLeft relative to the BODY
			// tag.
			objLeft = obj.offsetLeft;
			objTop = obj.offsetTop;
			objParent = obj.offsetParent;

			while (objParent.tagName.toUpperCase() != "BODY") {
				objLeft += objParent.offsetLeft;
				objTop += objParent.offsetTop;
				objParent = objParent.offsetParent;
			}

			objHeight = obj.offsetHeight;
			objWidth = obj.offsetWidth;

			if ((overDiv.offsetLeft + dim.width) <= objLeft)
				;
			else if ((overDiv.offsetTop + dim.height) <= objTop)
				;
			else if (overDiv.offsetTop >= (objTop + objHeight))
				;
			else if (overDiv.offsetLeft >= (objLeft + objWidth))
				;
			else {
				obj.style.visibility = "hidden";
			}
		}
	}
}

/*
 * unhides <select> and <applet> objects (for IE only)
 */
function xcube_showElement(elmID) {
	if (document.all) {
		for ( var i = 0; i < document.all.tags(elmID).length; i++) {
			obj = document.all.tags(elmID)[i];

			if (!obj || !obj.offsetParent) {
				continue;
			}

			obj.style.visibility = "";
		}
	}
}

// check/uncheck elements
function xcube_multiCheckElem(key, state) {

	var currKey = key;
	if (currentOpenCtrlId != null) {
		var elm = document.getElementById("xcubeLeafselBox_"
				+ currentOpenCtrlId);
		if (elm) {
			// old
			var oldFlags = elm.xwc_selection_flags;

			// split on check/uncheck cases.
			if (state == "checked") {
				if (key == ""){
					elm.xwc_selection_flags = new Array();
				}else{
					if (oldFlags.length < 1){
						// push all in list excluding path to this element
						elm.xwc_selection_flags = pushAllButKey(elm.xwc_data.elements, key, elm.xwc_selection_flags, "")
					}else{	
						
						oldFlags = removeSelection(key, oldFlags);
						
						var idx = key.lastIndexOf('/'); 
						key = key.substring(0, idx);
						idx = key.lastIndexOf('/');
						while (idx >= 0) {
							oldFlags = removeParentSelection(key, oldFlags, elm, currKey);
							key = key.substring(0, idx);
							idx = key.lastIndexOf('/');
						}
						elm.xwc_selection_flags = oldFlags;
					}
				}				
			}else{
				if (key == ""){
					elm.xwc_selection_flags = new Array();
				}else{
					// add selection
					elm.xwc_selection_flags = addElement(key, oldFlags, elm);
				}
			}
			xcube_renderTreeMulti(elm, state); // rerender
		}
	}
}


// add element to selections
function addElement(key, oldFlags, elm){
	var newSelection = oldFlags;
	
	// check if all leafs are selected to select root only
	var currKey = key;
	var data = elm.xwc_data;
	var rootElements = data.elements;
	
	
	var idx = currKey.lastIndexOf('/'); 
	key = currKey;
	currKey = currKey.substring(0, idx);
	
	while (idx >= 0) {
		
		var de = getDimensionElement(currKey, "", rootElements);
			
		if(de){
			var elements = de.elements;
		}else{
			var elements = rootElements;
		}
//		var fullSelection = newSelection;
//		fullSelection.push(key.substring(1));
		
		// check if all childs are selected
		var elemPath = "";
		var isAll = true;
		for ( var i = 0; i < elements.length; i++){
			elemPath = currKey + "/" + elements[i].key;
			if  (elemPath != key){
				if (!listContains(newSelection, elemPath.substring(1))){
					isAll = false;
					break;
				}
			}
		}
		
		if(isAll){
			// remove all from selection
			newSelection = removeSelection(currKey, newSelection);
			
		}else{
			// add element
			if(!listContains(newSelection, key.substring(1))){
				// clean up first of some remaining childs;
				newSelection = removeSelection(key, newSelection);
				newSelection.push(key.substring(1));
			}
			break;
		}
		key = currKey;
		idx = currKey.lastIndexOf('/');
		currKey = currKey.substring(0, idx);
		
		
	}
	
	return newSelection;
	
	
}


// remove parent element
function removeParentSelection(key, oldSelections, elm, child){
	
	// if the parent was selected
	var exists = false;
	newSelection = new Array();
	for ( var i = 0; i < oldSelections.length; i++){
		if (key.substring(1) != oldSelections[i]){
			newSelection.push(oldSelections[i]);
		}else{
			exists = true;
		}
	
	}
	var parentKey = key;
	
	// try check upper parents
	if (!exists){
		var idx = parentKey.lastIndexOf('/'); 
		while (idx >= 0) {
			if (listContains(oldSelections, parentKey.substring(0, idx).substring(1))){
				exists = true;
				break;
			}	
			parentKey = parentKey.substring(0, idx);
			idx = parentKey.lastIndexOf('/');
		}
	
	}
	
	if (exists){
		// check for this parent if we need to add back the other branches
		var data = elm.xwc_data;
		
		var rootElements = data.elements;
		
		var element = getDimensionElement(key, "", rootElements);
		
		var elements = element.elements;
		
		for ( var i = 0; i < elements.length; i++){
			var elementPath = key + "/" + elements[i].key;
			
			if(!(beginsWith(elementPath, child) || listContains(newSelection, elementPath.substring(1)))){
				newSelection.push(elementPath.substring(1));
			}
		}
	}
	
	
	return newSelection;
}


// push all but specified key not
function pushAllButKey(rootElements, key, newElements, parentPath){
	
	for(var i = 0; i < rootElements.length; i++){
		var isLeaf = rootElements[i].elements.length == 0
		var currentPath = parentPath + "/" + rootElements[i].key
		if (beginsWith(currentPath, key)){
			// add sub elements if any
			if(!isLeaf && currentPath != key){
				newElements = pushAllButKey(rootElements[i].elements, key, newElements, currentPath);
			}
		}else{
			newElements.push(currentPath.substring(1));
		}
		
	}
	return newElements;
}

// remove child elements for a parent
function removeSelection(key, oldSelections){
	
	newSelection = new Array();
	
	if (key == ""){
		return newSelection;
	}	
	for ( var i = 0; i < oldSelections.length; i++){
		if (key.substring(1) != oldSelections[i] && !beginsWith(key.substring(1), oldSelections[i])){
			newSelection.push(oldSelections[i]);
		}
		
	}
	return newSelection;
}

// return dimension element from data
function getDimensionElement(path, parentPath, elements){

	for (var i = 0; i < elements.length; i++){
		var currentElementPath = parentPath + "/" + elements[i].key;
		if (path == currentElementPath){
			return elements[i];
		}else{	
			if (beginsWith(currentElementPath.substring(1), path.substring(1))){
				return getDimensionElement(path, currentElementPath, elements[i].elements);
			}
		}
	}
	return null;
}

// for strings start with
function beginsWith(startWith, fullStr) {
	
	//we have dimension element paths here
	//check equality of roots. Example: "PS" and ("PS Mgr", "PS Mgr/xxx" )should not match
	var idx = fullStr.substr(startWith.length).indexOf("/");

	if (idx == 0){
		return fullStr.substr(0, startWith.length) == startWith;
	}else{
		return startWith == fullStr;
	}
	
}

// place the submit button
function placeButton(){
	code = "";
	code += "<tr><td allign = \"right\">";
	code += "<button allign = \"right\" onclick=\"xcube_selectElement_multi()\">OK</button>";
	code += "</td></tr>";
	return code;
}

/**
 * Fire selection event.
 * 
 * @param elmId
 * @return
 */
function xcube_selectElement_multi() {
	if (currentOpenCtrlId != null) {
		var elm = document.getElementById("xcubeLeafselBox_"
				+ currentOpenCtrlId);
		if (elm) {
			
			var paths = "";
			for(var i = 0; i < elm.xwc_selection_flags.length; i++){
				
				if (i !=0 ){
					paths += "##";
				}
				paths += elm.xwc_selection_flags[i];
			}
			
			
			jWic().fireAction(currentOpenCtrlId, 'selection', paths);
		}
	}
	xcube_closeTree();
}

// true if the array contains the given element
function listContains(elements, path){
	for (var i = 0; i < elements.length; i++){
		if(elements[i] == path){
			return true;
		}	
	}
	return false;
}


