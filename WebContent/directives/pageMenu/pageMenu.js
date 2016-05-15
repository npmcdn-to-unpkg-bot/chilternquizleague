mainApp.directive("cqlPageMenu",function(){
	
	return {
		restrict:'E',
		replace:true,
		template : "<span><md-button class='md-icon-button' ng-click='toggleLeft()' aria-label='Page menu'><md-icon md-svg-src='/images/icons/ic_more_horiz.svg'></md-icon><md-tooltip>Page menu</md-tooltip></md-button></span>"};
});