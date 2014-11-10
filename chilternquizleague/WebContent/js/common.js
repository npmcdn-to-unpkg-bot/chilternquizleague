var b = document.documentElement;
  b.setAttribute('data-useragent',  navigator.userAgent);
  b.setAttribute('data-platform', navigator.platform );
  b.className += ((!!('ontouchstart' in window) || !!('onmsgesturechange' in window))?' touch':'');

function cyclingListControllerFactory(type, otherFunctions) {

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
			
			findItem(id)
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

/**
 * Factory function to create list and selection artefacts for e.g. a list of seasons
 * and accompanying select box.
 * @param type
 * @param $scope
 * @param options
 * @param viewService
 * @returns {Function} Expects an id value as parameter
 */
function listAndSelection(type, $scope, viewService, options){
	
	
	var camelName = type.charAt(0).toUpperCase() + type.substr(1);
	var setName = "set" + camelName;
	var localOptions = {listName:type+"s", remoteListName:type+"s"};
	
	angular.extend(localOptions,options);
	
	var listName = localOptions.listName;
	var remoteListName = localOptions.remoteListName;
	
	return function (id){
		
		if(id){
		
			$scope[setName] = function(item){$scope[type] = item;};
			
			viewService.list(remoteListName, function(items) {
		
			$scope[listName] = items;

			for (idx in items) {

				if (items[idx].id == id) {
					$scope[setName](items[idx]);
					return;
				}

				$scope[setName](items ? items[0] : null);
			}});};};
}