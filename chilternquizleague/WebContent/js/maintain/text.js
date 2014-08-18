maintainApp.controller('TextListCtrl', getCommonParams(makeListFn("text")));

maintainApp.controller('TextDetailCtrl', getCommonParams(function($scope,
		entityService, $routeParams, $rootScope, $location) {
	makeUpdateFn("text")($scope, entityService, $routeParams,
			$rootScope, $location);

	$scope.addEntry = function() {
		var entry = {
			name : "",
			text : ""
		};
		$scope.text.entries.push(entry);
		$scope.currentEntry = entry;
	};

	$scope.setCurrentEntry = function(entry) {
		$scope.currentEntry = entry;
	};
	
	$scope.tinymceOptions={};

}));