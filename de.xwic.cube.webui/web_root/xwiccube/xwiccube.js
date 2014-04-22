/**
 * Cube js utils
 * depends on JWic.util
 */
var Cube = (function(util){
	var defineProp, defineObservable,tmpl
		map = util.map,
		reduce = util.reduce;
	tmpl = function tmpl(control, templateId, data){
		var prop,
			template = control.find(templateId).clone();
		template.removeAttr("style").removeAttr('id');
		if(data){
			for(prop in data){
				if(data.hasOwnProperty(prop)){
					template.find('#'+prop).text(data[prop]);
				}
			}
		}
		return template;
	};
	defineProp = function(object, propName){
		var val;
		object[propName] = function(value){
			var oldVal;
			if(value == null){//its a getter if null
				return val; 
			}//else its a setter
			oldVal = val;
			val = value;
			if(value !== oldVal && 'function' === typeof object['firePropertyChanged']){
				object.firePropertyChanged(propName,val,oldVal);//fire event listeners if applicable
			}
			return object;
		};	
		return object[propName]; 
	};
	defineObservable = function(object,observableName){
		var listeners = {},
			eventName = observableName.charAt(0).toUpperCase() + observableName.substr(1); //capitalize
		
		object['fire'+eventName] = function(){
			var args = arguments,
				l =listeners[observableName] || []; 
			map(l,function(o){
				o.apply(object,args);
			});
			return object;
		};
		object.on = object.on || function(what,listener){
			listeners[what] = listeners[what] || [];
			listeners[what].push(listener);
			return object;
		}
		object.unbind = object.unbind || function(what,listener){
			if(listener == null){
				listeners[what] = null;
				return object;
			}
			var index = listeners[what].indexOf(listener);
			listeners[what].splice(index,1);
			return object;
		};
	};
	return {
		defineObservable : defineObservable,
		defineProp : defineProp,
		tmpl : tmpl
	};
}(JWic.util));
