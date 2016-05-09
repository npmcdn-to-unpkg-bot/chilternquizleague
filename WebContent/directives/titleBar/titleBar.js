mainApp.directive("cqlTitleBar",["$mdSidenav", function($mdSidenav){
	
	return {
		scope:{},
		restrict:'E',
		replace:true,
		transclude:true,
		templateUrl : "/directives/titleBar/titlebar.html",
		link: function(scope, element, attrs, ctrl, transclude){
			scope.toggleRight = function(){
				$mdSidenav('right').toggle();
				$mdSidenav('left').close();};
			
			scope.toggleLeft = function(){$mdSidenav('left').toggle();};
			
			scope.pageMenu = attrs.hasOwnProperty("pageMenu");
		}};
}]);