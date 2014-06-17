var maintainApp = angular.module('maintainApp', ['ngRoute']);

maintainApp.config(['$routeProvider',
                    function($routeProvider) {
                      $routeProvider.
                        when('/venues', {
                          templateUrl: 'venue/venue-list.html',
                          controller: 'VenueListCtrl'
                        }).
                        when('/venues/:venueId', {
                          templateUrl: 'venue/venue-detail.html',
                          controller: 'VenueDetailCtrl'
                        }).
                        otherwise({
                          redirectTo: ''
                        });
                    }]);



maintainApp.controller('VenueListCtrl', ['$scope', '$http',function($scope,$http){
	
	$http.get("jaxrs/venue-list", {
		"responseType" : "json"
	}).success(function(ret) {
		$scope.venues = ret;
	});
}]);

maintainApp.controller('VenueDetailCtrl', ['$scope', '$http',function($scope,$http){
	
	$scope.master={};
	
	
	$http.get("jaxrs/venue/"+document.location.hash.split("/").pop(), {
		"responseType" : "json"
	}).success(function(ret) {
		$scope.master = ret;
		
		$scope.reset();
	});
	
	$scope.update = function(venue){
		
		$scope.master = angular.copy(venue);
		$http.post("jaxrs/venue", $scope.master);
		document.location = "#venues";
	};
	
	$scope.reset = function(){
		$scope.venue = angular.copy($scope.master);
	};
	
	
}]);