(function() {

	function sortVenues(venue1, venue2) {
		return venue1.name.localeCompare(venue2.name);
	}
	;

	mainApp.component('venues', {
	  templateUrl:"/venue/venues.html",
	  controller : "VenuesController",
	  $routeConfig: [
	     {path: '/',    name: 'VenueText',   component: 'venueText'},
	     {path: '/:id',    name: 'VenueDetail',   component: 'venueDetail'},

	  ]
	})
	.component('venueDetail', {
	  templateUrl: '/venue/venue-detail.html',
	  controller: "VenueDetailController",
	  require : {venues : "^venues"}
	})
	.component('venueText', {
	  templateUrl: '/venue/venue-text.html',
	})
	

	
	mainApp.controller('VenuesController', [  '$scope', 'viewService', function($scope, viewService){
			COMMON.configureGroupController("venue", this, $scope, viewService)
	}
	]);
	mainApp.controller("VenueDetailController", ["$scope", '$sce',function($scope, $sce){
		
		  COMMON.configureItemController("venue", this, $scope)
			
			function makeParts(venue){
				var parts=["https://maps.google.com/maps?&q=","", "&output=embed"];
				
				parts[1] = encodeURIComponent((venue.name + " " +venue.address).replace(/\s/g, "+"));
				return parts
			}		
			
			this.linkUrl = function(venue){
						return $sce.trustAsResourceUrl(makeParts(venue).slice(0,2).join());
					}
					
			this.embeddedUrl = function(venue){
						return $sce.trustAsResourceUrl(makeParts(venue).join());
					}

	}]);
	

})();