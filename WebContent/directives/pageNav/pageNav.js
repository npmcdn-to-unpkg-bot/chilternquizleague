mainApp.directive("cqlPageNav",["$mdSidenav", function($mdSidenav){

	return {
		scope:{"title":"@"},
		restrict:'E',
		transclude:true,
		link: function(scope){
			scope.closeLeft = function(){
				$mdSidenav('left').close();};
			scope.classes = "md-subheader md-default-theme"
		},
		templateUrl: "/directives/pageNav/sidenav.html"
	}
}])
