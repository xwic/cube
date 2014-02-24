{//CubeViewer.js
	afterUpdate : function(){
		var options = $control.buildJsonOptions();
		options.controlID = '$control.controlID';
		Cube.CubeViewer.initialize(options);
	},
	destroy : function(){
		var options = $control.buildJsonOptions();
		options.controlID = '$control.controlID';
		Cube.CubeViewer.destroy(options);
	}
}