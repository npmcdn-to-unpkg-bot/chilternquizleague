mainApp.filter("htmlify", ["$sce", function($sce){return function(text){
	
	return text ?  $sce.trustAsHtml(text.replace(/^<p>/,"").replace(/<\/p>$/,"")) : "";
};}]);

mainApp.filter("lineBreaks", [function(){return function(text){
	return text ?  text.replace(/\n/g, "<br/>") : "";
};}]);

