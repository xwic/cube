{//DimensionElementSelector.js
	afterUpdate : function(){
		#if($control.visible)
		Cube.DimensionElementSelector.initialize('$control.controlID',$control.buildJsonOptions());
		#end
	},
	destroy : function(){
		#if($control.visible)
		Cube.DimensionElementSelector.initialize('$control.controlID',$control.buildJsonOptions());
		#end
	}
}