function cyclingListControllerFactory(type, sortFunction, otherFunctions) {

	var camelName = type.charAt(0).toUpperCase() + type.substr(1);
	var listName = type + "s";
	return function($scope, $interval, viewService, $location) {

		var promise = null;
		var itemId = $location.path().substr(1);

		$scope["set" + camelName] = function(item) {

			$interval.cancel(promise);
			$scope[type] = item;
		};

		$scope.$watch(type, function(item) {

			if (item) {
				$location.path("/" + item.id);
			}
		});

		otherFunctions ? otherFunctions($scope, $interval, viewService,
				$location) : null;

		viewService
				.list(
						listName,
						function(items) {
							$scope[listName] = items.sort(sortFunction);
							if (itemId) {
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