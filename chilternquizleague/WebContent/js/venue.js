(function() {

	function sortVenues(venue1, venue2) {
		return venue1.name.localeCompare(venue2.name);
	}
	;

	mainApp.config([ '$routeProvider', '$locationProvider',
			function($routeProvider, $locationProvider) {
				$routeProvider.when('/venues/:itemId?/:template?', {
					templateUrl : '/venue/venues.html'
				});
			} ]);

	mainApp.controller('VenuesController', [ '$scope', '$interval',
			'viewService', '$location','$routeParams',
			cyclingListControllerFactory("venue", sortVenues) ]);
})();