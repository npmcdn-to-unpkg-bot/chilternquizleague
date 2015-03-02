var b = document.documentElement;
  b.setAttribute('data-useragent',  navigator.userAgent);
  b.setAttribute('data-platform', navigator.platform );
  b.className += ((!!('ontouchstart' in window) || !!('onmsgesturechange' in window))?' touch':'');

function listControllerFactory(type, otherFunctions) {

	var camelName = type.charAt(0).toUpperCase() + type.substr(1);
	var listName = type + "s";
	return function($scope, $interval, viewService, $location, $stateParams, $sce) {

		$scope["set" + camelName] = function(item) {
			$scope[type] = item;
		};


		otherFunctions ? otherFunctions($scope, $interval, viewService,
				$location, $stateParams, $sce) : null;

		viewService
				.list(
						type,
						function(items) {
							$scope[listName] = items;
						});
	
		
		$scope.setCurrentItem = function(){
			
			var id = $stateParams.itemId;
			
			function findItem(id) {
			for (idx in $scope[listName]) {

				if ($scope[listName][idx].id == id) {
					$scope["set" + camelName]($scope[listName][idx]);
					break;
				}
			}
		}
				
		if($scope[listName]){
			
			findItem(id);
		}
		else{
			var destr = $scope.$watchCollection(listName, function(items) {
				if(items && items.length > 0){
					findItem(id);
					destr();
				}
			});

			
		}
		}
	};
}

