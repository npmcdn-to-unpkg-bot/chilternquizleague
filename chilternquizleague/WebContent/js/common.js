var b = document.documentElement;
  b.setAttribute('data-useragent',  navigator.userAgent);
  b.setAttribute('data-platform', navigator.platform );
  b.className += ((!!('ontouchstart' in window) || !!('onmsgesturechange' in window))?' touch':'');

function cyclingListControllerFactory(type, sortFunction, otherFunctions) {

	var camelName = type.charAt(0).toUpperCase() + type.substr(1);
	var listName = type + "s";
	return function($scope, $interval, viewService, $location, $routeParams) {

		var promise = null;
		
		var parts = $location.path().split(/\//);
		var root = parts[1];
		
		var itemId = $routeParams.itemId;
		var template = $routeParams.template;

		$scope["set" + camelName] = function(item, keepCycling) {

			if(!keepCycling){$interval.cancel(promise);}
			$scope[type] = item;
		};

		$scope.$watch(type, function(item) {

			if (item) {
				//$location.path("/" + root + "/" + item.id + (template ? ("/" + template) : ""));
			}
		});

		otherFunctions ? otherFunctions($scope, $interval, viewService,
				$location, $routeParams) : null;

		viewService
				.list(
						type,
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

											$scope["set" + camelName]( $scope[listName][itemIndex = itemIndex >= (items.length - 1) ? 0
													: (itemIndex + 1)], true);
											//$location.path("/" + root + "/" + $scope[type].id + (template ? ("/" + template) : ""));
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