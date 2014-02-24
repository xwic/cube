{//DimensionElementSelector.js
	afterUpdate : function(){
		#if($control.visible)
		var options = $control.buildJsonOptions();
		options.controlId = '$control.controlID';
		Cube.DimensionElementSelector.initialize(options);
		#end
	},
	destroy : function(){
		#if($control.visible)
		var options = $control.buildJsonOptions();
		options.controlId = '$control.controlID';
		Cube.DimensionElementSelector.initialize(options);
		#end
	}
}