(function(){

	function sortVenues(venue1, venue2){return venue1.name.localeCompare(venue2.name);};
	
	
mainApp
		.controller(
				'VenuesController',
				[
						'$scope',
						'$interval',
						'viewService',
						'$location',
						cyclingListControllerFactory("venue", sortVenues) ]);
})();