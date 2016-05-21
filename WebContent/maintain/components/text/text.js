maintainApp.controller('TextListCtrl', getCommonParams(function($scope,ctrlUtil){
	ctrlUtil.makeListFn("text", $scope)
}));

maintainApp.controller('TextDetailCtrl', getCommonParams(function($scope, ctrlUtil) {
	ctrlUtil.makeUpdateFn("text",$scope, this);

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
	
	$scope.tinymceOptions = tinymceOptions;

}));