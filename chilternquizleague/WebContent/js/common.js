function cyclingListControllerFactory(type, sortFunction, otherFunctions) {

	var camelName = type.charAt(0).toUpperCase() + type.substr(1);
	var listName = type + "s";
	return function($scope, $interval, viewService, $location) {

		var promise = null;
		
		var parts = $location.path().split("/");
		
		var tail = "/" + parts.slice(2).join("/");
		
		var itemId = parts[1];

		$scope["set" + camelName] = function(item) {

			$interval.cancel(promise);
			$scope[type] = item;
		};

		$scope.$watch(type, function(item) {

			if (item) {
				$location.path("/" + item.id + tail);
			}
		});

		otherFunctions ? otherFunctions($scope, $interval, viewService,
				$location) : null;

		viewService
				.list(
						listName,
						function(items) {
							$scope[listName] = items.sort(sortFunction);
							if (itemId && itemId > 0) {
								for (idx in items) {
									if (items[idx].id == itemId) {
										$scope[type] = items[idx];
									}
								}
							} else {
								var itemIndex = 0;
								$scope[type] = items[0];
								promise = $interval(
										function() {

											$scope[type] = $scope[listName][itemIndex = itemIndex >= (items.length - 1) ? 0
													: (itemIndex + 1)];
										}, 5000);
							}

						});
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
	var listName = options && options.listName ? options.listName : (type + "s");
	var remoteListName = options && options.listName ? options.listName : listName;
	
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