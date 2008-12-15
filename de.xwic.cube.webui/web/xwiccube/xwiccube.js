
	var currentOpenCtrlId = null;
	var xcube_TreeReq = null;
	var insideClick = false;
	function xcube_showTree(ctrlId) {

		if (currentOpenCtrlId != null) {
			xcube_closeTree();
		}
		
		var elm = document.getElementById("xcubeLeafselBox_" + ctrlId);
		var elmTbl = document.getElementById("xcubeLeafselTbl_" + ctrlId);
		if (elm && elmTbl) {
			insideClick = true; // prevents immidiate closing
			currentOpenCtrlId = null;
			xcube_alignElement(elm, elmTbl);
			elm.style.display = "inline";
			elm.innerHtml = "Loading...";
			
			// install "close" hook
			document.onclick = function () {
				//debugger;
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
			xcube_TreeReq = jWic_sendResourceRequest(ctrlId, xcube_displayResults);
			
		} else {
			alert("selbox element for " + ctrlId + " not found on page.");
		}
	}
	
	function xcube_displayResults() {
		if (currentOpenCtrlId != null && xcube_TreeReq != null) {
			if (xcube_TreeReq.readyState == 4 && xcube_TreeReq.status == 200) {
				var resultString = xcube_TreeReq.responseText;
				try {
					var result = eval(resultString);
					
					alert(result);
					
				} catch (e) {
					alert("Error parsing data:" + e);
				}
			}
		}
	}
	
	/**
	 * close the currently open tree.
	 * @return
	 */
	function xcube_closeTree() {
		if (currentOpenCtrlId != null) {
			var elm = document.getElementById("xcubeLeafselBox_" + currentOpenCtrlId);
			if (elm) {
				elm.style.display = "none";
			}
			currentOpenCtrlId = null;
		}
	}
	
	function xcube_alignElement(elmSrc, elmAlignTo) {
		
		var fixedX = -1;
		var fixedY = -1;
		var	leftpos=0
		var	toppos=0
		var fixedWidth = 200;
		var fixedHeight = 300;
		var aTag = elmAlignTo
		do {
			aTag = aTag.offsetParent;
			leftpos	+= aTag.offsetLeft;
			toppos += aTag.offsetTop;
		} while(aTag.tagName!="BODY");

		var x = fixedX==-1 ? elmAlignTo.offsetLeft	+ leftpos :	fixedX==-2 ? elmAlignTo.offsetLeft + leftpos - fixedWidth : fixedX
		if (x < 1) { x = 1 }
		elmSrc.style.left = x + "px";
		var y = fixedY==-1 ? elmAlignTo.offsetTop + toppos + elmAlignTo.offsetHeight + 2 : fixedY==-2 ? elmAlignTo.offsetTop + toppos + 2 - fixedHeight : fixedY
		if (y < 1) { y = 1 }
		elmSrc.style.top = y + "px";

	}