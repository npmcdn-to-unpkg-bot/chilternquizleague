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
			viewService.list("venue", function(venues){$scope.venues = venues})
			
			this.setId = function(id){$scope.venueId = id}
			this.watch = function(name, lstn){return $scope.$watch(name, lstn)}
			
			$scope.$watchGroup(["venues", "venueId"], function(values){
				if(values[0] && values[1]){
					$scope.venue = values[0].filter(function(v){return v.id == values[1]}).pop()
				}
			})
	}
	]);
	mainApp.controller("VenueDetailController", ["$scope", 'viewService','$sce',function($scope, viewService, $sce){
			var $ctrl = this
			var derefs = []
			this.$routerOnActivate = function(next, previous) {
				$ctrl.venues.setId(next.params.id);
			}
			
			this.$onInit = function(){
				derefs.push($ctrl.venues.watch("venue", 
						function(venue){
					$ctrl.venue = venue
					}))
			}
			
			this.$onDelete = function(){
				derefs.forEach(function(i){i()})
				derefs = []
			}
			
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