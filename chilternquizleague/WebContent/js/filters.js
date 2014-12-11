mainApp.filter("htmlify", ["$sce", function($sce){return function(text){
	
	return text ?  $sce.trustAsHtml(text.replace(/^<p>/,"").replace(/<\/p>$/,"")) : "";
};}]);

mainApp.filter("lineBreaks", [function(){return function(text){
	return text ?  text.replace(/\n/g, "<br/>") : "";
};}]);

mainApp.filter('afterNow', function() {
	return function(input, disable) {
		if(disable){
			return input;
		}
		
		var now = new Date().getTime();
		var ret = [];
		for (idx in input) {
			if (input[idx].start >= now) {
				ret.push(input[idx]);
			}
		}

		return ret;
	};
});